import { useState } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import URLS from 'URLS';
import { ICreateSeason } from 'types/Season';
import SeasonAPI from 'api/SeasonAPI';
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

const CreateSeason = ({ leagueId, ...buttonProps }: IProps) => {
  const classes = useStyles();
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const [isOpen, setIsOpen] = useState(false);
  const { handleSubmit, errors, register } = useForm();
  const create = async (data: ICreateSeason) => {
    try {
      const response = await SeasonAPI.createSeason({ name: data.name, leagueId: leagueId });
      showSnackbar(`The season "${data.name}" was successfully created`, 'success');
      router.push(`${URLS.LEAGUES}/${leagueId}/${response.id}`);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  return (
    <>
      <Button className={classes.field} color='primary' fullWidth onClick={() => setIsOpen(true)} variant='outlined' {...buttonProps}>
        Create new season
      </Button>
      <Dialog confirmText='Create season' onClose={() => setIsOpen(false)} onConfirm={handleSubmit(create)} open={isOpen} titleText='Create season'>
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
        </form>
      </Dialog>
    </>
  );
};

export default CreateSeason;
