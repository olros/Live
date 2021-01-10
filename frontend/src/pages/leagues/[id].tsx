import { GetServerSideProps } from 'next';
import { useState } from 'react';
import URLS from 'URLS';
import { getAuthTokenServer } from 'utils';
import { ILeague } from 'types/League';
import LeagueAPI from 'api/LeagueAPI';

// Material UI
import Typography from '@material-ui/core/Typography';
import Collapse from '@material-ui/core/Collapse';

// Project
import Navigation from 'components/navigation/Navigation';
import TopLayout from 'components/layout/TopLayout';
import Tabs from 'components/layout/Tabs';
import LeagueAdmin from 'components/leagues/LeagueAdmin';

export type IProps = {
  league: ILeague;
};

enum TABS {
  MAIN,
  ADMIN,
}

// TODO: Change endpoint to pagination
const League = ({ league }: IProps) => {
  const selectableTabs = [
    { label: 'Main', value: TABS.MAIN },
    { label: 'Admin', value: TABS.ADMIN },
  ];
  const [selectedTab, setSelectedTab] = useState(TABS.MAIN);
  return (
    <Navigation>
      <TopLayout>
        <Typography variant='h1'>{league.name}</Typography>
        {league.isAdmin && <Tabs selected={selectedTab} setSelected={setSelectedTab} tabs={selectableTabs} />}
      </TopLayout>
      <Collapse in={selectedTab === TABS.MAIN}></Collapse>
      <Collapse in={selectedTab === TABS.ADMIN} mountOnEnter>
        <LeagueAdmin league={league} />
      </Collapse>
    </Navigation>
  );
};

export const getServerSideProps: GetServerSideProps = async ({ req, query }) => {
  try {
    const token = getAuthTokenServer(req.headers.cookie);
    const { id } = query;
    const league = await LeagueAPI.getLeagueById(Number(id), token);
    const data: IProps = { league };
    return { props: data };
  } catch (e) {
    return {
      notFound: true,
    };
  }
};

export default League;
