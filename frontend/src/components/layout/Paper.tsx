import React from 'react';
import MaterialPaper from '@material-ui/core/Paper';
import classnames from 'classnames';
import { makeStyles, Theme } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) => ({
  border: {
    border: '1px solid ' + theme.palette.divider,
  },
  padding: {
    padding: theme.spacing(3),
    [theme.breakpoints.down('md')]: {
      padding: theme.spacing(2),
    },
  },
  noBorder: {
    border: 'none',
  },
  marginBottom: {
    marginBottom: theme.spacing(2),
  },
}));

export type PaperProps = {
  children: React.ReactNode;
  shadow?: boolean;
  border?: boolean;
  noPadding?: boolean;
  marginBottom?: boolean;
  className?: string;
};

const Paper = ({ shadow = false, border = false, noPadding = false, marginBottom = false, children, className }: PaperProps) => {
  const classes = useStyles();
  return (
    <MaterialPaper
      className={classnames(
        border && classes.border,
        marginBottom && classes.marginBottom,
        !noPadding && classes.padding,
        shadow && classes.noBorder,
        className,
      )}
      elevation={shadow ? 2 : 0}>
      {children}
    </MaterialPaper>
  );
};

export default Paper;
