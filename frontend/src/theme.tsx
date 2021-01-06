import { createMuiTheme } from '@material-ui/core/styles';
import { red } from '@material-ui/core/colors';

// Create a theme instance.
const theme = createMuiTheme({
  breakpoints: {
    values: {
      xs: 0,
      sm: 400,
      md: 600,
      lg: 900,
      xl: 1200,
    },
  },
  shape: {
    borderRadius: 12,
  },
  spacing: 10,
  palette: {
    primary: {
      main: '#58a6ff',
    },
    secondary: {
      main: '#238636',
    },
    error: {
      main: red.A400,
    },
    mode: 'dark',
    background: {
      default: '#0d1117',
      paper: '#161b22',
    },
  },
});

export default theme;
