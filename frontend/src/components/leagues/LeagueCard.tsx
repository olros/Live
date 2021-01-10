import URLS from 'URLS';
import { ILeagueCompact } from 'types/League';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

// Project
import LinkGradientCard from 'components/layout/LinkGradientCard';

const useStyles = makeStyles((theme) => ({
  card: {
    margin: theme.spacing(1, 0),
  },
}));

export type IProps = {
  league: ILeagueCompact;
};

const LeagueCard = ({ league }: IProps) => {
  const classes = useStyles();
  return (
    <LinkGradientCard className={classes.card} gradientFrom='#8E2DE2' gradientTo='#4A00E0' to={`${URLS.LEAGUES}/${league.id}`}>
      <Typography variant='h3'>{league.name}</Typography>
    </LinkGradientCard>
  );
};

export default LeagueCard;
