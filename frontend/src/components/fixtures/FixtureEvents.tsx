/* eslint-disable @typescript-eslint/no-unused-vars */
import { useEffect, useState } from 'react';
import { IFixture, IFixtureEventCompact } from 'types/Fixture';
import { useSnackbar } from 'hooks/Snackbar';
import { useRouter } from 'next/router';
import FixtureAPI from 'api/FixtureAPI';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';

import Timeline from '@material-ui/lab/Timeline';
import TimelineItem from '@material-ui/lab/TimelineItem';
import TimelineSeparator from '@material-ui/lab/TimelineSeparator';
import TimelineConnector from '@material-ui/lab/TimelineConnector';
import TimelineContent from '@material-ui/lab/TimelineContent';
import TimelineDot from '@material-ui/lab/TimelineDot';
import TimelineOppositeContent from '@material-ui/lab/TimelineOppositeContent';

// Icons
import DeleteIcon from '@material-ui/icons/DeleteOutlineRounded';

// Project
import Paper from 'components/layout/Paper';
import AddFixturePlayer from 'components/fixtures/AddFixturePlayer';

const useStyles = makeStyles(() => ({
  hideBlock: {
    display: 'none',
  },
}));

export type IEventProps = {
  event: IFixtureEventCompact;
  admin?: boolean;
};

const EventItem = ({ event, admin = false }: IEventProps) => {
  const classes = useStyles();
  const router = useRouter();
  const showSnackbar = useSnackbar();
  // const removeFromFixture = async () => {
  //   try {
  //     await FixtureAPI.deleteFixturePlayer(fixtureId, player.id);
  //     showSnackbar(`${player.player.name} was removed from the fixture`, 'success');
  //     router.replace(router.asPath);
  //   } catch (e) {
  //     showSnackbar(e.message, 'error');
  //   }
  // };

  return (
    <TimelineItem>
      <TimelineOppositeContent className={classes.hideBlock} />
      <TimelineSeparator>
        <TimelineDot color='primary' variant='outlined' />
        <TimelineConnector />
      </TimelineSeparator>
      <TimelineContent>Fixture</TimelineContent>
    </TimelineItem>
  );
};

export type IProps = {
  fixture: IFixture;
};

const FixtureEvents = ({ fixture }: IProps) => {
  const [events, setEvents] = useState<Array<IFixtureEventCompact>>([]);

  useEffect(() => {
    FixtureAPI.getAllFixtureEvents(fixture.id)
      .then(setEvents)
      .catch(() => null);
  }, [fixture.id]);

  return (
    <>
      <Paper marginBottom>
        <Timeline align='left'>
          {events.map((event) => (
            <EventItem event={event} key={event.id} />
          ))}
        </Timeline>
      </Paper>
      {/* {selectedTab === TABS.HOME && fixture.homeTeam.isAdmin && <AddFixturePlayer fixture={fixture} teamId={fixture.homeTeam.id} />} */}
      {/* {selectedTab === TABS.AWAY && fixture.awayTeam.isAdmin && <AddFixturePlayer fixture={fixture} teamId={fixture.awayTeam.id} />} */}
    </>
  );
};

export default FixtureEvents;
