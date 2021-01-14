import { useMemo, useState } from 'react';
import { IFixture, IFixturePlayerCompact } from 'types/Fixture';
import { useSnackbar } from 'hooks/Snackbar';
import { useRouter } from 'next/router';
import FixtureAPI from 'api/FixtureAPI';

// Material UI
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Avatar from '@material-ui/core/Avatar';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import IconButton from '@material-ui/core/IconButton';

// Icons
import DeleteIcon from '@material-ui/icons/DeleteOutlineRounded';

// Project
import Paper from 'components/layout/Paper';
import Tabs from 'components/layout/Tabs';
import AddFixturePlayer from 'components/fixtures/AddFixturePlayer';

export type IEntryProps = {
  fixtureId: number;
  player: IFixturePlayerCompact;
  divider?: boolean;
  admin?: boolean;
};

const PlayerListItem = ({ fixtureId, player, divider = false, admin = false }: IEntryProps) => {
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const removeFromFixture = async () => {
    try {
      await FixtureAPI.deleteFixturePlayer(fixtureId, player.id);
      showSnackbar(`${player.player.name} was removed from the fixture`, 'success');
      router.replace(router.asPath);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };

  return (
    <ListItem divider={divider}>
      <ListItemAvatar>
        <Avatar>{player.number || ''}</Avatar>
      </ListItemAvatar>
      <ListItemText primary={player.player.name} secondary={player.position} />
      {admin && (
        <ListItemSecondaryAction>
          <IconButton aria-label='Remove from fixture' edge='end' onClick={removeFromFixture}>
            <DeleteIcon />
          </IconButton>
        </ListItemSecondaryAction>
      )}
    </ListItem>
  );
};

export type IProps = {
  fixture: IFixture;
};

enum TABS {
  HOME,
  AWAY,
}

const FixturePlayers = ({ fixture }: IProps) => {
  const tabs = [
    { label: fixture.homeTeam.name, value: TABS.HOME },
    { label: fixture.awayTeam.name, value: TABS.AWAY },
  ];
  const [selectedTab, setSelectedTab] = useState(TABS.HOME);

  const players = useMemo(() => (selectedTab === TABS.HOME ? fixture.homeTeamPlayers : fixture.awayTeamPlayers), [selectedTab, fixture]);
  const isAdmin = useMemo(() => (selectedTab === TABS.HOME ? fixture.homeTeam.isAdmin : selectedTab === TABS.AWAY && fixture.awayTeam.isAdmin), [
    selectedTab,
    fixture,
  ]);

  return (
    <>
      <Paper marginBottom>
        <Tabs marginBottom selected={selectedTab} setSelected={setSelectedTab} tabs={tabs} />
        <List>
          {players.map((player) => (
            <PlayerListItem admin={isAdmin} divider={player.id !== players[players.length - 1].id} fixtureId={fixture.id} key={player.id} player={player} />
          ))}
        </List>
      </Paper>
      {selectedTab === TABS.HOME && fixture.homeTeam.isAdmin && <AddFixturePlayer fixture={fixture} teamId={fixture.homeTeam.id} />}
      {selectedTab === TABS.AWAY && fixture.awayTeam.isAdmin && <AddFixturePlayer fixture={fixture} teamId={fixture.awayTeam.id} />}
    </>
  );
};

export default FixturePlayers;
