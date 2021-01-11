import { GetServerSideProps } from 'next';
import { useEffect, useState } from 'react';
import { getAuthTokenServer } from 'utils';
import { ILeague } from 'types/League';
import { ISeason, ISeasonCompact, ITableEntry } from 'types/Season';
import { IFixtureCompact } from 'types/Fixture';
import LeagueAPI from 'api/LeagueAPI';
import SeasonAPI from 'api/SeasonAPI';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import List from '@material-ui/core/List';
import Collapse from '@material-ui/core/Collapse';

// Project
import Navigation from 'components/navigation/Navigation';
import Paper from 'components/layout/Paper';
import TopLayout from 'components/layout/TopLayout';
import Tabs from 'components/layout/Tabs';
import LeagueAdmin from 'components/leagues/LeagueAdmin';
import CreateTeam from 'components/teams/CreateTeam';
import TeamCard from 'components/teams/TeamCard';
import CreateSeason from 'components/seasons/CreateSeason';
import SeasonCard from 'components/seasons/SeasonCard';
import SeasonsSelect from 'components/seasons/SeasonsSelect';
import AddSeasonTeam from 'components/seasons/AddSeasonTeam';
import SeasonTeamListItem from 'components/seasons/SeasonTeamListItem';
import SeasonAdmin from 'components/seasons/SeasonAdmin';

const useStyles = makeStyles(() => ({
  seasonsList: {
    display: 'flex',
    flexDirection: 'column-reverse',
  },
}));

export type IProps = {
  league: ILeague;
  season: ISeason | null;
  fixtures: Array<IFixtureCompact> | null;
  table: Array<ITableEntry> | null;
};

enum TABS {
  MAIN,
  TEAMS,
  ADMIN,
}

const League = ({ league, season, fixtures, table }: IProps) => {
  const classes = useStyles();
  const selectableTabs = [
    { label: 'Main', value: TABS.MAIN },
    { label: 'Teams admin', value: TABS.TEAMS },
    { label: 'Settings', value: TABS.ADMIN },
  ];
  const [selectedTab, setSelectedTab] = useState(TABS.MAIN);
  return (
    <Navigation>
      <TopLayout>
        <Typography variant='h1'>{league.name}</Typography>
        {league.isAdmin && <Tabs selected={selectedTab} setSelected={setSelectedTab} tabs={selectableTabs} />}
      </TopLayout>
      {season !== null && (
        <Paper marginBottom>
          <SeasonsSelect league={league} selectedSeasonId={season.id} />
        </Paper>
      )}
      <Collapse in={selectedTab === TABS.MAIN}>
        <Paper>
          {season ? (
            <></>
          ) : (
            <>
              <Typography variant='h3'>Seasons</Typography>
              {league.seasons
                .sort((a, b) => b.name.localeCompare(a.name))
                .map((season) => (
                  <SeasonCard key={season.id} leagueId={league.id} season={season} />
                ))}
              {league.isAdmin && <CreateSeason leagueId={league.id} />}
            </>
          )}
        </Paper>
      </Collapse>
      <Collapse in={selectedTab === TABS.TEAMS} mountOnEnter>
        {season ? (
          <>
            <Paper marginBottom>
              <Typography variant='h3'>Teams</Typography>
              <List>
                {season.teams.map((team) => (
                  <SeasonTeamListItem admin key={team.id} seasonId={season.id} team={team} />
                ))}
              </List>
            </Paper>
            <AddSeasonTeam league={league} season={season} />
          </>
        ) : (
          <>
            <Typography variant='h3'>Teams</Typography>
            {league.teams.map((team) => (
              <TeamCard key={team.id} team={team} />
            ))}
            <CreateTeam leagueId={league.id} />
          </>
        )}
      </Collapse>
      <Collapse in={selectedTab === TABS.ADMIN} mountOnEnter>
        {season ? <SeasonAdmin season={season} /> : <LeagueAdmin league={league} />}
      </Collapse>
    </Navigation>
  );
};

export const getServerSideProps: GetServerSideProps = async ({ req, query }) => {
  try {
    const token = getAuthTokenServer(req.headers.cookie);
    const { id } = query;
    if (!Array.isArray(id)) {
      throw new Error('');
    }
    const leagueId = Number(id[0]);
    const seasonId = Number(id[1]);
    const page = id[2];
    const [league, season, fixtures, table] = await Promise.all([
      LeagueAPI.getLeagueById(leagueId, token),
      seasonId ? SeasonAPI.getSeasonById(seasonId, token) : null,
      seasonId && page !== 'table' ? SeasonAPI.getSeasonFixtures(seasonId, token) : null,
      seasonId && page === 'table' ? SeasonAPI.getSeasonTable(seasonId, token) : null,
    ]);
    const data: IProps = { league, season, fixtures, table };
    return { props: data };
  } catch (e) {
    return {
      notFound: true,
    };
  }
};

export default League;
