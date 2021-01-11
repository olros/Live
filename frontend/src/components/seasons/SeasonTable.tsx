import { ITableEntry } from 'types/Season';
import URLS from 'URLS';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Avatar from '@material-ui/core/Avatar';
import Typography from '@material-ui/core/Typography';

// Project
import Paper from 'components/layout/Paper';
import Link from 'components/navigation/Link';

const useStyles = makeStyles((theme) => ({
  field: {
    margin: theme.spacing(1, 0),
  },
  header: {
    display: 'grid',
    textAlign: 'center',
    gridTemplateColumns: 'repeat(4, 16px) 46px 16px',
    gridGap: theme.spacing(2),
    justifyContent: 'flex-end',
    padding: theme.spacing(1, 2),
    borderBottom: `1px solid ${theme.palette.divider}`,
    [theme.breakpoints.down('md')]: {
      gridGap: theme.spacing(1),
    },
    [theme.breakpoints.down('sm')]: {
      gridGap: theme.spacing(0.5),
    },
  },
  entry: {
    display: 'grid',
    textAlign: 'center',
    gridTemplateColumns: '20px 1fr repeat(4, 16px) 46px 16px',
    gridGap: theme.spacing(2),
    [theme.breakpoints.down('md')]: {
      gridGap: theme.spacing(1),
    },
    [theme.breakpoints.down('sm')]: {
      gridGap: theme.spacing(0.5),
    },
  },
  avatar: {
    height: 24,
    width: 24,
    color: theme.palette.common.white,
    background: theme.palette.divider,
    fontSize: 12,
  },
}));

export type IEntryProps = {
  entry: ITableEntry;
  divider: boolean;
};

const Entry = ({ entry, divider }: IEntryProps) => {
  const classes = useStyles();
  return (
    <ListItem className={classes.entry} divider={divider}>
      <Avatar className={classes.avatar}>{entry.rank}</Avatar>
      <Link to={`${URLS.TEAMS}/${entry.team.id}`}>
        <Typography align='left'>{entry.team.name}</Typography>
      </Link>
      <Typography>{entry.played}</Typography>
      <Typography>{entry.wins}</Typography>
      <Typography>{entry.draws}</Typography>
      <Typography>{entry.losses}</Typography>
      <Typography>
        {entry.goalsFor} - {entry.goalsAgainst}
      </Typography>
      <Typography>{entry.points}</Typography>
    </ListItem>
  );
};

export type IProps = {
  table: Array<ITableEntry>;
};

const SeasonTable = ({ table }: IProps) => {
  const classes = useStyles();

  return (
    <Paper>
      <div className={classes.header}>
        <Typography>M</Typography>
        <Typography>W</Typography>
        <Typography>D</Typography>
        <Typography>L</Typography>
        <Typography>G</Typography>
        <Typography>Pts</Typography>
      </div>
      <List>
        {table.map((entry) => (
          <Entry divider={entry.rank !== table[table.length - 1].rank} entry={entry} key={entry.rank} />
        ))}
      </List>
    </Paper>
  );
};

export default SeasonTable;
