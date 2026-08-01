import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import { useEffect, useState } from 'react';
import Inventory2RoundedIcon from '@mui/icons-material/Inventory2Rounded';
import MenuRoundedIcon from '@mui/icons-material/MenuRounded';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import DownloadRoundedIcon from '@mui/icons-material/DownloadRounded';
import { GlowButton } from './styled';

const NAV_LINKS = [
  { label: 'Features', href: '#features' },
  { label: 'How It Works', href: '#how-it-works' },
  { label: 'FAQ', href: '#faq' },
];

export default function Header() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const handleNav = (href: string) => {
    setMobileOpen(false);
    const el = document.querySelector(href);
    if (el) el.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <>
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          bgcolor: scrolled ? 'rgba(11,15,25,0.92)' : 'transparent',
          backdropFilter: scrolled ? 'blur(20px)' : 'none',
          borderBottom: scrolled ? '1px solid' : 'none',
          borderColor: scrolled ? 'rgba(139,92,246,0.12)' : 'transparent',
          transition: 'background-color 0.5s ease, backdrop-filter 0.5s ease, border-color 0.5s ease',
          boxShadow: 'none',
        }}
      >
        <Container maxWidth="lg">
          <Toolbar disableGutters sx={{ minHeight: { xs: 64, md: 72 }, justifyContent: 'space-between' }}>
            {/* Logo */}
            <Box
              component="button"
              onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
              sx={{ background: 'none', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 1.25 }}
            >
              <Box
                sx={{
                  width: 36,
                  height: 36,
                  borderRadius: 2,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  background: 'linear-gradient(135deg, #7c3aed, #8b5cf6 50%, #06b6d4)',
                  boxShadow: '0 0 16px rgba(139,92,246,0.4)',
                }}
              >
                <Inventory2RoundedIcon sx={{ color: 'common.white', fontSize: 20 }} />
              </Box>
              <Box component="span" sx={{ fontWeight: 700, fontSize: '1.1rem', color: 'common.white', letterSpacing: '-0.01em' }}>
                Asset Tracker
              </Box>
            </Box>

            {/* Desktop nav */}
            <Stack direction="row" spacing={4} sx={{ display: { xs: 'none', md: 'flex' } }}>
              {NAV_LINKS.map((link) => (
                <Button
                  key={link.href}
                  onClick={() => handleNav(link.href)}
                  sx={{ color: 'text.secondary', fontWeight: 500, fontSize: '0.9rem', minWidth: 0, '&:hover': { color: 'common.white' } }}
                >
                  {link.label}
                </Button>
              ))}
            </Stack>

            {/* Desktop CTA */}
            <GlowButton
              href="https://github.com/balayokesh/asset-tracker-desktop"
              startIcon={<DownloadRoundedIcon />}
              sx={{ display: { xs: 'none', md: 'inline-flex' }, py: 1, px: 2, fontSize: '0.9rem' }}
            >
              Download Desktop App
            </GlowButton>

            {/* Mobile hamburger */}
            <IconButton
              onClick={() => setMobileOpen(true)}
              sx={{ display: { xs: 'flex', md: 'none' }, color: 'text.secondary' }}
              aria-label="Open menu"
            >
              <MenuRoundedIcon />
            </IconButton>
          </Toolbar>
        </Container>
      </AppBar>

      {/* Mobile drawer */}
      <Drawer
        anchor="right"
        open={mobileOpen}
        onClose={() => setMobileOpen(false)}
        PaperProps={{ sx: { width: 280, bgcolor: '#0f172a', borderLeft: '1px solid rgba(139,92,246,0.12)', p: 2 } }}
      >
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2, px: 1 }}>
          <Box sx={{ fontWeight: 700, color: 'common.white' }}>Menu</Box>
          <IconButton onClick={() => setMobileOpen(false)} sx={{ color: 'text.secondary' }} aria-label="Close menu">
            <CloseRoundedIcon />
          </IconButton>
        </Stack>
        <Divider sx={{ borderColor: 'rgba(255,255,255,0.06)', mb: 1 }} />
        <List>
          {NAV_LINKS.map((link) => (
            <ListItem key={link.href} disablePadding>
              <ListItemButton onClick={() => handleNav(link.href)} sx={{ borderRadius: 2, '&:hover': { bgcolor: 'rgba(139,92,246,0.08)' } }}>
                <ListItemText primary={link.label} primaryTypographyProps={{ fontWeight: 500, color: 'text.secondary' }} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider sx={{ borderColor: 'rgba(255,255,255,0.06)', my: 1 }} />
        <GlowButton href="https://github.com/balayokesh/asset-tracker-desktop" startIcon={<DownloadRoundedIcon />} fullWidth sx={{ mt: 1 }}>
          Download Desktop App
        </GlowButton>
      </Drawer>
    </>
  );
}
