import { GetServerSideProps } from 'next';
import { useEffect, useState } from 'react';
import { getAuthTokenServer, formatDate } from 'utils';
import { IFixture } from 'types/Fixture';
import { ITableEntry } from 'types/Season';
import FixtureAPI from 'api/FixtureAPI';
import SeasonAPI from 'api/SeasonAPI';
import parseISO from 'date-fns/parseISO';
import isPast from 'date-fns/isPast';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Collapse from '@material-ui/core/Collapse';
import Container from '@material-ui/core/Container';

// Project
import Navigation from 'components/navigation/Navigation';
import Tabs from 'components/layout/Tabs';
import FixtureAdmin from 'components/fixtures/FixtureAdmin';
import FixturePlayers from 'components/fixtures/FixturePlayers';
import FixtureEvents from 'components/fixtures/FixtureEvents';
import SeasonTable from 'components/seasons/SeasonTable';

const useStyles = makeStyles((theme) => ({
  container: {
    [theme.breakpoints.down('sm')]: {
      paddingRight: theme.spacing(2),
      paddingLeft: theme.spacing(2),
    },
  },
  top: {
    width: '100%',
    height: 400,
    marginBottom: theme.spacing(5),
    position: 'relative',
    background: 'url("/football-field.jpeg")',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    [theme.breakpoints.down('md')]: {
      height: 300,
    },
  },
  darken: {
    background: `linear-gradient(to bottom, transparent, ${theme.palette.background.default})`,
    position: 'absolute',
    top: 0,
    bottom: -1,
    right: 0,
    left: 0,
  },
  content: {
    position: 'absolute',
    top: '25%',
    left: 0,
    right: 0,
  },
  score: {
    display: 'grid',
    gridTemplateColumns: '1fr auto 1fr',
    gridGap: theme.spacing(2),
    marginBottom: theme.spacing(2),
    '& h1': {
      fontSize: '5rem',
      alignSelf: 'center',
      [theme.breakpoints.down('md')]: {
        fontSize: '2rem',
      },
    },
  },
}));

export type IProps = {
  fixture: IFixture;
};

enum ADMIN_TABS {
  MAIN,
  ADMIN,
}
enum USER_TABS {
  EVENTS,
  PLAYERS,
  TABLE,
}
const adminTabs = [
  { label: 'Main', value: ADMIN_TABS.MAIN },
  { label: 'Settings', value: ADMIN_TABS.ADMIN },
];
const userTabs = [
  { label: 'Events', value: USER_TABS.EVENTS },
  { label: 'Players', value: USER_TABS.PLAYERS },
  { label: 'Table', value: USER_TABS.TABLE },
];

const Fixture = ({ fixture }: IProps) => {
  const classes = useStyles();
  const [selectedAdminTab, setSelectedAdminTab] = useState(ADMIN_TABS.MAIN);
  const [selectedUserTab, setSelectedUserTab] = useState(USER_TABS.EVENTS);
  const [table, setTable] = useState<Array<ITableEntry> | null>(null);

  useEffect(() => {
    if (selectedUserTab === USER_TABS.TABLE && !table) {
      SeasonAPI.getSeasonTable(fixture.season.id)
        .then(setTable)
        .catch(() => setTable([]));
    }
  }, [selectedUserTab, fixture.season.id, table]);

  return (
    <Navigation maxWidth={false} noTopbar>
      <div className={classes.top}>
        <div className={classes.darken} />
        <div className={classes.content}>
          <div className={classes.score}>
            <Typography align='right' variant='h1'>
              {fixture.homeTeam.name}
            </Typography>
            <Typography align='center' variant='h1'>{`${isPast(parseISO(fixture.time)) ? fixture.result.homeTeam : ''} - ${
              isPast(parseISO(fixture.time)) ? fixture.result.awayTeam : ''
            }`}</Typography>
            <Typography align='left' variant='h1'>
              {fixture.awayTeam.name}
            </Typography>
          </div>
          <Typography align='center' variant='subtitle1'>
            Time: {formatDate(parseISO(fixture.time))}
          </Typography>
          <Typography align='center' variant='subtitle1'>
            Location: {fixture.location || 'Not set'}
          </Typography>
          <Typography align='center' variant='subtitle1'>
            Referee: {fixture.referee || 'Not set'}
          </Typography>
        </div>
      </div>
      <Container className={classes.container} maxWidth={'lg'}>
        {fixture.isAdmin && <Tabs marginBottom selected={selectedAdminTab} setSelected={setSelectedAdminTab} tabs={adminTabs} />}
        <Collapse in={selectedAdminTab === ADMIN_TABS.MAIN}>
          <Tabs marginBottom selected={selectedUserTab} setSelected={setSelectedUserTab} tabs={userTabs} />
          <Collapse in={selectedUserTab === USER_TABS.EVENTS}>
            <FixtureEvents fixture={fixture} />
          </Collapse>
          <Collapse in={selectedUserTab === USER_TABS.PLAYERS} mountOnEnter>
            <FixturePlayers fixture={fixture} />
          </Collapse>
          <Collapse in={selectedUserTab === USER_TABS.TABLE && Boolean(table)} mountOnEnter>
            <SeasonTable table={table || []} />
          </Collapse>
        </Collapse>
        <Collapse in={selectedAdminTab === ADMIN_TABS.ADMIN} mountOnEnter>
          <FixtureAdmin fixture={fixture} />
        </Collapse>
      </Container>
    </Navigation>
  );
};

export const getServerSideProps: GetServerSideProps = async ({ req, query }) => {
  try {
    const token = getAuthTokenServer(req.headers.cookie);
    const { id } = query;
    const fixture = await FixtureAPI.getFixture(Number(id), token);
    const data: IProps = { fixture };
    return { props: data };
  } catch (e) {
    return {
      notFound: true,
    };
  }
};

export default Fixture;
