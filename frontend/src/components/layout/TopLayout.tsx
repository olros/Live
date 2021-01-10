import React from 'react';
import classnames from 'classnames';
import { makeStyles, Theme } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) => ({
  topLayout: {
    display: 'grid',
    gridTemplateColumns: '1fr auto',
    gridGap: theme.spacing(1),
    marginBottom: theme.spacing(2),
    [theme.breakpoints.down('md')]: {
      gridTemplateColumns: '1fr',
    },
  },
}));

export type IProps = {
  children: React.ReactNode;
  className?: string;
};

const TopLayout = ({ children, className }: IProps) => {
  const classes = useStyles();
  return <div className={classnames(classes.topLayout, className)}>{children}</div>;
};

export default TopLayout;
