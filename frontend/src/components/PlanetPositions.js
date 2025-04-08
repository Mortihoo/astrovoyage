import React from 'react';
import { Box, Typography, Table, TableBody, TableCell, TableContainer, TableRow, Paper, Divider, Tooltip } from '@mui/material';

const PlanetPositions = ({ chartData }) => {
  // Define planet symbol mapping
  const planetSymbols = {
    SUN: '☉',
    MOON: '☽',
    MERCURY: '☿',
    VENUS: '♀',
    MARS: '♂',
    JUPITER: '♃',
    SATURN: '♄',
    URANUS: '♅',
    NEPTUNE: '♆',
    PLUTO: '♇',
    'North Node': '☊',
    'South Node': '☋',
    Chiron: '⚷',
    'Part of Fortune': '⊗',
    Vertex: 'Vx'
  };

  // Define zodiac sign symbol mapping
  const zodiacSymbols = {
    Aries: '♈',
    Taurus: '♉',
    Gemini: '♊',
    Cancer: '♋',
    Leo: '♌',
    Virgo: '♍',
    Libra: '♎',
    Scorpio: '♏',
    Sagittarius: '♐',
    Capricorn: '♑',
    Aquarius: '♒',
    Pisces: '♓'
  };

  const elementColors = {
    Fire: '#FF6B6B',
    Earth: '#38D9A9',
    Air: '#74C0FC',
    Water: '#748FFC'
  };

  const dignitySymbols = {
    domicile: '⌂',
    exaltation: '⇈',
    detriment: '⌒',
    fall: '⇊'
  };

  const getElementForSign = (sign) => {
    const elements = {
      Aries: 'Fire', Leo: 'Fire', Sagittarius: 'Fire',
      Taurus: 'Earth', Virgo: 'Earth', Capricorn: 'Earth',
      Gemini: 'Air', Libra: 'Air', Aquarius: 'Air',
      Cancer: 'Water', Scorpio: 'Water', Pisces: 'Water'
    };
    return elements[sign] || '';
  };

  const getModalityForSign = (sign) => {
    const modalities = {
      Aries: 'Cardinal', Cancer: 'Cardinal', Libra: 'Cardinal', Capricorn: 'Cardinal',
      Taurus: 'Fixed', Leo: 'Fixed', Scorpio: 'Fixed', Aquarius: 'Fixed',
      Gemini: 'Mutable', Virgo: 'Mutable', Sagittarius: 'Mutable', Pisces: 'Mutable'
    };
    return modalities[sign] || '';
  };

  const getDignitiesForPlanet = (planetName, sign) => {
    const dignities = {
      Sun: { domicile: 'Leo', exaltation: 'Aries', fall: 'Libra', detriment: 'Aquarius' },
      Moon: { domicile: 'Cancer', exaltation: 'Taurus', fall: 'Scorpio', detriment: 'Capricorn' },
      Mercury: { domicile: ['Gemini', 'Virgo'], exaltation: 'Virgo', fall: 'Pisces', detriment: ['Sagittarius', 'Pisces'] },
      Venus: { domicile: ['Taurus', 'Libra'], exaltation: 'Pisces', fall: 'Virgo', detriment: ['Aries', 'Scorpio'] },
      Mars: { domicile: ['Aries', 'Scorpio'], exaltation: 'Capricorn', fall: 'Cancer', detriment: ['Taurus', 'Libra'] },
      Jupiter: { domicile: ['Sagittarius', 'Pisces'], exaltation: 'Cancer', fall: 'Capricorn', detriment: ['Gemini', 'Virgo'] },
      Saturn: { domicile: ['Capricorn', 'Aquarius'], exaltation: 'Libra', fall: 'Aries', detriment: ['Cancer', 'Leo'] }
    };

    const planetDignities = dignities[planetName];
    if (!planetDignities) return [];

    const result = [];
    if (Array.isArray(planetDignities.domicile)) {
      if (planetDignities.domicile.includes(sign)) result.push('domicile');
    } else if (planetDignities.domicile === sign) {
      result.push('domicile');
    }

    if (planetDignities.exaltation === sign) result.push('exaltation');
    if (planetDignities.fall === sign) result.push('fall');

    if (Array.isArray(planetDignities.detriment)) {
      if (planetDignities.detriment.includes(sign)) result.push('detriment');
    } else if (planetDignities.detriment === sign) {
      result.push('detriment');
    }

    return result;
  };

  // Format house display
  const formatHouseDisplay = (house) => {
    if (house.number === 1) return 'AC';
    if (house.number === 10) return 'MC';
    if (house.number === 4) return 'IC';
    if (house.number === 7) return 'DC';
    return house.number;
  };

  // Convert planets Map to array
  const getPlanetsArray = (planets) => {
    if (!planets) return [];
    return Object.entries(planets).map(([name, coords]) => {
      const position = coords[0];
      const sign = getSignFromPosition(position);
      const speed = coords[3] || 0;
      return {
        name,
        position,
        sign,
        degree: Math.floor(position % 30),
        minute: Math.floor((position % 1) * 60),
        element: getElementForSign(sign),
        modality: getModalityForSign(sign),
        dignities: getDignitiesForPlanet(name, sign),
        retrograde: speed < 0,
        latitude: coords[1] || 0,
        speed: speed
      };
    });
  };

  // Get sign from position (0-360 degrees)
  const getSignFromPosition = (position) => {
    const signs = [
      'Aries', 'Taurus', 'Gemini', 'Cancer', 
      'Leo', 'Virgo', 'Libra', 'Scorpio', 
      'Sagittarius', 'Capricorn', 'Aquarius', 'Pisces'
    ];
    const signIndex = Math.floor(position / 30);
    return signs[signIndex % 12];
  };

  // Split houses into two columns
  const splitHouses = (houses) => {
    if (!houses || !Array.isArray(houses)) return [[], []];
    
    const housesArray = houses.map((position, index) => ({
      number: index + 1,
      position: position
    }));
    
    const leftColumn = housesArray.filter(h => h.number <= 6);
    const rightColumn = housesArray.filter(h => h.number > 6);
    return [leftColumn, rightColumn];
  };

  const [leftHouses, rightHouses] = splitHouses(chartData?.houses);
  const planetsArray = getPlanetsArray(chartData?.planetPositions);

  // Calculate element and modality distributions
  const elementDistribution = planetsArray.reduce((acc, planet) => {
    acc[planet.element] = (acc[planet.element] || 0) + 1;
    return acc;
  }, {});

  const modalityDistribution = planetsArray.reduce((acc, planet) => {
    acc[planet.modality] = (acc[planet.modality] || 0) + 1;
    return acc;
  }, {});

  return (
    <Box className="planet-positions">
      <Typography variant="h6" gutterBottom sx={{
        color: '#333',
        fontSize: '1.1rem',
        borderBottom: '2px solid #eee',
        pb: 1,
        mb: 2
      }}>
        Planetary Positions
      </Typography>

      <TableContainer component={Paper} elevation={0}>
        <Table size="small" className="planet-table">
          <TableBody>
            {planetsArray.map((planet) => (
              <TableRow 
                key={planet.name}
                sx={{
                  '&:hover': {
                    backgroundColor: 'rgba(0, 0, 0, 0.02)',
                  }
                }}
              >
                <TableCell sx={{ 
                  width: '40px',
                  fontSize: '1.2rem',
                  color: elementColors[planet.element] || '#666',
                  textAlign: 'center',
                  border: 'none'
                }}>
                  <Tooltip title={planet.name} arrow>
                    <span>{planetSymbols[planet.name] || planet.name}</span>
                  </Tooltip>
                </TableCell>
                <TableCell sx={{
                  fontWeight: 500,
                  color: '#444',
                  border: 'none'
                }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    {planet.name}
                    {planet.retrograde && (
                      <Typography component="span" sx={{ color: '#FF4444', fontSize: '0.8rem' }}>
                        ℞
                      </Typography>
                    )}
                    {planet.dignities.map((dignity) => (
                      <Tooltip key={dignity} title={dignity} arrow>
                        <Typography component="span" sx={{ 
                          color: dignity === 'fall' || dignity === 'detriment' ? '#FF4444' : '#38D9A9',
                          fontSize: '0.9rem'
                        }}>
                          {dignitySymbols[dignity]}
                        </Typography>
                      </Tooltip>
                    ))}
                  </Box>
                </TableCell>
                <TableCell sx={{
                  textAlign: 'right',
                  color: '#666',
                  border: 'none',
                  fontFamily: 'monospace'
                }}>
                  <Tooltip title={`${planet.element} - ${planet.modality}`} arrow>
                    <Box component="span" sx={{ mr: 1, color: elementColors[planet.element] }}>
                      {zodiacSymbols[planet.sign]}
                    </Box>
                  </Tooltip>
                  {planet.degree}°{planet.minute.toString().padStart(2, '0')}'
                  <Typography component="div" sx={{ fontSize: '0.75rem', color: '#888' }}>
                    {planet.latitude > 0 ? '+' : ''}{planet.latitude.toFixed(2)}°
                  </Typography>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Divider sx={{ my: 2 }} />

      <Box sx={{ mb: 2 }}>
        <Typography variant="subtitle2" gutterBottom sx={{ color: '#666' }}>
          Element Distribution
        </Typography>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
          {Object.entries(elementDistribution).map(([element, count]) => (
            <Box key={element} sx={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: 0.5,
              color: elementColors[element],
              fontSize: '0.9rem'
            }}>
              {element}: {count}
            </Box>
          ))}
        </Box>
      </Box>

      <Box sx={{ mb: 2 }}>
        <Typography variant="subtitle2" gutterBottom sx={{ color: '#666' }}>
          Modality Distribution
        </Typography>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
          {Object.entries(modalityDistribution).map(([modality, count]) => (
            <Box key={modality} sx={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: 0.5,
              color: '#666',
              fontSize: '0.9rem'
            }}>
              {modality}: {count}
            </Box>
          ))}
        </Box>
      </Box>

      <Divider sx={{ my: 2 }} />

      <Box className="aspect-legend">
        <Typography variant="subtitle2" gutterBottom sx={{ color: '#666' }}>
          Aspect Legend
        </Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
          <Box className="aspect-item">
            <Box className="aspect-line conjunction" />
            <Typography variant="caption">Conjunction (0°)</Typography>
          </Box>
          <Box className="aspect-item">
            <Box className="aspect-line opposition" />
            <Typography variant="caption">Opposition (180°)</Typography>
          </Box>
          <Box className="aspect-item">
            <Box className="aspect-line trine" />
            <Typography variant="caption">Trine (120°)</Typography>
          </Box>
          <Box className="aspect-item">
            <Box className="aspect-line square" />
            <Typography variant="caption">Square (90°)</Typography>
          </Box>
          <Box className="aspect-item">
            <Box className="aspect-line sextile" />
            <Typography variant="caption">Sextile (60°)</Typography>
          </Box>
        </Box>
      </Box>

      <Divider sx={{ my: 2 }} />

      <Box>
        <Typography variant="subtitle2" gutterBottom sx={{ color: '#666' }}>
          Essential Dignities Legend
        </Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, fontSize: '0.9rem' }}>
            <span style={{ color: '#38D9A9' }}>{dignitySymbols.domicile}</span>
            <span>Domicile</span>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, fontSize: '0.9rem' }}>
            <span style={{ color: '#38D9A9' }}>{dignitySymbols.exaltation}</span>
            <span>Exaltation</span>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, fontSize: '0.9rem' }}>
            <span style={{ color: '#FF4444' }}>{dignitySymbols.detriment}</span>
            <span>Detriment</span>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, fontSize: '0.9rem' }}>
            <span style={{ color: '#FF4444' }}>{dignitySymbols.fall}</span>
            <span>Fall</span>
          </Box>
        </Box>
      </Box>

      <Typography variant="h6" sx={{ mt: 3 }} gutterBottom>
        Houses: (Placidus system)
      </Typography>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableBody>
            <TableRow>
              <TableCell>
                {leftHouses.map((house) => (
                  <Box key={house.number} sx={{ mb: 1 }}>
                    {formatHouseDisplay(house)}: {house.position.toFixed(2)}°
                  </Box>
                ))}
              </TableCell>
              <TableCell>
                {rightHouses.map((house) => (
                  <Box key={house.number} sx={{ mb: 1 }}>
                    {formatHouseDisplay(house)}: {house.position.toFixed(2)}°
                  </Box>
                ))}
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default PlanetPositions; 