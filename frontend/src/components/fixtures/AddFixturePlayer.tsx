import { useEffect, useMemo, useRef, useState } from 'react';
import { useRouter } from 'next/router';
import { IFixture } from 'types/Fixture';
import { IPlayerCompact } from 'types/Player';
import TeamAPI from 'api/TeamAPI';
import FixtureAPI from 'api/FixtureAPI';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import MenuItem from '@material-ui/core/MenuItem';
import Button from '@material-ui/core/Button';
// Project
import Paper from 'components/layout/Paper';

const useStyles = makeStyles((theme) => ({
  field: {
    margin: theme.spacing(1, 0),
  },
}));

export type IProps = {
  fixture: IFixture;
  teamId: number;
};

const AddFixturePlayer = ({ fixture, teamId }: IProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const router = useRouter();
  const inputRef = useRef<HTMLInputElement>();
  const [players, setPlayers] = useState<Array<IPlayerCompact>>([]);
  const fixturePlayers = useMemo(() => (teamId === fixture.homeTeam.id ? fixture.homeTeamPlayers : fixture.awayTeamPlayers), [teamId, fixture]);

  useEffect(() => {
    TeamAPI.getAllTeamPlayers(teamId)
      .then(setPlayers)
      .catch(() => null);
  }, [teamId]);

  const addPlayer = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const playerId = inputRef.current?.value;
    if (!playerId) {
      showSnackbar('Something went wrong', 'error');
      return;
    }
    try {
      await FixtureAPI.createFixturePlayer(fixture.id, { playerId: Number(playerId) });
      showSnackbar(`The player was added to the fixture`, 'success');
      if (inputRef.current) {
        inputRef.current.value = '';
      }
      router.replace(router.asPath);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };

  return (
    <Paper>
      <Typography className={classes.field} variant='h3'>
        Add player to fixture
      </Typography>
      <form onSubmit={addPlayer}>
        <TextField className={classes.field} defaultValue='' fullWidth inputRef={inputRef} label='Player' select>
          {players
            .filter((player) => !fixturePlayers.find((fPlayer) => fPlayer.player.id === player.id))
            .map((player) => (
              <MenuItem key={player.id} value={player.id}>
                {player.name}
              </MenuItem>
            ))}
        </TextField>
        <Button className={classes.field} color='primary' fullWidth type='submit' variant='outlined'>
          Add player
        </Button>
      </form>
    </Paper>
  );
};

export default AddFixturePlayer;
