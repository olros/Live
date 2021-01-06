import Head from 'next/head';

// Material UI
import Button from '@material-ui/core/Button';

// Project
import Navigation from 'components/navigation/Navigation';

export default function Home() {
  return (
    <>
      <Head>
        <title>Live - Hjem</title>
      </Head>

      <Navigation>
        <Button variant='outlined'>Button</Button>
      </Navigation>
    </>
  );
}
