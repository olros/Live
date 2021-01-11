import { useRouter } from 'next/router';
import SeasonAPI from 'api/SeasonAPI';
import { ITeamCompact } from 'types/Team';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import Avatar from '@material-ui/core/Avatar';
import IconButton from '@material-ui/core/IconButton';

// Icons
import DeleteIcon from '@material-ui/icons/DeleteOutlineRounded';

export type IProps = {
  seasonId: number;
  team: ITeamCompact;
  divider?: boolean;
  admin?: boolean;
};

const SeasonTeamListItem = ({ seasonId, team, divider = false, admin = false }: IProps) => {
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const removeTeam = async () => {
    try {
      await SeasonAPI.deleteSeasonTeam(seasonId, team.id);
      showSnackbar(`${team.name} was removed from this season`, 'success');
      router.replace(router.asPath);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  return (
    <ListItem divider={divider}>
      <ListItemAvatar>
        <Avatar src={team.logo}>{team.name.substr(0, 1)}</Avatar>
      </ListItemAvatar>
      <ListItemText primary={team.name} />
      {admin && (
        <ListItemSecondaryAction>
          <IconButton aria-label='Remove team from season' edge='end' onClick={removeTeam}>
            <DeleteIcon />
          </IconButton>
        </ListItemSecondaryAction>
      )}
    </ListItem>
  );
};

export default SeasonTeamListItem;
