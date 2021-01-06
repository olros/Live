import Link from 'next/link';
import URLS from 'URLS';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';

const useStyles = makeStyles((theme) => ({
  content: {
    width: '100%',
    padding: 40,
    margin: 'auto',
  },
  logo: {
    width: '60%',
    maxWidth: 300,
    objectFit: 'contain',
    margin: 'auto',
    display: 'block',
  },
}));

function Footer() {
  const classes = useStyles();

  return (
    <>
      <Divider variant='middle' />
      <div className={classes.content}>
        <img alt='Logo' className={classes.logo} src='/logo.png' />
      </div>
    </>
  );
}

export default Footer;
