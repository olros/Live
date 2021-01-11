import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { ISeason, IUpdateSeason } from 'types/Season';
import SeasonAPI from 'api/SeasonAPI';
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
  season: ISeason;
};

const SeasonAdmin = ({ season }: IProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const { handleSubmit, errors, register } = useForm({ defaultValues: { name: season.name } });
  const router = useRouter();

  const updateDetails = async (data: IUpdateSeason) => {
    try {
      await SeasonAPI.updateSeasonById(season.id, { name: data.name });
      showSnackbar(`"${data.name}" was successfully updated`, 'success');
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
      <form onSubmit={handleSubmit(updateDetails)}>
        <TextField
          className={classes.field}
          defaultValue=''
          error={Boolean(errors.name)}
          fullWidth
          helperText={errors.name?.message}
          inputRef={register({ required: 'You must provide a name' })}
          label='Name'
          name='name'
        />
        <Button className={classes.field} color='primary' fullWidth type='submit' variant='outlined'>
          Save
        </Button>
      </form>
    </Paper>
  );
};

export default SeasonAdmin;
