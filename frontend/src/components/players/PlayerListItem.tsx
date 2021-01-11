import { IPlayerCompact } from 'types/Player';
import TeamAPI from 'api/TeamAPI';
import { useSnackbar } from 'hooks/Snackbar';
import { useRouter } from 'next/router';

// Material UI
import ListItem from '@material-ui/core/ListItem';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import Avatar from '@material-ui/core/Avatar';
import IconButton from '@material-ui/core/IconButton';

// Icons
import ArrowDownIcon from '@material-ui/icons/ArrowDownwardRounded';
import ArrowUpIcon from '@material-ui/icons/ArrowUpwardRounded';

export type IProps = {
  teamId: number;
  player: IPlayerCompact;
  divider?: boolean;
  admin?: boolean;
};

const PlayerListItem = ({ teamId, player, divider = false, admin = false }: IProps) => {
  const router = useRouter();
  const showSnackbar = useSnackbar();
  const changeActiveStatus = async () => {
    try {
      await TeamAPI.updateTeamPlayer(teamId, player.id, { active: !player.active });
      showSnackbar(`${player.name} was switched to being an ${player.active ? 'former' : 'current'} player`, 'success');
      router.replace(router.asPath);
    } catch (e) {
      showSnackbar(e.message, 'error');
    }
  };
  return (
    <ListItem divider={divider}>
      <ListItemAvatar>
        <Avatar>{player.number || ''}</Avatar>
      </ListItemAvatar>
      <ListItemText primary={player.name} secondary={player.position} />
      {admin && (
        <ListItemSecondaryAction>
          <IconButton aria-label='Change active status' edge='end' onClick={changeActiveStatus}>
            {player.active ? <ArrowDownIcon /> : <ArrowUpIcon />}
          </IconButton>
        </ListItemSecondaryAction>
      )}
    </ListItem>
  );
};

export default PlayerListItem;
