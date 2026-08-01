import { useState } from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import PictureAsPdfRoundedIcon from '@mui/icons-material/PictureAsPdfRounded';
import ImageRoundedIcon from '@mui/icons-material/ImageRounded';
import ScreenshotRoundedIcon from '@mui/icons-material/ScreenshotRounded';
import LinkRoundedIcon from '@mui/icons-material/LinkRounded';
import RecordVoiceOverRoundedIcon from '@mui/icons-material/RecordVoiceOverRounded';
import { GradientText } from './styled';
import Reveal from './Reveal';

const STEPS = [
  { num: 1, title: 'Add Product', body: "Enter your asset's name, purchase price, date, category, and store info in seconds." },
  { num: 2, title: 'Attach Documents', body: 'Drop in invoices, receipts, screenshots, purchase links, or even audio/video notes.' },
  { num: 3, title: 'Recover Anywhere', body: 'Search, find, and access your full asset history the moment you need it — offline, always.' },
];

const FORMATS = [
  { icon: <PictureAsPdfRoundedIcon sx={{ color: '#f87171', fontSize: 28 }} />, label: 'PDF Invoices' },
  { icon: <ImageRoundedIcon sx={{ color: '#6ee7b7', fontSize: 28 }} />, label: 'Image Receipts' },
  { icon: <ScreenshotRoundedIcon sx={{ color: '#67e8f9', fontSize: 28 }} />, label: 'Screenshots' },
  { icon: <LinkRoundedIcon sx={{ color: '#a78bfa', fontSize: 28 }} />, label: 'Purchase Links' },
  { icon: <RecordVoiceOverRoundedIcon sx={{ color: '#fbbf24', fontSize: 28 }} />, label: 'Audio & Video Notes' },
];

export default function HowItWorks() {
  const [activeStep, setActiveStep] = useState(1);

  return (
    <Box component="section" id="how-it-works" sx={{ py: { xs: 10, md: 16 }, px: { xs: 2, md: 3 }, position: 'relative', overflow: 'hidden' }}>
      <Box sx={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', width: 600, height: 400, borderRadius: '50%', bgcolor: 'rgba(139,92,246,0.05)', filter: 'blur(100px)', pointerEvents: 'none' }} />
      <Container maxWidth="lg" sx={{ position: 'relative', zIndex: 1 }}>
        <Reveal sx={{ textAlign: 'center', mb: { xs: 6, md: 8 } }}>
          <Typography sx={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.15em', color: 'secondary.main', mb: 1.5 }}>
            How It Works
          </Typography>
          <Typography variant="h2" sx={{ fontSize: { xs: '2.25rem', md: '3rem' } }}>
            Attach everything.
            <br />
            <GradientText>Find it instantly.</GradientText>
          </Typography>
          <Typography sx={{ mt: 2, color: 'text.secondary', fontSize: '1.1rem', maxWidth: 480, mx: 'auto' }}>
            Every asset you own can store a complete paper trail — digitally.
          </Typography>
        </Reveal>

        {/* Steps */}
        <Grid container spacing={{ xs: 2, md: 0 }} sx={{ mb: { xs: 6, md: 8 }, alignItems: 'center' }}>
          {STEPS.map((step, i) => (
            <Grid key={step.num} size={{ xs: 12, md: 4 }} sx={{ position: 'relative' }}>
              <Reveal delay={i * 100}>
                <Box
                  onClick={() => setActiveStep(step.num)}
                  sx={{ textAlign: 'center', px: 3, py: 2, cursor: 'pointer' }}
                >
                  <Box
                    sx={{
                      width: 56,
                      height: 56,
                      borderRadius: 4,
                      mx: 'auto',
                      mb: 2,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontWeight: 900,
                      fontSize: '1.25rem',
                      color: activeStep === step.num ? 'common.white' : 'text.secondary',
                      background: activeStep === step.num ? 'linear-gradient(135deg, #7c3aed, #06b6d4)' : 'rgba(30,41,59,0.8)',
                      border: activeStep === step.num ? 'none' : '1px solid rgba(255,255,255,0.1)',
                      boxShadow: activeStep === step.num ? '0 0 20px rgba(139,92,246,0.6)' : 'none',
                      transition: 'all 0.4s ease',
                    }}
                  >
                    {step.num}
                  </Box>
                  <Typography variant="h6" sx={{ fontSize: '1.1rem', mb: 1 }}>
                    {step.title}
                  </Typography>
                  <Typography sx={{ color: 'text.secondary', fontSize: '0.875rem', lineHeight: 1.7 }}>
                    {step.body}
                  </Typography>
                </Box>
              </Reveal>
            </Grid>
          ))}
        </Grid>

        {/* Attachment capabilities */}
        <Reveal>
          <Paper
            elevation={0}
            sx={{
              bgcolor: 'rgba(30,41,59,0.55)',
              backdropFilter: 'blur(18px)',
              border: '1px solid rgba(255,255,255,0.07)',
              borderRadius: 4,
              p: { xs: 3, md: 4 },
            }}
          >
            <Typography sx={{ textAlign: 'center', fontSize: '0.7rem', textTransform: 'uppercase', letterSpacing: '0.15em', color: 'text.secondary', fontWeight: 600, mb: 3 }}>
              Supported Attachment Formats
            </Typography>
            <Grid container spacing={2}>
              {FORMATS.map((fmt) => (
                <Grid key={fmt.label} size={{ xs: 6, sm: 4, md: 2.4 }}>
                  <Box
                    sx={{
                      borderRadius: 3,
                      px: 2,
                      py: 2.5,
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'center',
                      gap: 1,
                      textAlign: 'center',
                      bgcolor: 'rgba(139,92,246,0.1)',
                      border: '1px solid rgba(139,92,246,0.25)',
                      transition: 'background 0.3s, border-color 0.3s, transform 0.3s',
                      '&:hover': {
                        bgcolor: 'rgba(139,92,246,0.22)',
                        borderColor: 'rgba(139,92,246,0.6)',
                        transform: 'scale(1.05)',
                      },
                    }}
                  >
                    {fmt.icon}
                    <Typography sx={{ fontSize: '0.75rem', fontWeight: 600, color: 'text.primary' }}>
                      {fmt.label}
                    </Typography>
                  </Box>
                </Grid>
              ))}
            </Grid>
          </Paper>
        </Reveal>
      </Container>
    </Box>
  );
}
