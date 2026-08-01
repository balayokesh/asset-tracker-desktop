import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Inventory2RoundedIcon from '@mui/icons-material/Inventory2Rounded';

const NAV_LINKS = [
  { label: 'Features', href: '#features' },
  { label: 'How It Works', href: '#how-it-works' },
  { label: 'FAQ', href: '#faq' },
];

export default function Footer() {
  return (
    <Box
      component="footer"
      sx={{
        borderTop: '1px solid rgba(255,255,255,0.05)',
        py: 5,
        px: { xs: 2, md: 3 },
        mt: 2,
      }}
    >
      <Container maxWidth="lg">
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          justifyContent="space-between"
          alignItems="center"
          spacing={3}
        >
          {/* Brand */}
          <Stack direction="row" spacing={1.25} alignItems="center">
            <Box
              sx={{
                width: 28,
                height: 28,
                borderRadius: 1.5,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #7c3aed, #8b5cf6 50%, #06b6d4)',
              }}
            >
              <Inventory2RoundedIcon sx={{ color: 'common.white', fontSize: 16 }} />
            </Box>
            <Typography sx={{ fontWeight: 700, fontSize: '0.875rem', color: 'text.primary' }}>
              Asset Tracker
            </Typography>
          </Stack>

          {/* Nav links */}
          <Stack direction="row" spacing={3}>
            {NAV_LINKS.map((link) => (
              <Button
                key={link.href}
                onClick={() => document.querySelector(link.href)?.scrollIntoView({ behavior: 'smooth' })}
                sx={{ color: 'text.secondary', fontSize: '0.75rem', minWidth: 0, p: 0, '&:hover': { color: 'text.primary' } }}
              >
                {link.label}
              </Button>
            ))}
          </Stack>

          {/* Copyright & privacy note */}
          <Box sx={{ textAlign: { xs: 'center', sm: 'right' } }}>
            <Typography sx={{ fontSize: '0.75rem', color: 'text.secondary' }}>
              © 2025 Asset Tracker. All rights reserved.
            </Typography>
            <Typography sx={{ fontSize: '0.75rem', color: 'rgba(148,163,184,0.5)', mt: 0.5 }}>
              Built for privacy. Runs 100% locally on your computer.
            </Typography>
          </Box>
        </Stack>
      </Container>
    </Box>
  );
}
