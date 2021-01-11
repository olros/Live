import { useRef } from 'react';
import { useRouter } from 'next/router';
import { ILeague } from 'types/League';
import { ISeason } from 'types/Season';
import SeasonAPI from 'api/SeasonAPI';
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
  league: ILeague;
  season: ISeason;
};

const AddSeasonTeam = ({ league, season }: IProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const router = useRouter();
  const inputRef = useRef<HTMLInputElement>();

  const addTeam = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const teamId = inputRef.current?.value;
    if (!teamId) {
      showSnackbar('Something went wrong', 'error');
      return;
    }
    try {
      await SeasonAPI.addSeasonTeam(season.id, { teamId: Number(teamId) });
      showSnackbar(`The team was added to "${season.name}"`, 'success');
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
        Add team to season
      </Typography>
      <form onSubmit={addTeam}>
        <TextField className={classes.field} defaultValue='' fullWidth inputRef={inputRef} label='Name' select>
          {league.teams
            .filter((team) => !season.teams.find((sTeam) => sTeam.id === team.id))
            .map((team) => (
              <MenuItem key={team.id} value={team.id}>
                {team.name}
              </MenuItem>
            ))}
        </TextField>
        <Button className={classes.field} color='primary' fullWidth type='submit' variant='outlined'>
          Add team
        </Button>
      </form>
    </Paper>
  );
};

export default AddSeasonTeam;
