WRITE_FRIDA
  ;A=register
  ;B=value
  STA $BFC0
  STB $BFC1
  RTS

READ_FRIDA
  ;A=register
  ;returns port reading in B
  STA $BFC0
  LDB $BFC1
  RTS

WRITE_LED
  ;B=off($00)/on($01)
  ADDB #$1F
  ANDB #$20
  STB <TEMP0
  LDA #$02
  BSR READ_FRIDA
  ANDB #$1F
  ORB <TEMP0
  BRA WRITE_FRIDA

READ_KEYROW
  ;B=select keyboard row
  ;returns row reading
  STB <TEMP0
  LDA $$02
  BSR READ_FRIDA
  ANDB #$F8
  ORB <TEMP0
  BSR WRITE_FRIDA
  LDA #$04
  BRA READ_FRIDA
