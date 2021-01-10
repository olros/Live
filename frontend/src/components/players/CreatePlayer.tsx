import { useState } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { EPosition, ICreatePlayer } from 'types/Player';
import TeamAPI from 'api/TeamAPI';
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
  teamId: number;
};

const CreateTeam = ({ teamId, ...buttonProps }: IProps) => {
  const classes = useStyles();
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const [isOpen, setIsOpen] = useState(false);
  const { handleSubmit, errors, register, control, reset } = useForm();
  const create = async (data: ICreatePlayer) => {
    try {
      await TeamAPI.createTeamPlayer(teamId, { name: data.name, number: data.number, position: data.position, active: true });
      showSnackbar(`"${data.name}" was successfully created add added to the team as active`, 'success');
      router.replace(router.asPath);
      reset();
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  return (
    <>
      <Button className={classes.field} color='primary' fullWidth onClick={() => setIsOpen(true)} variant='outlined' {...buttonProps}>
        Create new player
      </Button>
      <Dialog confirmText='Create player' onClose={() => setIsOpen(false)} onConfirm={handleSubmit(create)} open={isOpen} titleText='Create player'>
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
          <TextField
            className={classes.field}
            defaultValue=''
            fullWidth
            inputRef={register({ valueAsNumber: true })}
            label='Number'
            name='number'
            type='number'
          />
          <ReactHookFormSelect
            className={classes.field}
            control={control}
            defaultValue={EPosition.MIDFIELDER.toUpperCase() || ''}
            fullWidth
            label='Position'
            name='position'
            variant='outlined'>
            {[EPosition.KEEPER, EPosition.DEFENDER, EPosition.MIDFIELDER, EPosition.FORWARD].map((position) => (
              <MenuItem key={position} value={position.toUpperCase()}>
                {position}
              </MenuItem>
            ))}
          </ReactHookFormSelect>
        </form>
      </Dialog>
    </>
  );
};

export default CreateTeam;
