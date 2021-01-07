// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import Divider from '@material-ui/core/Divider';

const useStyles = makeStyles((theme) => ({
  content: {
    width: '100%',
    padding: theme.spacing(4),
    margin: 'auto',
  },
  logo: {
    width: '60%',
    maxWidth: 200,
    objectFit: 'contain',
    margin: 'auto',
    display: 'block',
  },
}));

const Footer = () => {
  const classes = useStyles();

  return (
    <>
      <Divider variant='middle' />
      <div className={classes.content}>
        <img alt='Logo' className={classes.logo} src='/logo.png' />
      </div>
    </>
  );
};

export default Footer;
