import { useRef, useState } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { IAddLeagueAdmin, ILeague } from 'types/League';
import { ISeason, IAddSeasonTeam } from 'types/Season';
import { IUserCompact } from 'types/User';
import SeasonAPI from 'api/SeasonAPI';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import MenuItem from '@material-ui/core/MenuItem';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import IconButton from '@material-ui/core/IconButton';

// Icons
import DeleteIcon from '@material-ui/icons/Delete';

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
      await SeasonAPI.addSeasonTeam(season.id, { teamId } as IAddSeasonTeam);
      showSnackbar(`The team was added to "${season.name}"`, 'success');
      inputRef.current.value = '';
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
