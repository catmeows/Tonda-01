# Tonda-01
Fantasy computer based on 6809 cpu

## Hardware

* CPU: 6809 @ 1.7734475 
* ROM: 16KB
* RAM: 64KB
* Video: 
  * Mode 0: 320x176 bitmap with 40x24 16 color (from 64) overlay, in square 8x8 pixels two colors from 16 are given by overlay (double buffer capability)
  * Mode 1: 160x88 bitmap  with 16 colors (from 64) (double buffer capability)
  * 16 color LUT for pixel area + 1 color LUT for border area. Entry in LUT is mapped to RRGGBB (64 colors) 

* Audio:
  * Custom 4 channel PSG with editable wave form, 8 volume levels. Any channels can be switched to noise mode
  * Output to internal speaker or stereo 3.5 mm jack
* I/O
  * Keyboard with 56 keys (matrix 8x7)
  * 2 x DIN connector for two seven button gamepads 
  * Tape in/out connector
  * TV out 
  * Analog (4 level) RGB + Composite video (DIN connector)
  * Edge connector
  * Centronics parallel port
 
* Video/Audio provided by single 40-pin ASIC
* I/O provided by 6821 
  




