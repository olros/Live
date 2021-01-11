import { useRouter } from 'next/router';
import URLS from 'URLS';
import { ILeague } from 'types/League';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Button, { ButtonProps } from '@material-ui/core/Button';
import TextField, { TextFieldProps } from '@material-ui/core/TextField';
import MenuItem from '@material-ui/core/MenuItem';

// Icons
import BackIcon from '@material-ui/icons/KeyboardBackspaceRounded';

// Project
import Link from 'components/navigation/Link';

const useStyles = makeStyles((theme) => ({
  flex: {
    display: 'flex',
    justifyContent: 'space-between',
  },
  select: {
    minWidth: 150,
  },
}));

export type IProps = TextFieldProps & {
  league: ILeague;
  selectedSeasonId: number;
  textFieldProps?: TextFieldProps;
  buttonProps?: ButtonProps;
};

const SeasonsSelect = ({ league, selectedSeasonId, textFieldProps, buttonProps }: IProps) => {
  const classes = useStyles();
  const router = useRouter();
  const goToSeason = (seasonId: number) => router.push(`${URLS.LEAGUES}/${league.id}/${seasonId}`);
  return (
    <div className={classes.flex}>
      <Link passHref to={`${URLS.LEAGUES}/${league.id}`}>
        <Button color='inherit' startIcon={<BackIcon />} {...buttonProps}>
          Back
        </Button>
      </Link>
      <TextField
        className={classes.select}
        label='Season'
        onChange={(e) => goToSeason(Number(e.target.value))}
        select
        value={selectedSeasonId}
        {...textFieldProps}>
        {league.seasons.map((season) => (
          <MenuItem key={season.id} value={season.id}>
            {season.name}
          </MenuItem>
        ))}
      </TextField>
    </div>
  );
};

export default SeasonsSelect;
