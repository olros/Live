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

export type IProps = {
  isAuthed: boolean;
  fixtures: Array<IFixtureCompact>;
};

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const Landing = ({ fixtures, isAuthed }: IProps) => {
  const classes = useStyles();
  return (
    <Navigation>
      <LinkGradientCard className={classes.topMargin} gradientFrom='#8A2387' gradientTo='#E94057' to={URLS.PROFILE}>
        <Typography align='center' variant='h3'>
          {isAuthed ? 'Profil' : 'Logg inn'}
        </Typography>
      </LinkGradientCard>
      <LinkGradientCard className={classes.card} gradientFrom='#8E2DE2' gradientTo='#4A00E0' to={URLS.LEAGUES}>
        <Typography align='center' variant='h3'>
          Leagues
        </Typography>
      </LinkGradientCard>
    </Navigation>
  );
};

export const getServerSideProps: GetServerSideProps = async ({ req }) => {
  const token = getAuthTokenServer(req.headers.cookie);
  try {
    const fixtures: Array<IFixtureCompact> = await FixtureAPI.getAllFixtures(token);
    const data: IProps = { isAuthed: Boolean(token), fixtures };
    return { props: data };
  } catch (e) {
    const data: IProps = { isAuthed: Boolean(token), fixtures: [] };
    return { props: data };
  }
};

export default Landing;
