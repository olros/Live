import { GetServerSideProps } from 'next';
import { getAuthTokenServer } from 'utils';
import { ILeagueCompact } from 'types/League';
import LeagueAPI from 'api/LeagueAPI';

// Material UI
import Typography from '@material-ui/core/Typography';

// Project
import Navigation from 'components/navigation/Navigation';
import LeagueCard from 'components/leagues/LeagueCard';
import CreateLeague from 'components/leagues/CreateLeague';

export type IProps = {
  leagues: Array<ILeagueCompact>;
};

// TODO: Change endpoint to pagination
const Leagues = ({ leagues }: IProps) => {
  return (
    <Navigation>
      <Typography variant='h1'>Leagues</Typography>
      {leagues.map((league) => (
        <LeagueCard key={league.id} league={league} />
      ))}
      <CreateLeague color='secondary' />
    </Navigation>
  );
};

export const getServerSideProps: GetServerSideProps = async ({ req }) => {
  try {
    const token = getAuthTokenServer(req.headers.cookie);
    if (!token) {
      throw new Error('');
    }
    const leagues = await LeagueAPI.getAllLeagues(token);
    const data: IProps = { leagues };
    return { props: data };
  } catch (e) {
    const data: IProps = { leagues: [] };
    return { props: data };
  }
};

export default Leagues;
