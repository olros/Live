import { useState } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import URLS from 'URLS';
import { ICreateFixture } from 'types/Fixture';
import { ISeason } from 'types/Season';
import FixtureAPI from 'api/FixtureAPI';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import MenuItem from '@material-ui/core/MenuItem';
import Button, { ButtonProps } from '@material-ui/core/Button';

// Project
import Dialog from 'components/layout/Dialog';
import ReactHookFormSelect from 'components/input/ReactHookFormSelect';

const useStyles = makeStyles((theme) => ({
  field: {
    margin: theme.spacing(1, 0),
  },
}));

export type IProps = ButtonProps & {
  season: ISeason;
};

const CreateFixture = ({ season, ...buttonProps }: IProps) => {
  const classes = useStyles();
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const [isOpen, setIsOpen] = useState(false);
  const { handleSubmit, errors, register, control } = useForm();
  const create = async (data: ICreateFixture) => {
    try {
      const response = await FixtureAPI.createFixture({
        seasonId: season.id,
        homeTeam: data.homeTeam,
        awayTeam: data.awayTeam,
        time: `${data.time}:00Z`,
        location: data.location,
        referee: data.referee,
      });
      showSnackbar(`The fixture was successfully created`, 'success');
      router.push(`${URLS.FIXTURES}/${response.id}`);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  return (
    <>
      <Button className={classes.field} color='primary' fullWidth onClick={() => setIsOpen(true)} variant='outlined' {...buttonProps}>
        Create new fixture
      </Button>
      <Dialog confirmText='Create fixture' onClose={() => setIsOpen(false)} onConfirm={handleSubmit(create)} open={isOpen} titleText='Create fixture'>
        <form onSubmit={handleSubmit(create)}>
          <ReactHookFormSelect
            className={classes.field}
            control={control}
            defaultValue=''
            errorMessage={errors.homeTeam?.message}
            fullWidth
            label='Hometeam'
            name='homeTeam'
            requiredMessage='You must select a hometeam'
            variant='outlined'>
            {season.teams.map((team) => (
              <MenuItem key={team.id} value={team.id}>
                {team.name}
              </MenuItem>
            ))}
          </ReactHookFormSelect>
          <ReactHookFormSelect
            className={classes.field}
            control={control}
            defaultValue=''
            errorMessage={errors.awayTeam?.message}
            fullWidth
            label='Awayteam'
            name='awayTeam'
            requiredMessage='You must select a awayteam'
            variant='outlined'>
            {season.teams.map((team) => (
              <MenuItem key={team.id} value={team.id}>
                {team.name}
              </MenuItem>
            ))}
          </ReactHookFormSelect>
          <TextField
            InputLabelProps={{ shrink: true }}
            className={classes.field}
            defaultValue=''
            error={Boolean(errors.time)}
            fullWidth
            helperText={errors.time?.message}
            inputRef={register({ required: 'You must provide a time for the fixture' })}
            label='Time'
            name='time'
            type='datetime-local'
          />
          <TextField className={classes.field} defaultValue='' fullWidth inputRef={register} label='Location' name='location' />
          <TextField className={classes.field} defaultValue='' fullWidth inputRef={register} label='Referee' name='referee' />
        </form>
      </Dialog>
    </>
  );
};

export default CreateFixture;
