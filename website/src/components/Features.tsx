import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Chip from '@mui/material/Chip';
import LockRoundedIcon from '@mui/icons-material/LockRounded';
import VerifiedUserRoundedIcon from '@mui/icons-material/VerifiedUserRounded';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import { GlassCard, GradientText } from './styled';
import Reveal from './Reveal';

const FEATURES = [
  {
    icon: <LockRoundedIcon sx={{ color: 'primary.light', fontSize: 26 }} />,
    title: '100% Offline & Private',
    body: 'Your data stays on your machine. No accounts, no cloud sync, no monthly subscriptions, and zero privacy worries. You own your data, completely.',
    chips: [
      { label: 'No Cloud', color: 'primary' as const },
      { label: 'No Account', color: 'success' as const },
      { label: 'Local Storage', color: 'secondary' as const },
    ],
  },
  {
    icon: <VerifiedUserRoundedIcon sx={{ color: 'secondary.main', fontSize: 26 }} />,
    title: 'Warranty Expiration Tracking',
    body: 'Never miss a warranty claim again. Automatic visual indicators keep you informed before your coverage expires — so you can act, not scramble.',
    chips: [
      { label: 'Auto Alerts', color: 'secondary' as const },
      { label: 'Visual Indicators', color: 'primary' as const },
    ],
  },
  {
    icon: <SearchRoundedIcon sx={{ color: 'success.light', fontSize: 26 }} />,
    title: 'One-Click Search & Recovery',
    body: 'Search by name or category instantly. Access bills, invoices, receipts, and audio notes with one click the moment an issue occurs. No digging required.',
    chips: [
      { label: 'Instant Search', color: 'success' as const },
      { label: 'By Category', color: 'primary' as const },
    ],
  },
];

const chipColorMap = {
  primary: { bgcolor: 'rgba(139,92,246,0.1)', border: 'rgba(139,92,246,0.2)', color: '#a78bfa' },
  secondary: { bgcolor: 'rgba(6,182,212,0.1)', border: 'rgba(6,182,212,0.2)', color: '#67e8f9' },
  success: { bgcolor: 'rgba(16,185,129,0.1)', border: 'rgba(16,185,129,0.2)', color: '#6ee7b7' },
};

export default function Features() {
  return (
    <Box component="section" id="features" sx={{ py: { xs: 10, md: 16 }, px: { xs: 2, md: 3 } }}>
      <Container maxWidth="lg">
        <Reveal sx={{ textAlign: 'center', mb: { xs: 6, md: 8 } }}>
          <Typography sx={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.15em', color: 'primary.main', mb: 1.5 }}>
            Core Features
          </Typography>
          <Typography variant="h2" sx={{ fontSize: { xs: '2.25rem', md: '3rem' } }}>
            Everything you need.
            <br />
            <GradientText>Nothing you don't.</GradientText>
          </Typography>
          <Typography sx={{ mt: 2, color: 'text.secondary', fontSize: '1.1rem', maxWidth: 480, mx: 'auto' }}>
            Designed to be dead-simple, lightning-fast, and completely private.
          </Typography>
        </Reveal>

        <Grid container spacing={3}>
          {FEATURES.map((feature, i) => (
            <Grid key={feature.title} size={{ xs: 12, md: 4 }}>
              <Reveal delay={i * 100}>
                <GlassCard sx={{ height: '100%', borderRadius: 4, p: 4, display: 'flex', flexDirection: 'column', gap: 2.5 }}>
                  <Box
                    sx={{
                      width: 48,
                      height: 48,
                      borderRadius: 3,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0,
                      background: 'linear-gradient(135deg, rgba(139,92,246,0.18), rgba(139,92,246,0.06))',
                      border: '1px solid rgba(139,92,246,0.3)',
                    }}
                  >
                    {feature.icon}
                  </Box>
                  <Box>
                    <Typography variant="h5" sx={{ fontSize: '1.25rem', mb: 1 }}>
                      {feature.title}
                    </Typography>
                    <Typography sx={{ color: 'text.secondary', fontSize: '0.9rem', lineHeight: 1.7 }}>
                      {feature.body}
                    </Typography>
                  </Box>
                  <Stack direction="row" spacing={1} sx={{ flexWrap: 'wrap', gap: 1, mt: 'auto' }}>
                    {feature.chips.map((chip) => (
                      <Chip
                        key={chip.label}
                        label={chip.label}
                        size="small"
                        variant="outlined"
                        sx={{
                          bgcolor: chipColorMap[chip.color].bgcolor,
                          borderColor: chipColorMap[chip.color].border,
                          color: chipColorMap[chip.color].color,
                          fontWeight: 500,
                          fontSize: '0.75rem',
                          height: 26,
                        }}
                      />
                    ))}
                  </Stack>
                </GlassCard>
              </Reveal>
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
}
