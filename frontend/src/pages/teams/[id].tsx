import { GetServerSideProps } from 'next';
import { useState } from 'react';
import { getAuthTokenServer } from 'utils';
import { ITeam } from 'types/Team';
import TeamAPI from 'api/TeamAPI';

// Material UI
import Typography from '@material-ui/core/Typography';
import List from '@material-ui/core/List';
import Collapse from '@material-ui/core/Collapse';

// Project
import Navigation from 'components/navigation/Navigation';
import Paper from 'components/layout/Paper';
import TopLayout from 'components/layout/TopLayout';
import Tabs from 'components/layout/Tabs';
import TeamAdmin from 'components/teams/TeamAdmin';
import CreatePlayer from 'components/players/CreatePlayer';
import PlayerListItem from 'components/players/PlayerListItem';

export type IProps = {
  team: ITeam;
};

enum TABS {
  MAIN,
  ADMIN,
}

const Team = ({ team }: IProps) => {
  const selectableTabs = [
    { label: 'Main', value: TABS.MAIN },
    { label: 'Settings', value: TABS.ADMIN },
  ];
  const [selectedTab, setSelectedTab] = useState(TABS.MAIN);
  return (
    <Navigation>
      <TopLayout>
        <Typography variant='h1'>{team.name}</Typography>
        {team.isAdmin && <Tabs selected={selectedTab} setSelected={setSelectedTab} tabs={selectableTabs} />}
      </TopLayout>
      <Collapse in={selectedTab === TABS.MAIN}>
        <Paper marginBottom>{team.description}</Paper>
        <Paper marginBottom>
          <Typography variant='h3'>Players - Current</Typography>
          <List>
            {team.players
              .filter((player) => player.active)
              .sort((a, b) => (a.number || 0) - (b.number || 0))
              .map((player) => (
                <PlayerListItem admin={team.isAdmin} key={player.id} player={player} teamId={team.id} />
              ))}
          </List>
          {team.isAdmin && <CreatePlayer teamId={team.id} />}
        </Paper>
        <Paper marginBottom>
          <Typography variant='h3'>Players - Former</Typography>
          <List>
            {team.players
              .filter((player) => !player.active)
              .sort((a, b) => (a.number || 0) - (b.number || 0))
              .map((player) => (
                <PlayerListItem admin={team.isAdmin} key={player.id} player={player} teamId={team.id} />
              ))}
          </List>
        </Paper>
      </Collapse>
      <Collapse in={selectedTab === TABS.ADMIN} mountOnEnter>
        <TeamAdmin team={team} />
      </Collapse>
    </Navigation>
  );
};

export const getServerSideProps: GetServerSideProps = async ({ req, query }) => {
  try {
    const token = getAuthTokenServer(req.headers.cookie);
    const { id } = query;
    const team = await TeamAPI.getTeamById(Number(id), token);
    const data: IProps = { team };
    return { props: data };
  } catch (e) {
    return {
      notFound: true,
    };
  }
};

export default Team;
