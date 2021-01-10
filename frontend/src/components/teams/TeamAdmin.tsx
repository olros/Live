import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { useForm } from 'react-hook-form';
import { IAddLeagueAdmin } from 'types/League';
import { ITeam, IUpdateTeam } from 'types/Team';
import { IUserCompact } from 'types/User';
import TeamAPI from 'api/TeamAPI';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
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
  team: ITeam;
};

const TeamAdmin = ({ team }: IProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const [admins, setAdmins] = useState<Array<IUserCompact>>([]);
  const { handleSubmit: handleSubmitDetails, errors: errorsDetails, register: registerDetails } = useForm({
    defaultValues: { name: team.name, logo: team.logo, description: team.description },
  });
  const { handleSubmit: handleSubmitAdmin, errors: errorsAdmin, register: registerAdmin, reset } = useForm();
  const router = useRouter();

  useEffect(() => {
    TeamAPI.getAllTeamAdmins(team.id).then((admins) => setAdmins(admins));
  }, [team.id]);

  const updateDetails = async (data: IUpdateTeam) => {
    try {
      await TeamAPI.updateTeamById(team.id, { name: data.name, logo: data.logo, description: data.description });
      showSnackbar(`"${data.name}" was successfully updated`, 'success');
      router.replace(router.asPath);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };

  const addAdmin = async (data: IAddLeagueAdmin) => {
    try {
      const newAdmins = await TeamAPI.addTeamAdmin(team.id, { email: data.email });
      setAdmins(newAdmins);
      showSnackbar(`"${data.email}" was added as an admin of this team`, 'success');
      reset();
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };

  const removeAdmin = async (userId: number) => {
    try {
      const newAdmins = await TeamAPI.deleteTeamAdmin(team.id, userId);
      setAdmins(newAdmins);
      showSnackbar(`The user was removed from being an admin of this team`, 'success');
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };

  return (
    <Paper>
      <Typography className={classes.field} variant='h3'>
        Details
      </Typography>
      <form onSubmit={handleSubmitDetails(updateDetails)}>
        <TextField
          className={classes.field}
          defaultValue=''
          error={Boolean(errorsDetails.name)}
          fullWidth
          helperText={errorsDetails.name?.message}
          inputRef={registerDetails({ required: 'You must provide a name' })}
          label='Name'
          name='name'
        />
        <Button className={classes.field} color='primary' fullWidth type='submit' variant='outlined'>
          Save
        </Button>
      </form>
      <Divider className={classes.field} />
      <Typography className={classes.field} variant='h3'>
        Admins
      </Typography>
      <form onSubmit={handleSubmitAdmin(addAdmin)}>
        <TextField
          className={classes.field}
          defaultValue=''
          error={Boolean(errorsAdmin.email)}
          fullWidth
          helperText={errorsAdmin.email?.message}
          inputRef={registerAdmin({ required: 'You must provide an email' })}
          label='Email of new admin'
          name='email'
          type='email'
        />
        <Button className={classes.field} color='primary' fullWidth type='submit' variant='outlined'>
          Add admin
        </Button>
      </form>
      <List dense>
        {admins.map((admin) => (
          <ListItem divider={admin.id !== admins[admins.length - 1].id} key={admin.id}>
            <ListItemText primary={admin.name} secondary={admin.email} />
            <ListItemSecondaryAction>
              <IconButton aria-label='delete' edge='end' onClick={() => removeAdmin(admin.id)}>
                <DeleteIcon />
              </IconButton>
            </ListItemSecondaryAction>
          </ListItem>
        ))}
      </List>
    </Paper>
  );
};

export default TeamAdmin;
