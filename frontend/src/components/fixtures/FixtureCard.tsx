import URLS from 'URLS';
import { IFixtureCompact } from 'types/Fixture';
import parseISO from 'date-fns/parseISO';
import { formatDate } from 'utils';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

// Project
import LinkGradientCard from 'components/layout/LinkGradientCard';

const useStyles = makeStyles((theme) => ({
  card: {
    margin: theme.spacing(1, 0),
  },
  content: {
    display: 'grid',
    gridTemplateColumns: '1fr auto 1fr',
    gridGap: theme.spacing(1),
  },
}));

export type IProps = {
  fixture: IFixtureCompact;
};

const FixtureCard = ({ fixture }: IProps) => {
  const classes = useStyles();
  return (
    <LinkGradientCard className={classes.card} gradientFrom='#5C258D' gradientTo='#4389A2' to={`${URLS.FIXTURES}/${fixture.id}`}>
      <div className={classes.content}>
        <Typography align='right' variant='h3'>
          {fixture.homeTeam.name}
        </Typography>
        <Typography align='center' variant='h3'>{`${fixture.result.homeTeam} - ${fixture.result.awayTeam}`}</Typography>
        <Typography align='left' variant='h3'>
          {fixture.awayTeam.name}
        </Typography>
      </div>
      <Typography align='center' variant='subtitle2'>
        {formatDate(parseISO(fixture.time))}
      </Typography>
    </LinkGradientCard>
  );
};

export default FixtureCard;
