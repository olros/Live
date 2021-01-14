import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { IFixture, IUpdateFixture } from 'types/Fixture';
import FixtureAPI from 'api/FixtureAPI';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
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
};

const FixtureAdmin = ({ fixture }: IProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const { handleSubmit, errors, register } = useForm({
    defaultValues: { location: fixture.location, referee: fixture.referee, time: fixture.time.slice(0, -6) },
  });
  const router = useRouter();

  const update = async (data: Pick<IUpdateFixture, 'location' | 'referee' | 'time'>) => {
    try {
      await FixtureAPI.updateFixture(fixture.id, {
        location: data.location,
        referee: data.referee,
        time: `${(data.time ? new Date(data.time) : new Date()).toJSON()}`,
      });
      showSnackbar(`The fixture was successfully updated`, 'success');
      router.replace(router.asPath);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };

  return (
    <Paper>
      <Typography className={classes.field} variant='h3'>
        Details
      </Typography>
      <form onSubmit={handleSubmit(update)}>
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
        <Button className={classes.field} color='primary' fullWidth type='submit' variant='outlined'>
          Save
        </Button>
      </form>
    </Paper>
  );
};

export default FixtureAdmin;
