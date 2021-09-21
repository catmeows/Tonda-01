# Tonda-01
Fantasy computer based on 6809 cpu

## Hardware

* CPU: 6809 @ 1.7734475 
* ROM: 16KB
* RAM: 64KB
* Video: 
  * Mode 0: 256x192 bitmap with 32x24 16 color (from 64) overlay, in square 8x8 pixels two colors from 16 are given by overlay (double buffer capability)
  * Mode 1: 128x192 bitmap with 32x24 16 color (from 64) overlay, in square 4x8 two colors from 16 are given by overlay and two colors from 16 are global (double buffer capability)
  * Mode 2: 256x192 bitmap with 4 colors (from 64)
  * Mode 3: 128x96 bitmap with 16 colors (from 64) (double buffer capability)
  * Mode 4: 512x192 bitmap with 2 colors (from 64)
  * 16 color LUT for pixel area + 1 color LUT for border area. Entry in LUT is mapped to RRGGBB (64 colors) 

* Audio:
  * Custom 4 channel PSG with editable wave form, 8 volume levels. Any channels can be switched to noise mode
  * Output to internal speaker or stereo 3.5 mm jack
* I/O
  * Keyboard with 56 keys (matrix 8x7)
  * 2x Atari joystick connector (one with 2 analog lines) 
  * Tape in/out connector
  * TV out 
  * Analog (4 level) RGB
  * Composite video
  * Edge connector
  * Parallel port
  




