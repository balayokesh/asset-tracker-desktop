import { createTheme, responsiveFontSizes } from '@mui/material/styles';

let theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#8b5cf6',
      light: '#a78bfa',
      dark: '#7c3aed',
    },
    secondary: {
      main: '#06b6d4',
      light: '#67e8f9',
      dark: '#0e7490',
    },
    success: {
      main: '#10b981',
      light: '#6ee7b7',
      dark: '#059669',
    },
    background: {
      default: '#0b0f19',
      paper: '#0f172a',
    },
    text: {
      primary: '#e2e8f0',
      secondary: '#94a3b8',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontWeight: 900, letterSpacing: '-0.02em', lineHeight: 1.1 },
    h2: { fontWeight: 900, letterSpacing: '-0.02em', lineHeight: 1.15 },
    h3: { fontWeight: 800, letterSpacing: '-0.01em' },
    h4: { fontWeight: 700 },
    h5: { fontWeight: 700 },
    h6: { fontWeight: 700 },
    button: { textTransform: 'none', fontWeight: 600 },
  },
  shape: { borderRadius: 12 },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        html: { scrollBehavior: 'smooth' },
        'body::-webkit-scrollbar': { width: '8px' },
        'body::-webkit-scrollbar-track': { background: '#0b0f19' },
        'body::-webkit-scrollbar-thumb': {
          background: 'rgba(139,92,246,0.25)',
          borderRadius: '4px',
        },
        '@keyframes floatAnim': {
          '0%,100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-8px)' },
        },
        '@keyframes pulseGlow': {
          '0%,100%': { boxShadow: '0 0 20px 4px rgba(139,92,246,0.35)' },
          '50%': { boxShadow: '0 0 36px 8px rgba(139,92,246,0.55)' },
        },
      },
    },
  },
});

theme = responsiveFontSizes(theme);

export default theme;
