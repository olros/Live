import { GetServerSideProps } from 'next';
import { useRouter } from 'next/router';
import { useState, useRef } from 'react';
import Cookies from 'js-cookie';
import URLS from 'URLS';
import addMonths from 'date-fns/addMonths';
import { AUTH_TOKEN } from 'constant';
import { getAuthTokenServer } from 'utils';
import { IUser } from 'types/User';
import UserAPI from 'api/UserAPI';
import AuthAPI from 'api/AuthAPI';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';

// Project
import Navigation from 'components/navigation/Navigation';
// import LinkGradientCard from 'components/layout/LinkGradientCard';

const useStyles = makeStyles((theme) => ({
  field: {
    margin: theme.spacing(1, 0),
  },
}));

enum PAGES {
  LOGIN,
  SIGNUP,
}

const NotAuthed = () => {
  const classes = useStyles();
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const [page, setPage] = useState(PAGES.LOGIN);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const refreshData = () => {
    router.replace(router.asPath);
  };

  const signIn = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const response = await AuthAPI.login({ email, password });
      Cookies.set(AUTH_TOKEN, response.token, { expires: addMonths(new Date(), 6) });
      showSnackbar('Successfully signed in!', 'success');
      refreshData();
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  const signUp = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const response = await AuthAPI.signUp({
        name,
        email,
        password,
      });
      showSnackbar(response.detail, 'success');
      setPage(PAGES.LOGIN);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  return (
    <Navigation maxWidth='md'>
      {page === PAGES.LOGIN ? (
        <>
          <Typography className={classes.field} variant='h1'>
            Sign in
          </Typography>
          <form onSubmit={signIn}>
            <TextField className={classes.field} fullWidth label='Email' onChange={(e) => setEmail(e.target.value)} type='email' value={email} variant='outlined' />
            <TextField className={classes.field} fullWidth label='Password' onChange={(e) => setPassword(e.target.value)} type='password' value={password} variant='outlined' />
            <Button className={classes.field} color='primary' fullWidth type='submit' variant='contained'>
              Sign in
            </Button>
          </form>
          <Button className={classes.field} color='secondary' fullWidth onClick={() => setPage(PAGES.SIGNUP)} variant='outlined'>
            New user
          </Button>
        </>
      ) : (
        <>
          <Typography className={classes.field} variant='h1'>
            New user
          </Typography>
          <form onSubmit={signUp}>
            <TextField className={classes.field} fullWidth label='Name' onChange={(e) => setName(e.target.value)} value={name} variant='outlined' />
            <TextField className={classes.field} fullWidth label='Email' onChange={(e) => setEmail(e.target.value)} type='email' value={email} variant='outlined' />
            <TextField className={classes.field} fullWidth label='Password' onChange={(e) => setPassword(e.target.value)} type='password' value={password} variant='outlined' />
            <Button className={classes.field} color='primary' fullWidth type='submit' variant='contained'>
              Create user
            </Button>
          </form>
          <Button className={classes.field} color='secondary' fullWidth onClick={() => setPage(PAGES.LOGIN)} variant='outlined'>
            Sign in
          </Button>
        </>
      )}
    </Navigation>
  );
};

const Authed = () => {
  const classes = useStyles();
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const refreshData = () => {
    router.replace(router.asPath);
  };
  const signOut = () => {
    Cookies.remove(AUTH_TOKEN);
    showSnackbar('Signed out', 'info');
    refreshData();
  };
  return (
    <Navigation>
      <Typography className={classes.field} variant='h1'>
        Profile
      </Typography>
      <Button className={classes.field} color='secondary' fullWidth onClick={signOut} variant='outlined'>
        Sign out
      </Button>
    </Navigation>
  );
};

export type IProps = {
  user: IUser | null;
};

const Profile = ({ user }: IProps) => (user ? <Authed /> : <NotAuthed />);

export const getServerSideProps: GetServerSideProps = async ({ req }) => {
  try {
    const token = getAuthTokenServer(req.headers.cookie);
    if (!token) {
      throw new Error('');
    }
    const user = await UserAPI.getUser(token);
    const data: IProps = { user };
    return { props: data };
  } catch (e) {
    const data: IProps = { user: null };
    return { props: data };
  }
};

export default Profile;
