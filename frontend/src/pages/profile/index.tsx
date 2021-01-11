import { GetServerSideProps } from 'next';
import { useRouter } from 'next/router';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import Cookies from 'js-cookie';
import addMonths from 'date-fns/addMonths';
import { AUTH_TOKEN } from 'constant';
import { getAuthTokenServer } from 'utils';
import { IUser, ICreateUser } from 'types/User';
import { LoginRequest } from 'types/Request';
import UserAPI from 'api/UserAPI';
import AuthAPI from 'api/AuthAPI';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';

// Project
import Navigation from 'components/navigation/Navigation';
import CreateLeague from 'components/leagues/CreateLeague';
import LeagueCard from 'components/leagues/LeagueCard';
import TeamCard from 'components/teams/TeamCard';

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
  const { handleSubmit: handleSubmitSignIn, errors: errorsSignIn, register: registerSignIn } = useForm();
  const { handleSubmit: handleSubmitSignUp, errors: errorsSignUp, register: registerSignUp } = useForm();
  const [page, setPage] = useState(PAGES.LOGIN);

  const refreshData = () => {
    router.replace(router.asPath);
  };

  const signIn = async (data: LoginRequest) => {
    try {
      const response = await AuthAPI.login({ email: data.email, password: data.password });
      Cookies.set(AUTH_TOKEN, response.token, { expires: addMonths(new Date(), 6) });
      showSnackbar('Successfully signed in!', 'success');
      refreshData();
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  const signUp = async (data: ICreateUser) => {
    try {
      const response = await AuthAPI.signUp({
        name: data.name,
        email: data.email,
        password: data.password,
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
          <Typography variant='h1'>Sign in</Typography>
          <form onSubmit={handleSubmitSignIn(signIn)}>
            <TextField
              className={classes.field}
              defaultValue=''
              error={Boolean(errorsSignIn.email)}
              fullWidth
              helperText={errorsSignIn.email?.message}
              inputRef={registerSignIn({ required: 'You must provide an email' })}
              label='Email'
              name='email'
              required
              type='email'
              variant='outlined'
            />
            <TextField
              className={classes.field}
              defaultValue=''
              error={Boolean(errorsSignIn.password)}
              fullWidth
              helperText={errorsSignIn.password?.message}
              inputRef={registerSignIn({ required: 'You must provide a password' })}
              label='Password'
              name='password'
              required
              type='password'
              variant='outlined'
            />
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
          <Typography variant='h1'>New user</Typography>
          <form onSubmit={handleSubmitSignUp(signUp)}>
            <TextField
              className={classes.field}
              defaultValue=''
              error={Boolean(errorsSignUp.name)}
              fullWidth
              helperText={errorsSignUp.name?.message}
              inputRef={registerSignUp({ required: 'You must provide a name' })}
              label='Name'
              name='name'
              required
              variant='outlined'
            />
            <TextField
              className={classes.field}
              defaultValue=''
              error={Boolean(errorsSignUp.email)}
              fullWidth
              helperText={errorsSignUp.email?.message}
              inputRef={registerSignUp({ required: 'You must provide an email' })}
              label='Email'
              name='email'
              required
              type='email'
              variant='outlined'
            />
            <TextField
              className={classes.field}
              defaultValue=''
              error={Boolean(errorsSignUp.password)}
              fullWidth
              helperText={errorsSignUp.password?.message}
              inputRef={registerSignUp({
                required: 'You must provide a password',
                minLength: { value: 6, message: 'The password must be at least 6 characters long' },
              })}
              label='Password'
              name='password'
              required
              type='password'
              variant='outlined'
            />
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

const Authed = ({ user }: { user: IUser }) => {
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
      <Typography variant='h1'>{`Hi, ${user.name}!`}</Typography>
      <Typography variant='h3'>Your leagues</Typography>
      {user.leagues.map((league) => (
        <LeagueCard key={league.id} league={league} />
      ))}
      <CreateLeague />
      <Divider className={classes.field} />
      <Typography variant='h3'>Your teams</Typography>
      {user.teams.map((team) => (
        <TeamCard key={team.id} team={team} />
      ))}
      <Divider className={classes.field} />
      <Button className={classes.field} color='secondary' fullWidth onClick={signOut} variant='outlined'>
        Sign out
      </Button>
    </Navigation>
  );
};

export type IProps = {
  user: IUser | null;
};

const Profile = ({ user }: IProps) => (user ? <Authed user={user} /> : <NotAuthed />);

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
