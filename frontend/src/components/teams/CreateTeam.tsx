import { useState } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import URLS from 'URLS';
import { ICreateTeam } from 'types/Team';
import TeamAPI from 'api/TeamAPI';
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

export type IProps = ButtonProps & {
  leagueId: number;
};

const CreateTeam = ({ leagueId, ...buttonProps }: IProps) => {
  const classes = useStyles();
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const [isOpen, setIsOpen] = useState(false);
  const { handleSubmit, errors, register } = useForm();
  const create = async (data: ICreateTeam) => {
    try {
      const response = await TeamAPI.createTeam({ name: data.name, logo: data.logo, description: data.description, leagueId });
      showSnackbar(`The team "${data.name}" was successfully created`, 'success');
      router.push(`${URLS.TEAMS}/${response.id}`);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  return (
    <>
      <Button className={classes.field} color='primary' fullWidth onClick={() => setIsOpen(true)} variant='outlined' {...buttonProps}>
        Create new team
      </Button>
      <Dialog confirmText='Create team' onClose={() => setIsOpen(false)} onConfirm={handleSubmit(create)} open={isOpen} titleText='Create team'>
        <form onSubmit={handleSubmit(create)}>
          <TextField
            className={classes.field}
            defaultValue=''
            error={Boolean(errors.name)}
            fullWidth
            helperText={errors.name?.message}
            inputRef={register({ required: 'You must provide a name' })}
            label='Name'
            name='name'
            required
          />
          <TextField className={classes.field} defaultValue='' fullWidth inputRef={register} label='Logo (url)' name='logo' />
          <TextField
            className={classes.field}
            defaultValue=''
            error={Boolean(errors.description)}
            fullWidth
            helperText={errors.description?.message}
            inputRef={register({ required: 'You must provide a description' })}
            label='Description'
            name='description'
            required
          />
        </form>
      </Dialog>
    </>
  );
};

export default CreateTeam;
