import { useState } from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import ExpandMoreRoundedIcon from '@mui/icons-material/ExpandMoreRounded';
import { alpha } from '@mui/material/styles';
import Reveal from './Reveal';

const FAQS = [
  {
    q: 'Is Asset Tracker really free?',
    a: 'Yes — 100% free, forever. There are no premium tiers, subscriptions, or hidden paywalls. Asset Tracker is a local desktop app that you download once and use indefinitely.',
  },
  {
    q: 'Where is my data stored?',
    a: 'All your data lives entirely on your local machine — in a secure local database on your computer. Nothing is ever sent to the internet or a third-party server.',
  },
  {
    q: 'What file types can I attach?',
    a: 'You can attach PDFs, images (JPG, PNG, WEBP), screenshots, links, audio notes, and video clips. Basically any file relevant to your asset\u2019s purchase or warranty.',
  },
  {
    q: 'Does it work without internet?',
    a: 'Absolutely. Asset Tracker is built to be fully offline-first. Internet is never required — not to install, run, search, or retrieve any of your files.',
  },
  {
    q: 'Which operating systems are supported?',
    a: 'Currently Asset Tracker is available for Windows. macOS and Linux versions are planned for a future release.',
  },
];

export default function FAQ() {
  const [open, setOpen] = useState<number | null>(0);

  return (
    <Box component="section" id="faq" sx={{ py: { xs: 10, md: 16 }, px: { xs: 2, md: 3 } }}>
      <Container maxWidth="md">
        <Reveal sx={{ textAlign: 'center', mb: 7 }}>
          <Typography sx={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.15em', color: 'primary.main', mb: 1.5 }}>
            FAQ
          </Typography>
          <Typography variant="h2" sx={{ fontSize: { xs: '2.25rem', md: '2.75rem' } }}>
            Common Questions
          </Typography>
        </Reveal>

        <Stack spacing={2}>
          {FAQS.map((faq, i) => {
            const isOpen = open === i;
            return (
              <Reveal key={faq.q} delay={i * 50}>
                <Paper
                  elevation={0}
                  sx={{
                    bgcolor: 'rgba(30,41,59,0.7)',
                    backdropFilter: 'blur(20px)',
                    border: `1px solid ${isOpen ? alpha('#8b5cf6', 0.4) : alpha('#8b5cf6', 0.12)}`,
                    borderRadius: 4,
                    overflow: 'hidden',
                    transition: 'border-color 0.35s ease',
                  }}
                >
                  <Stack
                    direction="row"
                    justifyContent="space-between"
                    alignItems="center"
                    onClick={() => setOpen(isOpen ? null : i)}
                    sx={{ px: 3, py: 2.5, cursor: 'pointer', userSelect: 'none' }}
                  >
                    <Typography sx={{ fontWeight: 600, color: 'common.white', fontSize: { xs: '0.95rem', sm: '1rem' }, pr: 2 }}>
                      {faq.q}
                    </Typography>
                    <IconButton
                      size="small"
                      sx={{
                        color: 'primary.main',
                        transform: isOpen ? 'rotate(180deg)' : 'rotate(0deg)',
                        transition: 'transform 0.3s ease',
                      }}
                      aria-label="Toggle answer"
                    >
                      <ExpandMoreRoundedIcon />
                    </IconButton>
                  </Stack>
                  <Box
                    sx={{
                      maxHeight: isOpen ? 200 : 0,
                      opacity: isOpen ? 1 : 0,
                      overflow: 'hidden',
                      transition: 'max-height 0.4s ease, opacity 0.4s ease',
                    }}
                  >
                    <Typography sx={{ px: 3, pb: 3, color: 'text.secondary', fontSize: '0.9rem', lineHeight: 1.7 }}>
                      {faq.a}
                    </Typography>
                  </Box>
                </Paper>
              </Reveal>
            );
          })}
        </Stack>
      </Container>
    </Box>
  );
}
