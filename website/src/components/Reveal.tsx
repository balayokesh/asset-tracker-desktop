import { useEffect, useRef, useState, type ReactNode } from 'react';
import Box from '@mui/material/Box';
import type { SxProps } from '@mui/material/styles';
import type { Theme } from '@mui/material/styles';

interface RevealProps {
  children: ReactNode;
  delay?: number;
  sx?: SxProps<Theme>;
}

export default function Reveal({ children, delay = 0, sx }: RevealProps) {
  const ref = useRef<HTMLDivElement>(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setVisible(true);
          observer.unobserve(entry.target);
        }
      },
      { threshold: 0.12, rootMargin: '0px 0px -40px 0px' },
    );
    if (ref.current) observer.observe(ref.current);
    return () => observer.disconnect();
  }, []);

  return (
    <Box
      ref={ref}
      sx={{
        opacity: visible ? 1 : 0,
        transform: visible ? 'translateY(0)' : 'translateY(24px)',
        transition: `opacity 0.7s ease ${delay}ms, transform 0.7s ease ${delay}ms`,
        ...sx,
      }}
    >
      {children}
    </Box>
  );
}
