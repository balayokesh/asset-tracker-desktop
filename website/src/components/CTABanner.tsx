import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import Chip from '@mui/material/Chip';
import DownloadRoundedIcon from '@mui/icons-material/DownloadRounded';
import { GlowButton, GradientText } from './styled';
import Reveal from './Reveal';

export default function CTABanner() {
  return (
    <Box component="section" sx={{ py: { xs: 8, md: 12 }, px: { xs: 2, md: 3 } }}>
      <Container maxWidth="md">
        <Reveal>
          <Box
            sx={{
              position: 'relative',
              borderRadius: 6,
              overflow: 'hidden',
              p: { xs: 5, md: 7 },
              textAlign: 'center',
              background: 'linear-gradient(135deg, rgba(15,23,42,0.9), rgba(30,41,59,0.8))',
              border: '1px solid rgba(139,92,246,0.25)',
            }}
          >
            {/* Glow */}
            <Box sx={{ position: 'absolute', inset: 0, background: 'radial-gradient(ellipse 70% 60% at 50% 50%, rgba(139,92,246,0.18), rgba(6,182,212,0.08), transparent)', pointerEvents: 'none' }} />
            <Box sx={{ position: 'absolute', top: -80, right: -80, width: 256, height: 256, borderRadius: '50%', bgcolor: 'rgba(139,92,246,0.1)', filter: 'blur(60px)', pointerEvents: 'none' }} />
            <Box sx={{ position: 'absolute', bottom: -80, left: -80, width: 256, height: 256, borderRadius: '50%', bgcolor: 'rgba(6,182,212,0.08)', filter: 'blur(60px)', pointerEvents: 'none' }} />

            <Box sx={{ position: 'relative', zIndex: 1 }}>
              <Chip
                label="Get Started Today"
                sx={{
                  mb: 3,
                  bgcolor: 'rgba(30,41,59,0.55)',
                  backdropFilter: 'blur(18px)',
                  border: '1px solid rgba(255,255,255,0.07)',
                  color: 'text.secondary',
                  fontWeight: 600,
                  fontSize: '0.7rem',
                  letterSpacing: '0.08em',
                  textTransform: 'uppercase',
                  height: 30,
                }}
              />
              <Typography variant="h3" sx={{ fontSize: { xs: '1.75rem', sm: '2.25rem', md: '2.75rem' }, mb: 2, lineHeight: 1.2 }}>
                Ready to organize your
                <br />
                <GradientText>physical assets?</GradientText>
              </Typography>
              <Typography sx={{ color: 'text.secondary', fontSize: '1.1rem', mb: 4, maxWidth: 460, mx: 'auto' }}>
                Start tracking your appliances, electronics, and valuable items today.
              </Typography>
              <GlowButton href="https://github.com/balayokesh/asset-tracker-desktop" size="large" startIcon={<DownloadRoundedIcon />} sx={{ py: 1.75, px: 4, fontSize: '1rem', borderRadius: 3, animation: 'pulseGlow 3s ease-in-out infinite' }}>
                Download Asset Tracker Now
              </GlowButton>
              <Typography variant="caption" sx={{ display: 'block', mt: 2, color: 'rgba(148,163,184,0.5)' }}>
                100% Free &nbsp;·&nbsp; No Sign-Up &nbsp;·&nbsp; Runs Locally
              </Typography>
            </Box>
          </Box>
        </Reveal>
      </Container>
    </Box>
  );
}
