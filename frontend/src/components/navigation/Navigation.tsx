import { ReactNode, useEffect } from 'react';
import PropTypes from 'prop-types';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import LinearProgress from '@material-ui/core/LinearProgress';
import Container from '@material-ui/core/Container';

// Project Components
import Footer from 'components/navigation/Footer';
import TopBar from 'components/navigation/TopBar';

const useStyles = makeStyles((theme) => ({
  main: {
    minHeight: '101vh',
  },
  container: {
    [theme.breakpoints.down('sm')]: {
      paddingRight: theme.spacing(2),
      paddingLeft: theme.spacing(2),
    },
  },
}));

export type NavigationProps = {
  children?: ReactNode;
  isLoading?: boolean;
  noFooter?: boolean;
  maxWidth?: false | 'xs' | 'sm' | 'md' | 'lg' | 'xl';
};

function Navigation({ children, isLoading, noFooter, maxWidth }: NavigationProps) {
  const classes = useStyles();

  return (
    <>
      <TopBar />
      {isLoading ? (
        <LinearProgress />
      ) : (
        <main className={classes.main}>
          {maxWidth === false ? (
            <>{children}</>
          ) : (
            <Container className={classes.container} maxWidth={maxWidth || 'lg'}>
              {children}
            </Container>
          )}
        </main>
      )}
      {!noFooter &&
        !isLoading &&
        (maxWidth === false ? (
          <Footer />
        ) : (
          <Container className={classes.container} maxWidth={maxWidth || 'lg'}>
            <Footer />
          </Container>
        ))}
    </>
  );
}

Navigation.propTypes = {
  children: PropTypes.node,
  isLoading: PropTypes.bool,
  noFooter: PropTypes.bool,
  maxWidth: PropTypes.oneOfType([PropTypes.bool, PropTypes.oneOf(['xs', 'sm', 'md', 'lg', 'xl'])]),
};

export default Navigation;
