import { useState } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import URLS from 'URLS';
import { ICreateLeague } from 'types/League';
import LeagueAPI from 'api/LeagueAPI';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Button, { ButtonProps } from '@material-ui/core/Button';

// Project
import Dialog from 'components/layout/Dialog';

const useStyles = makeStyles((theme) => ({
  field: {
    margin: theme.spacing(1, 0),
  },
}));

const CreateLeague = ({ ...buttonProps }: ButtonProps) => {
  const classes = useStyles();
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const [isOpen, setIsOpen] = useState(false);
  const { handleSubmit, errors, register } = useForm();
  const create = async (data: ICreateLeague) => {
    try {
      const response = await LeagueAPI.createLeague({ name: data.name });
      showSnackbar(`The league "${data.name}" was successfully created`, 'success');
      router.push(`${URLS.LEAGUES}/${response.id}`);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  return (
    <>
      <Button className={classes.field} color='primary' fullWidth onClick={() => setIsOpen(true)} variant='outlined' {...buttonProps}>
        Create new league
      </Button>
      <Dialog confirmText='Create league' onClose={() => setIsOpen(false)} onConfirm={handleSubmit(create)} open={isOpen} titleText='Create league'>
        <form onSubmit={handleSubmit(create)}>
          <TextField
            defaultValue=''
            error={Boolean(errors.name)}
            fullWidth
            helperText={errors.name?.message}
            inputRef={register({ required: 'You must provide a name' })}
            label='Name'
            name='name'
          />
        </form>
      </Dialog>
    </>
  );
};

export default CreateLeague;
