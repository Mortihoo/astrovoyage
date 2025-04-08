import React, { useEffect, useRef } from 'react';
import '../styles/AstrologyChart.css';
import { Box } from '@mui/material';

const AstrologyChart = ({ chartData }) => {
  const canvasRef = useRef(null);

  // Helper functions
  const getSignFromPosition = (position) => {
    const signs = [
      'Aries', 'Taurus', 'Gemini', 'Cancer', 
      'Leo', 'Virgo', 'Libra', 'Scorpio', 
      'Sagittarius', 'Capricorn', 'Aquarius', 'Pisces'
    ];
    const signIndex = Math.floor(position / 30);
    return signs[signIndex % 12];
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

  const elementColors = {
    Fire: '#FF6B6B',
    Earth: '#38D9A9',
    Air: '#74C0FC',
    Water: '#748FFC'
  };

  const getDignitiesForPlanet = (planetName, position) => {
    const sign = getSignFromPosition(position);
    const dignities = {
      Sun: { domicile: 'Leo', exaltation: 'Aries', fall: 'Libra', detriment: 'Aquarius' },
      Moon: { domicile: 'Cancer', exaltation: 'Taurus', fall: 'Scorpio', detriment: 'Capricorn' },
      Mercury: { domicile: ['Gemini', 'Virgo'], exaltation: 'Virgo', fall: 'Pisces', detriment: ['Sagittarius', 'Pisces'] },
      Venus: { domicile: ['Taurus', 'Libra'], exaltation: 'Pisces', fall: 'Virgo', detriment: ['Aries', 'Scorpio'] },
      Mars: { domicile: ['Aries', 'Scorpio'], exaltation: 'Capricorn', fall: 'Cancer', detriment: ['Taurus', 'Libra'] },
      Jupiter: { domicile: ['Sagittarius', 'Pisces'], exaltation: 'Cancer', fall: 'Capricorn', detriment: ['Gemini', 'Virgo'] },
      Saturn: { domicile: ['Capricorn', 'Aquarius'], exaltation: 'Libra', fall: 'Aries', detriment: ['Cancer', 'Leo'] },
      Uranus: { domicile: 'Aquarius' },
      Neptune: { domicile: 'Pisces' },
      Pluto: { domicile: 'Scorpio' }
    };

    const planetDignities = dignities[planetName];
    if (!planetDignities) return null;

    if (Array.isArray(planetDignities.domicile) && planetDignities.domicile.includes(sign)) return 'domicile';
    if (planetDignities.domicile === sign) return 'domicile';
    if (planetDignities.exaltation === sign) return 'exaltation';
    if (planetDignities.fall === sign) return 'fall';
    if (Array.isArray(planetDignities.detriment) && planetDignities.detriment.includes(sign)) return 'detriment';
    if (planetDignities.detriment === sign) return 'detriment';
    return null;
  };

  const getAspectColor = (aspectType, alpha = 1) => {
    const colors = {
      conjunction: `rgba(255, 0, 0, ${alpha})`,
      opposition: `rgba(255, 0, 0, ${alpha * 0.8})`,
      trine: `rgba(0, 0, 255, ${alpha})`,
      square: `rgba(255, 69, 0, ${alpha})`,
      sextile: `rgba(0, 128, 0, ${alpha})`
    };
    return colors[aspectType] || `rgba(128, 128, 128, ${alpha})`;
  };

  const getPlanetGlyph = (planetName) => {
    const glyphs = {
      Sun: '☉', Moon: '☽', Mercury: '☿', Venus: '♀', Mars: '♂',
      Jupiter: '♃', Saturn: '♄', Uranus: '♅', Neptune: '♆', Pluto: '♇',
      'North Node': '☊', 'South Node': '☋', Chiron: '⚷',
      'Part of Fortune': '⊗', Vertex: 'Vx'
    };
    return glyphs[planetName] || planetName;
  };

  // Drawing functions
  const drawZodiacCircle = (ctx) => {
    const width = 800;
    const height = 800;
    const radius = Math.min(width, height) / 2 - 40;
    const centerX = width / 2;
    const centerY = height / 2;

    // Set canvas size
    canvasRef.current.width = width;
    canvasRef.current.height = height;

    // Draw background
    ctx.fillStyle = '#FFFFFF';
    ctx.fillRect(0, 0, width, height);

    // Draw outer circle with gradient
    const gradient = ctx.createRadialGradient(centerX, centerY, radius - 10, centerX, centerY, radius);
    gradient.addColorStop(0, '#FFFFFF');
    gradient.addColorStop(1, '#F0F0F0');
    
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, 0, Math.PI * 2);
    ctx.fillStyle = gradient;
    ctx.fill();
    ctx.strokeStyle = '#666666';
    ctx.lineWidth = 2;
    ctx.stroke();

    // Draw inner circles with subtle gradients
    [0.8, 0.6, 0.4].forEach(ratio => {
      const innerRadius = radius * ratio;
      const innerGradient = ctx.createRadialGradient(
        centerX, centerY, innerRadius - 5,
        centerX, centerY, innerRadius
      );
      innerGradient.addColorStop(0, '#FFFFFF');
      innerGradient.addColorStop(1, '#F8F8F8');
      
      ctx.beginPath();
      ctx.arc(centerX, centerY, innerRadius, 0, Math.PI * 2);
      ctx.fillStyle = innerGradient;
      ctx.fill();
      ctx.strokeStyle = '#999999';
      ctx.lineWidth = 1;
      ctx.stroke();
    });

    // Draw planet ring (now in the middle position)
    const planetRingRadius = radius * 0.7;
    const planetRingWidth = 20;
    
    // Draw planet ring background
    ctx.beginPath();
    ctx.arc(centerX, centerY, planetRingRadius, 0, Math.PI * 2);
    ctx.fillStyle = '#F8F8F8';
    ctx.fill();
    ctx.strokeStyle = '#DDDDDD';
    ctx.lineWidth = 1;
    ctx.stroke();

    // Draw planet ring inner circle
    ctx.beginPath();
    ctx.arc(centerX, centerY, planetRingRadius - planetRingWidth, 0, Math.PI * 2);
    ctx.strokeStyle = '#DDDDDD';
    ctx.lineWidth = 1;
    ctx.stroke();

    // Draw house ring (now in the inner position)
    const houseRingRadius = radius * 0.45;
    const houseRingWidth = 25;
    
    // Draw house ring background with gradient
    const houseRingGradient = ctx.createRadialGradient(
      centerX, centerY, houseRingRadius - houseRingWidth,
      centerX, centerY, houseRingRadius
    );
    houseRingGradient.addColorStop(0, '#F8F8F8');
    houseRingGradient.addColorStop(1, '#F0F0F0');
    
    // Create clipping path for house ring
    ctx.save();
    ctx.beginPath();
    ctx.arc(centerX, centerY, houseRingRadius, 0, Math.PI * 2);
    ctx.arc(centerX, centerY, houseRingRadius - houseRingWidth, 0, Math.PI * 2, true);
    ctx.clip();
    
    // Fill the clipped area with gradient
    ctx.fillStyle = houseRingGradient;
    ctx.fillRect(0, 0, width, height);
    ctx.restore();
    
    // Draw outer border
    ctx.beginPath();
    ctx.arc(centerX, centerY, houseRingRadius, 0, Math.PI * 2);
    ctx.strokeStyle = '#666666';
    ctx.lineWidth = 2;
    ctx.stroke();
    
    // Draw inner border
    ctx.beginPath();
    ctx.arc(centerX, centerY, houseRingRadius - houseRingWidth, 0, Math.PI * 2);
    ctx.strokeStyle = '#666666';
    ctx.lineWidth = 2;
    ctx.stroke();
    
    // Draw decorative lines
    for (let i = 0; i < 12; i++) {
      const angle = (i * 30 - 90) * (Math.PI / 180);
      const x1 = centerX + (houseRingRadius - houseRingWidth) * Math.sin(angle);
      const y1 = centerY - (houseRingRadius - houseRingWidth) * Math.cos(angle);
      const x2 = centerX + houseRingRadius * Math.sin(angle);
      const y2 = centerY - houseRingRadius * Math.cos(angle);
      
      // Draw line with gradient
      const lineGradient = ctx.createLinearGradient(x1, y1, x2, y2);
      lineGradient.addColorStop(0, 'rgba(102, 102, 102, 0.3)');
      lineGradient.addColorStop(1, 'rgba(102, 102, 102, 0.6)');
      
      ctx.beginPath();
      ctx.moveTo(x1, y1);
      ctx.lineTo(x2, y2);
      ctx.strokeStyle = lineGradient;
      ctx.lineWidth = 0.8;
      ctx.stroke();
    }

    // Draw zodiac segments with improved styling
    const zodiacSigns = ['♈', '♉', '♊', '♋', '♌', '♍', '♎', '♏', '♐', '♑', '♒', '♓'];
    const zodiacColors = {
      '♈': '#FFE5E5', '♉': '#E5FFE5', '♊': '#E5E5FF',
      '♋': '#FFE5FF', '♌': '#FFFFE5', '♍': '#E5FFFF',
      '♎': '#FFE5E5', '♏': '#E5FFE5', '♊': '#E5E5FF',
      '♐': '#FFE5FF', '♑': '#FFFFE5', '♒': '#E5FFFF',
      '♓': '#FFE5E5'
    };
    
    zodiacSigns.forEach((sign, i) => {
      const startAngle = (i * 30 - 90) * (Math.PI / 180);
      const endAngle = ((i + 1) * 30 - 90) * (Math.PI / 180);
      const midAngle = ((i * 30 + 15) - 90) * (Math.PI / 180);
      
      // Draw zodiac segment background
      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.arc(centerX, centerY, radius, startAngle, endAngle);
      ctx.lineTo(centerX, centerY);
      ctx.fillStyle = zodiacColors[sign];
      ctx.fill();
      
      // Draw zodiac symbol with shadow
      const symbolRadius = radius - 45;
      const symbolX = centerX + symbolRadius * Math.cos(midAngle);
      const symbolY = centerY + symbolRadius * Math.sin(midAngle);
      
      ctx.save();
      ctx.translate(symbolX, symbolY);
      ctx.rotate(midAngle + Math.PI/2);
      
      // Add shadow effect
      ctx.shadowColor = 'rgba(0, 0, 0, 0.3)';
      ctx.shadowBlur = 3;
      ctx.shadowOffsetX = 1;
      ctx.shadowOffsetY = 1;
      
      ctx.font = 'bold 24px Arial';
      ctx.fillStyle = '#333333';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(sign, 0, 0);
      ctx.restore();

      // Draw degree markers and numbers within this segment
      for (let deg = 0; deg < 30; deg += 5) {
        const angle = ((i * 30 + deg) - 90) * (Math.PI / 180);
        const isMainDegree = deg === 0;
        const markerLength = isMainDegree ? 15 : (deg % 10 === 0 ? 10 : 5);
        
        const x1 = centerX + radius * Math.cos(angle);
        const y1 = centerY + radius * Math.sin(angle);
        const x2 = centerX + (radius - markerLength) * Math.cos(angle);
        const y2 = centerY + (radius - markerLength) * Math.sin(angle);

        ctx.beginPath();
        ctx.moveTo(x1, y1);
        ctx.lineTo(x2, y2);
        ctx.strokeStyle = isMainDegree ? '#444444' : '#888888';
        ctx.lineWidth = isMainDegree ? 2 : 0.5;
        ctx.stroke();

        if (isMainDegree) {
          const textRadius = radius - 25;
          const textX = centerX + textRadius * Math.cos(angle);
          const textY = centerY + textRadius * Math.sin(angle);
          
          ctx.save();
          ctx.translate(textX, textY);
          ctx.rotate(angle + Math.PI/2);
          ctx.font = 'bold 12px Arial';
          ctx.fillStyle = '#444444';
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          ctx.fillText((i * 30).toString(), 0, 0);
          ctx.restore();
        }
      }
    });
  };

  const drawHouse = (ctx, houseNumber, position) => {
    const width = 800;
    const height = 800;
    const radius = Math.min(width, height) / 2 - 40;
    const houseRingRadius = radius * 0.45;
    const houseRingWidth = 25;
    const centerX = width / 2;
    const centerY = height / 2;

    // Draw house outer and inner contours (only draw full circle contour for the first house)
    if (houseNumber === 1) {
      // Draw outer contour
      ctx.beginPath();
      ctx.arc(centerX, centerY, houseRingRadius, 0, Math.PI * 2);
      ctx.strokeStyle = '#444444';
      ctx.lineWidth = 1.5;
      ctx.stroke();

      // Draw inner contour
      ctx.beginPath();
      ctx.arc(centerX, centerY, houseRingRadius - houseRingWidth * 1.7, 0, Math.PI * 2);
      ctx.strokeStyle = '#444444';
      ctx.lineWidth = 1.5;
      ctx.stroke();
    }

    const angle = position * (Math.PI / 180);
    const x1 = centerX + (houseRingRadius - houseRingWidth * 1.2) * Math.sin(angle);
    const y1 = centerY - (houseRingRadius - houseRingWidth * 1.2) * Math.cos(angle);
    const x2 = centerX + houseRingRadius * Math.sin(angle);
    const y2 = centerY - houseRingRadius * Math.cos(angle);

    // Draw house line with gradient
    const gradient = ctx.createLinearGradient(x1, y1, x2, y2);
    gradient.addColorStop(0, 'rgba(0, 0, 0, 0.4)');
    gradient.addColorStop(1, 'rgba(0, 0, 0, 0.8)');

    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.strokeStyle = gradient;
    ctx.lineWidth = houseNumber % 3 === 0 ? 1.5 : 0.8;
    ctx.stroke();

    // Draw house number in the house ring
    const labelAngle = (position + 2) * (Math.PI / 180);
    const labelRadius = houseRingRadius - houseRingWidth * 0.9;
    const labelX = centerX + labelRadius * Math.sin(labelAngle);
    const labelY = centerY - labelRadius * Math.cos(labelAngle);
    
    ctx.save();
    ctx.translate(labelX, labelY);
    ctx.rotate(labelAngle + Math.PI/2);
    
    // Draw house number - increased font size from 11px to 14px
    ctx.font = 'bold 14px Arial';
    ctx.fillStyle = '#444444';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    
    // Special labels for angles
    let houseLabel = houseNumber.toString();
    if (houseNumber === 1) houseLabel = 'ASC';
    if (houseNumber === 10) houseLabel = 'MC';
    if (houseNumber === 7) houseLabel = 'DSC';
    if (houseNumber === 4) houseLabel = 'IC';
    
    ctx.fillText(houseLabel, 0, 0);
    
    // Draw degree - increased font size from 9px to 12px
    ctx.font = '12px Arial';
    ctx.fillStyle = '#666666';
    ctx.fillText(`${Math.floor(position)}°${Math.floor((position % 1) * 60)}'`, 0, 14);
    
    ctx.restore();
  };

  const drawPlanet = (ctx, planetName, position, index, total) => {
    const width = 800;
    const height = 800;
    const radius = Math.min(width, height) / 2 - 40;
    const planetRingRadius = radius * 0.7;
    const planetRingWidth = 20;
    const centerX = width / 2;
    const centerY = height / 2;

    // Calculate base position
    const angle = position * (Math.PI / 180);
    const r = planetRingRadius - planetRingWidth / 2;
    const baseX = centerX + r * Math.sin(angle);
    const baseY = centerY - r * Math.cos(angle);

    // Reverse offset direction to display planet information starting from near the house layer
    let offset;
    if (total === 1) {
      offset = 25;
    } else if (total === 2) {
      offset = index * 60 - 20;
    } else {
      let p = 60 - 5 * total
      offset = index * p - 50;
    }

    // Calculate offset position
    const perpAngle = angle + Math.PI / 2;
    const x = baseX + offset * Math.cos(perpAngle);
    const y = baseY + offset * Math.sin(perpAngle);

    // Draw connecting line if offset
    if (offset !== 0) {
      ctx.beginPath();
      ctx.moveTo(baseX, baseY);
      ctx.lineTo(x, y);
      ctx.strokeStyle = 'rgba(102, 102, 102, 0.5)';
      ctx.lineWidth = 0.5;
      ctx.stroke();
    }

    // Draw planet marker with gradient
    const gradient = ctx.createRadialGradient(x, y, 0, x, y, 6);
    gradient.addColorStop(0, '#FFFFFF');
    gradient.addColorStop(1, '#666666');

    ctx.beginPath();
    ctx.arc(x, y, 6, 0, Math.PI * 2);
    ctx.fillStyle = gradient;
    ctx.fill();
    ctx.strokeStyle = '#444444';
    ctx.lineWidth = 1;
    ctx.stroke();

    // Draw planet symbol with shadow
    const planetSymbols = {
      SUN: '☉', MOON: '☽', MERCURY: '☿', VENUS: '♀', MARS: '♂',
      JUPITER: '♃', SATURN: '♄', URANUS: '♅', NEPTUNE: '♆', PLUTO: '♇'
    };

    ctx.save();
    ctx.shadowColor = 'rgba(0, 0, 0, 0.3)';
    ctx.shadowBlur = 2;
    ctx.shadowOffsetX = 1;
    ctx.shadowOffsetY = 1;

    ctx.font = 'bold 20px Arial';
    ctx.fillStyle = '#000000';
    
    let textAlign = 'left';
    let symbolOffset = 10;
    if (position > 90 && position < 270) {
      textAlign = 'right';
      symbolOffset = -10;
    }
    
    if (total > 1) {
      if (index === 0) {
        symbolOffset = position > 90 && position < 270 ? -10 : 10;
      } else if (index === total - 1) {
        symbolOffset = position > 90 && position < 270 ? -10: 10;
      }
    }
    
    ctx.textAlign = textAlign;
    ctx.textBaseline = 'middle';
    ctx.fillText(planetSymbols[planetName] || planetName, x + symbolOffset, y);

    // Draw degree
    const degree = Math.floor(position);
    const minute = Math.floor((position % 1) * 60);
    ctx.font = '12px Arial';
    ctx.fillStyle = '#666666';
    ctx.fillText(`${degree}°${minute}'`, x + symbolOffset, y + 20);
    
    // Draw retrograde symbol if planet is retrograde 
    if (chartData.planetSpeeds?.[planetName] < 0) {
      ctx.font = '14px Arial';
      ctx.fillStyle = '#FF4444';
      ctx.fillText('℞', x + symbolOffset + (textAlign === 'left' ? 15 : -15), y - 8);
    }
    
    // Only show speed and latitude information if there are less than 3 planets in the group
    if (total < 3 && chartData.planetPositions[planetName]?.[1]) {
      const latitude = chartData.planetPositions[planetName][1];
      const planetSpeed = chartData.planetSpeeds?.[planetName] || 0;
      console.log(`Planet ${planetName} speed: ${planetSpeed}`);

      ctx.font = '10px Arial';
      ctx.fillStyle = '#888888';
      ctx.fillText(`${latitude > 0 ? '+' : ''}${latitude.toFixed(2)}°`, x + symbolOffset, y + 32);
      
      // Add speed information with more prominent display
      ctx.font = '11px Arial';  // Increased font size
      ctx.fillStyle = planetSpeed < 0 ? '#FF4444' : '#444444';  // Use red color for retrograde
      ctx.fillText(`${planetSpeed > 0 ? '+' : ''}${planetSpeed.toFixed(3)}°/d`, x + symbolOffset, y + 44);
    }
    
    ctx.restore();

    // Use the variables we defined
    const sign = getSignFromPosition(position);
    const element = getElementForSign(sign);
    const modality = getModalityForSign(sign);
    const color = elementColors[element] || '#000000';
    const dignity = getDignitiesForPlanet(planetName, position);
    const glyph = getPlanetGlyph(planetName);
    
    // Apply the color to the planet
    ctx.fillStyle = color;
    ctx.strokeStyle = color;
    
    // Draw planet with dignity indicator
    if (dignity) {
      // Add visual indicator for dignity
      ctx.beginPath();
      ctx.arc(x, y, 8, 0, Math.PI * 2);
      ctx.strokeStyle = color;
      ctx.lineWidth = 2;
      ctx.stroke();
    }
    
  };

  const drawAspect = (ctx, planet1Pos, planet2Pos, angle, aspectType) => {
    if (!['conjunction', 'opposition', 'trine', 'square', 'sextile'].includes(aspectType)) {
      return;
    }

    const width = 800;
    const height = 800;
    const radius = Math.min(width, height) / 2 - 40;
    const innerRadius = radius * 0.47 - 25 * 1.7; // Adjust aspect layer radius to fit closely with house layer inner circle
    const centerX = width / 2;
    const centerY = height / 2;

    const angle1 = planet1Pos * (Math.PI / 180);
    const angle2 = planet2Pos * (Math.PI / 180);
    const r = innerRadius * 0.9;

    const x1 = centerX + r * Math.sin(angle1);
    const y1 = centerY - r * Math.cos(angle1);
    const x2 = centerX + r * Math.sin(angle2);
    const y2 = centerY - r * Math.cos(angle2);

    // Draw aspect line with gradient
    const gradient = ctx.createLinearGradient(x1, y1, x2, y2);

    // Use getAspectColor
    const color = getAspectColor(aspectType, 0.7);

    // Set line style based on aspect type
    switch (aspectType) {
      case 'conjunction':
        gradient.addColorStop(0, 'rgba(255, 0, 0, 0.8)');
        gradient.addColorStop(1, 'rgba(255, 0, 0, 0.4)');
        ctx.lineWidth = 2;
        break;
      case 'opposition':
        gradient.addColorStop(0, 'rgba(255, 0, 0, 0.6)');
        gradient.addColorStop(1, 'rgba(255, 0, 0, 0.3)');
        ctx.lineWidth = 2;
        ctx.setLineDash([5, 3]);
        break;
      case 'trine':
        gradient.addColorStop(0, 'rgba(0, 0, 255, 0.6)');
        gradient.addColorStop(1, 'rgba(0, 0, 255, 0.3)');
        ctx.lineWidth = 1.5;
        break;
      case 'square':
        gradient.addColorStop(0, 'rgba(255, 69, 0, 0.6)');
        gradient.addColorStop(1, 'rgba(255, 69, 0, 0.3)');
        ctx.lineWidth = 1.5;
        ctx.setLineDash([4, 2]);
        break;
      case 'sextile':
        gradient.addColorStop(0, 'rgba(0, 128, 0, 0.6)');
        gradient.addColorStop(1, 'rgba(0, 128, 0, 0.3)');
        ctx.lineWidth = 1;
        ctx.setLineDash([2, 2]);
        break;
      default:
        // Handle unknown aspect types
        ctx.lineWidth = 1;
        ctx.strokeStyle = 'rgba(128, 128, 128, 0.5)';
        break;
    }

    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.strokeStyle = color;
    ctx.stroke();
    ctx.setLineDash([]);
  };

  // New function to draw zodiac sign rulers
  const drawSignRulers = (ctx) => {
    const width = 800;
    const height = 800;
    const radius = Math.min(width, height) / 2 - 40;
    const rulerRadius = radius - 65;
    const centerX = width / 2;
    const centerY = height / 2;

    const rulers = {
      '♈': '♂', '♉': '♀', '♊': '☿', '♋': '☽',
      '♌': '☉', '♍': '☿', '♎': '♀', '♏': '♂',
      '♐': '♃', '♑': '♄', '♒': '♄', '♓': '♃'
    };

    Object.entries(rulers).forEach(([sign, ruler], i) => {
      const angle = ((i * 30) + 15 - 90) * (Math.PI / 180);
      const x = centerX + rulerRadius * Math.cos(angle);
      const y = centerY + rulerRadius * Math.sin(angle);

      ctx.save();
      ctx.translate(x, y);
      ctx.rotate(angle + Math.PI/2);
      
      ctx.font = '12px Arial';
      ctx.fillStyle = '#666666';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(ruler, 0, 0);
      
      ctx.restore();
    });
  };

  // New function to draw element and modality information
  const drawElementsAndModalities = (ctx) => {
    // Remove code for drawing elements and modalities
    return;
  };

  const renderChart = () => {
    if (!chartData) return null;

    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Draw zodiac circle
    drawZodiacCircle(ctx);

    // Draw sign rulers
    drawSignRulers(ctx);

    // Draw houses
    if (chartData.houses) {
      chartData.houses.forEach((position, index) => {
        drawHouse(ctx, index + 1, position);
      });
    }

    // Draw elements and modalities
    drawElementsAndModalities(ctx);

    // Draw aspects - draw them in order of importance
    if (chartData.aspects && chartData.planetPositions) {
      // First draw less important aspects
      Object.entries(chartData.aspects).forEach(([aspectName, angle]) => {
        const [planet1, planet2] = aspectName.split('-');
        const aspectType = chartData.aspectsDetails?.[aspectName];
        if (chartData.planetPositions[planet1] && chartData.planetPositions[planet2] &&
            ['sextile', 'square', 'trine'].includes(aspectType)) {
          drawAspect(ctx, 
            chartData.planetPositions[planet1][0],
            chartData.planetPositions[planet2][0],
            angle,
            aspectType
          );
        }
      });

      // Then draw major aspects (conjunction and opposition) on top
      Object.entries(chartData.aspects).forEach(([aspectName, angle]) => {
        const [planet1, planet2] = aspectName.split('-');
        const aspectType = chartData.aspectsDetails?.[aspectName];
        if (chartData.planetPositions[planet1] && chartData.planetPositions[planet2] &&
            ['conjunction', 'opposition'].includes(aspectType)) {
          drawAspect(ctx, 
            chartData.planetPositions[planet1][0],
            chartData.planetPositions[planet2][0],
            angle,
            aspectType
          );
        }
      });
    }

    // Draw planets with position grouping
    if (chartData.planetPositions) {
      // Group planets by their positions within 30 degrees
      const positionGroups = {};
      Object.entries(chartData.planetPositions).forEach(([name, coords]) => {
        const position = coords[0];
        const groupKey = Math.floor(position / 30) * 30; // Group every 30 degrees
        if (!positionGroups[groupKey]) {
          positionGroups[groupKey] = [];
        }
        positionGroups[groupKey].push({ name, position });
      });

      // Draw planets with offsets based on their group
      Object.values(positionGroups).forEach(group => {
        group.sort((a, b) => a.position - b.position); // Sort by exact position
        group.forEach((planet, index) => {
          drawPlanet(ctx, planet.name, planet.position, index, group.length);
        });
      });
    }
  };

  useEffect(() => {
    if (canvasRef.current && chartData) {
      renderChart();
    }
  }, [chartData]);

  return (
    <Box sx={{ display: 'flex', alignItems: 'flex-start' }}>
      <canvas ref={canvasRef} style={{ border: '1px solid black' }} />
    </Box>
  );
};

export default AstrologyChart; 