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
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          padding: 12,
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        variant: 'outlined',
      },
    },
  },
  shape: {
    borderRadius: 16,
  },
  spacing: 8,
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
  typography: {
    fontFamily: 'Inter, sans-serif',
    h1: {
      fontSize: '3.1rem',
      fontWeight: 900,
    },
    h2: {
      fontSize: '2.2rem',
      fontWeight: 700,
    },
    h3: {
      fontSize: '1.5rem',
    },
  },
});

export default theme;
