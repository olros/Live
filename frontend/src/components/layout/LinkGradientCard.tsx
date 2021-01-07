import React from 'react';
import classnames from 'classnames';

// Material UI
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';

// Project
import Link from 'components/navigation/Link';

const useStyles = makeStyles((theme) => ({
  border: {
    border: '1px solid ' + theme.palette.divider,
  },
  padding: {
    padding: theme.spacing(3),
    [theme.breakpoints.down('md')]: {
      padding: theme.spacing(2),
    },
  },
}));

export type LinkGradientCardProps = {
  gradientFrom: React.CSSProperties['backgroundColor'];
  gradientTo: React.CSSProperties['backgroundColor'];
  degree?: number;
  to: string;
  children?: React.ReactNode;
  border?: boolean;
  noPadding?: boolean;
  className?: string;
};

const LinkGradientCard = ({ gradientFrom, gradientTo, degree = 45, to, border, noPadding, children, className }: LinkGradientCardProps) => {
  const classes = useStyles();
  return (
    <Card
      className={classnames(border && classes.border, className)}
      elevation={0}
      style={{ background: `linear-gradient(${degree}deg, ${gradientFrom}, ${gradientTo})` }}>
      <Link passHref to={to}>
        <CardActionArea>
          <CardContent className={classnames(!noPadding && classes.padding)}>{children}</CardContent>
        </CardActionArea>
      </Link>
    </Card>
  );
};

export default LinkGradientCard;
