import URLS from 'URLS';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

// Project
import Navigation from 'components/navigation/Navigation';
import LinkGradientCard from 'components/layout/LinkGradientCard';

const useStyles = makeStyles((theme) => ({
  topMargin: {
    marginTop: theme.spacing(5),
  },
}));

const Auth = () => {
  const classes = useStyles();
  return (
    <>
      <Navigation>
        <LinkGradientCard className={classes.topMargin} gradientFrom='#8A2387' gradientTo='#E94057' to={URLS.PROFILE}>
          <Typography align='center' variant='h2'>
            Logg ut
          </Typography>
        </LinkGradientCard>
      </Navigation>
    </>
  );
};

export default Auth;
