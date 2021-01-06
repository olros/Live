import URLS from 'URLS';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import IconButton from '@material-ui/core/IconButton';

// Assets/Icons
import AccountIcon from '@material-ui/icons/AccountCircleOutlined';

// Project components
import Link from 'components/navigation/Link';
import Paper from 'components/layout/Paper';

const useStyles = makeStyles((theme) => ({
  nav: {
    borderRadius: 0,
    padding: theme.spacing(1, 0),
    borderTop: 'none',
    borderLeft: 'none',
    borderRight: 'none',
  },
  navContent: {
    maxWidth: theme.breakpoints.values.lg,
    margin: 'auto',
    padding: theme.spacing(0, 1),
    width: '100%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  menuButton: {
    color: theme.palette.text.primary,
  },
}));

function TopBar() {
  const classes = useStyles();

  return (
    <Paper className={classes.nav}>
      <div className={classes.navContent}>
        <Link to={URLS.LANDING}>
          <img alt='Logo' height='40px' src='/logo.png' />
        </Link>

        <Link to={URLS.AUTH}>
          <IconButton className={classes.menuButton}>
            <AccountIcon />
          </IconButton>
        </Link>
      </div>
    </Paper>
  );
}

export default TopBar;
