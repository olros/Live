/* eslint-disable @typescript-eslint/no-explicit-any */
import MuiTabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
  tabsRoot: {
    backgroundColor: theme.palette.background.paper,
    borderRadius: theme.shape.borderRadius,
    minHeight: 44,
  },
  tabsFlexContainer: {
    display: 'inline-flex',
    position: 'relative',
    zIndex: 1,
    width: '100%',
  },
  tabsScroller: {
    [theme.breakpoints.up('md')]: {
      padding: '0 8px',
    },
  },
  tabsIndicator: {
    top: 3,
    bottom: 3,
    right: 3,
    height: 'auto',
    background: 'none',
    '&:after': {
      content: '""',
      display: 'block',
      position: 'absolute',
      top: 0,
      left: 4,
      right: 4,
      bottom: 0,
      borderRadius: 8,
      backgroundColor: theme.palette.secondary.main,
      boxShadow: '0 4px 12px 0 rgba(0,0,0,0.16)',
    },
  },
  tabRoot: {
    '&:hover': {
      opacity: 1,
    },
    padding: theme.spacing(2),
    minHeight: 44,
    minWidth: 96,
    [theme.breakpoints.up('md')]: {
      minWidth: 120,
    },
  },
  tabWrapper: {
    color: theme.palette.text.primary,
    textTransform: 'initial',
    whiteSpace: 'nowrap',
  },
  marginBottom: {
    marginBottom: theme.spacing(2),
  },
}));

export type IProps = {
  selected: any;
  setSelected: (newSelected: any) => void;
  tabs: Array<{
    label: string;
    value: number | string;
  }>;
  marginBottom?: boolean;
};

const Tabs = ({ selected, setSelected, tabs, marginBottom = false }: IProps) => {
  const classes = useStyles();
  return (
    <MuiTabs
      className={marginBottom ? classes.marginBottom : ''}
      classes={{ root: classes.tabsRoot, flexContainer: classes.tabsFlexContainer, indicator: classes.tabsIndicator, scroller: classes.tabsScroller }}
      onChange={(e, newValue) => setSelected(newValue)}
      value={selected}
      variant='fullWidth'>
      {tabs.map((tab, i) => (
        <Tab classes={{ root: classes.tabRoot, wrapper: classes.tabWrapper }} disableRipple key={i} label={tab.label} value={tab.value} />
      ))}
    </MuiTabs>
  );
};

export default Tabs;
