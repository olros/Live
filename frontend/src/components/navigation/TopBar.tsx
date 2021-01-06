import URLS from 'URLS';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Hidden from '@material-ui/core/Hidden';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import Avatar from '@material-ui/core/Avatar';

// Assets/Icons
import LOGO from 'assets/img/logo.png';
import AccountIcon from '@material-ui/icons/AccountCircleOutlined';
import EventIcon from '@material-ui/icons/Event';
import ChallengeIcon from '@material-ui/icons/EmojiEventsRounded';
import PlayerIcon from '@material-ui/icons/PlayArrowRounded';

// Project components
import Link from 'components/navigation/link';
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
