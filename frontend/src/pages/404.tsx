import { GetServerSideProps } from 'next';
import URLS from 'URLS';
import { getAuthTokenServer } from 'utils';
import { IFixtureCompact } from 'types/Fixture';
import FixtureAPI from 'api/FixtureAPI';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

// Project
import Navigation from 'components/navigation/Navigation';
import LinkGradientCard from 'components/layout/LinkGradientCard';

const useStyles = makeStyles((theme) => ({
  topMargin: {
    marginTop: theme.spacing(5),
    marginBottom: theme.spacing(1),
  },
  card: {
    margin: theme.spacing(1, 0),
  },
}));

const NotFound = () => {
  const classes = useStyles();
  return (
    <Navigation>
      <Typography align='center' className={classes.topMargin} variant='h1'>
        404
      </Typography>
      <Typography align='center' variant='h3'>
        {`Could not find what you're looking for`}
      </Typography>
      <LinkGradientCard className={classes.card} gradientFrom='#8A2387' gradientTo='#E94057' to={URLS.LANDING}>
        <Typography align='center' variant='h3'>
          Home
        </Typography>
      </LinkGradientCard>
    </Navigation>
  );
};

export default NotFound;
