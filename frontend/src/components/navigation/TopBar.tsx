import URLS from 'URLS';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';

// Project components
import Link from 'components/navigation/Link';

const useStyles = makeStyles((theme) => ({
  nav: {
    borderRadius: 0,
    padding: theme.spacing(2, 0),
    borderTop: 'none',
    borderLeft: 'none',
    borderRight: 'none',
    display: 'flex',
    justifyContent: 'center',
  },
}));

const TopBar = () => {
  const classes = useStyles();

  return (
    <div className={classes.nav}>
      <Link to={URLS.LANDING}>
        <img alt='Logo' height='40px' src='/logo.png' />
      </Link>
    </div>
  );
};

export default TopBar;
