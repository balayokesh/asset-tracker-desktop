import { styled, alpha } from '@mui/material/styles';
import Button from '@mui/material/Button';

export const GradientText = styled('span')(({ theme }) => ({
  background: `linear-gradient(135deg, ${theme.palette.primary.light}, ${theme.palette.secondary.main} 60%, ${theme.palette.success.light})`,
  WebkitBackgroundClip: 'text',
  WebkitTextFillColor: 'transparent',
  backgroundClip: 'text',
}));

export const GlowButton = styled(Button)(({ theme }) => ({
  background: `linear-gradient(135deg, ${theme.palette.primary.dark}, ${theme.palette.primary.main} 50%, ${theme.palette.secondary.main})`,
  backgroundSize: '200% 200%',
  color: theme.palette.common.white,
  transition: 'background-position 0.4s ease, box-shadow 0.35s ease, transform 0.2s ease',
  boxShadow: `0 0 22px ${alpha(theme.palette.primary.main, 0.4)}`,
  '&:hover': {
    backgroundPosition: 'right center',
    boxShadow: `0 0 36px ${alpha(theme.palette.primary.main, 0.65)}`,
    transform: 'translateY(-2px)',
  },
  '&:active': {
    transform: 'translateY(0)',
  },
}));

export const GlassCard = styled('div')(({ theme }) => ({
  background: `linear-gradient(135deg, ${alpha(theme.palette.background.paper, 0.7)}, ${alpha(theme.palette.background.default, 0.8)})`,
  backdropFilter: 'blur(20px)',
  WebkitBackdropFilter: 'blur(20px)',
  border: `1px solid ${alpha(theme.palette.primary.main, 0.15)}`,
  transition: 'border-color 0.35s ease, box-shadow 0.35s ease, transform 0.35s ease',
  '&:hover': {
    borderColor: alpha(theme.palette.primary.main, 0.55),
    boxShadow: `0 0 36px ${alpha(theme.palette.primary.main, 0.18)}, 0 8px 32px rgba(0,0,0,0.4)`,
    transform: 'translateY(-4px)',
  },
}));
