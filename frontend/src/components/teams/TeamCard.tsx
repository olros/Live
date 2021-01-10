import URLS from 'URLS';
import { ITeamCompact } from 'types/Team';

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
  team: ITeamCompact;
};

const TeamCard = ({ team }: IProps) => {
  const classes = useStyles();
  return (
    <LinkGradientCard className={classes.card} gradientFrom='#636363' gradientTo='#a2ab58' to={`${URLS.TEAMS}/${team.id}`}>
      <Typography variant='h3'>{team.name}</Typography>
    </LinkGradientCard>
  );
};

export default TeamCard;
