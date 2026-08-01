import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Chip from '@mui/material/Chip';
import ArrowDownwardRoundedIcon from '@mui/icons-material/ArrowDownwardRounded';
import DownloadRoundedIcon from '@mui/icons-material/DownloadRounded';
import { GlowButton, GradientText } from './styled';

export default function Hero() {
  return (
    <Box
      component="section"
      sx={{
        position: 'relative',
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        pt: { xs: 14, md: 16 },
        pb: { xs: 10, md: 12 },
        px: { xs: 2, md: 3 },
        overflow: 'hidden',
      }}
    >
      {/* Background atmosphere */}
      <Box
        sx={{
          position: 'absolute',
          inset: 0,
          background: 'radial-gradient(ellipse 80% 50% at 50% -20%, rgba(139,92,246,0.25), transparent)',
          pointerEvents: 'none',
        }}
      />
      <Box sx={{ position: 'absolute', top: '25%', left: '50%', transform: 'translateX(-50%)', width: 700, height: 700, borderRadius: '50%', bgcolor: 'rgba(139,92,246,0.05)', filter: 'blur(120px)', pointerEvents: 'none' }} />
      <Box sx={{ position: 'absolute', top: '33%', right: '25%', width: 300, height: 300, borderRadius: '50%', bgcolor: 'rgba(6,182,212,0.05)', filter: 'blur(80px)', pointerEvents: 'none' }} />

      <Container maxWidth="md" sx={{ position: 'relative', zIndex: 1, textAlign: 'center' }}>
        {/* Badge */}
        <Chip
          label="100% Free · Offline · No Account Required"
          sx={{
            mb: { xs: 3, md: 4 },
            bgcolor: 'rgba(30,41,59,0.55)',
            backdropFilter: 'blur(18px)',
            border: '1px solid rgba(255,255,255,0.07)',
            color: 'text.secondary',
            fontWeight: 600,
            fontSize: '0.7rem',
            letterSpacing: '0.08em',
            textTransform: 'uppercase',
            height: 32,
            '& .MuiChip-label': { px: 2 },
          }}
        />

        {/* Headline */}
        <Typography variant="h1" sx={{ fontSize: { xs: '2.75rem', sm: '3.5rem', md: '4.5rem' }, mb: 3 }}>
          Buy it. Track it.
          <br />
          <GradientText>Protect it.</GradientText>
        </Typography>

        {/* Sub-headline */}
        <Typography
          variant="h6"
          component="p"
          sx={{ color: 'text.secondary', fontWeight: 400, maxWidth: 640, mx: 'auto', mb: 5, fontSize: { xs: '1rem', md: '1.25rem' }, lineHeight: 1.6 }}
        >
          The offline personal asset locker. Easily keep purchase dates, prices, invoices, and warranty details handy for life — without the hassle of organizing or losing paper trails.
        </Typography>

        {/* CTAs */}
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} justifyContent="center" alignItems="center" sx={{ mb: 2 }}>
          <GlowButton href="https://github.com/balayokesh/asset-tracker-desktop" size="large" startIcon={<DownloadRoundedIcon />} sx={{ py: 1.5, px: 3.5, fontSize: '1rem', borderRadius: 3 }}>
            Download for Windows
          </GlowButton>
          <Box
            component="button"
            onClick={() => document.querySelector('#features')?.scrollIntoView({ behavior: 'smooth' })}
            sx={{
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              display: 'inline-flex',
              alignItems: 'center',
              gap: 0.75,
              color: 'text.secondary',
              fontWeight: 500,
              fontSize: '0.9rem',
              '&:hover': { color: 'common.white' },
              transition: 'color 0.2s',
            }}
          >
            Explore features
            <ArrowDownwardRoundedIcon sx={{ fontSize: 18 }} />
          </Box>
        </Stack>
        <Typography variant="caption" sx={{ color: 'rgba(148,163,184,0.6)', display: 'block', mb: 6 }}>
          100% Free &nbsp;·&nbsp; Offline &nbsp;·&nbsp; No Account Required
        </Typography>

        {/* App Mockup */}
        <Box sx={{ position: 'relative', maxWidth: 920, mx: 'auto' }}>
          {/* Glow */}
          <Box sx={{ position: 'absolute', left: 32, right: 32, bottom: 0, height: 96, bgcolor: 'rgba(139,92,246,0.2)', filter: 'blur(40px)', borderRadius: '50%', pointerEvents: 'none' }} />
          <Box
            sx={{
              borderRadius: 3,
              overflow: 'hidden',
              background: 'linear-gradient(135deg, rgba(15,23,42,0.95), rgba(30,41,59,0.9))',
              border: '1px solid rgba(139,92,246,0.25)',
              boxShadow: '0 0 80px rgba(139,92,246,0.18), 0 0 160px rgba(6,182,212,0.06), 0 32px 80px rgba(0,0,0,0.6)',
              animation: 'floatAnim 6s ease-in-out infinite',
            }}
          >
            {/* Window chrome */}
            <Stack direction="row" spacing={1} alignItems="center" sx={{ px: 2, py: 1.5, borderBottom: '1px solid rgba(255,255,255,0.05)', bgcolor: 'rgba(255,255,255,0.03)' }}>
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'rgba(239,68,68,0.8)' }} />
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'rgba(234,179,8,0.8)' }} />
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'rgba(34,197,94,0.8)' }} />
              <Box sx={{ flex: 1, ml: 1.5, height: 20, borderRadius: 1, bgcolor: 'rgba(255,255,255,0.05)', display: 'flex', alignItems: 'center', px: 1.5 }}>
                <Typography sx={{ fontSize: '0.7rem', color: 'rgba(148,163,184,0.5)', fontFamily: 'monospace' }}>Asset Tracker — Dashboard</Typography>
              </Box>
            </Stack>
            {/* Preview */}
            <Box
              component="img"
              src="/dashboard-preview.webp"
              alt="Asset Tracker Dashboard Preview"
              sx={{ width: '100%', display: 'block', objectFit: 'cover' }}
              loading="lazy"
            />
          </Box>
        </Box>
      </Container>
    </Box>
  );
}
