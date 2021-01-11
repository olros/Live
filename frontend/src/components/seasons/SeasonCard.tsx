import URLS from 'URLS';
import { ISeasonCompact } from 'types/Season';

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
  leagueId: number;
  season: ISeasonCompact;
};

const SeasonCard = ({ leagueId, season }: IProps) => {
  const classes = useStyles();
  return (
    <LinkGradientCard className={classes.card} gradientFrom='#ff9966' gradientTo='#ff5e62' to={`${URLS.LEAGUES}/${leagueId}/${season.id}`}>
      <Typography variant='h3'>{season.name}</Typography>
    </LinkGradientCard>
  );
};

export default SeasonCard;
