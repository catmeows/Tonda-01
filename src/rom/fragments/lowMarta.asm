;=
;= LOW LEVEL ACCESS TO MARTA
;=

READ_MARTA
  ;returns register 0 (MAP) in A
  LDA $BF80
  RTS

WRITE_MARTA0
  STA <TEMP0
  ORB <TEMP0
  CLRA
WRITE_MARTA
  ;A=register
  ;B=value to write
  ANDA #$1F
  STA $BF80     
  ANDB #$7F
  ORB #$80
  STA $BF80
  RTS

WRITE_BORDER
  ;B=color of border
  LDA #$02
  BRA WRITE_MARTA

WRITE_FIRQ
  ;B=interrupt frequency
  ;0..never, 1-127..after 1-127 lines
  LDA #$01
  BRA WRITE_MARTA

WRITE_COLOR
  ;A=color index
  ;B=color value
  ANDA #$0F
  ORA #$10
  BRA WRITE_MARTA

WRITE_MODE
  ;B=video mode
  BSR READ_MARTA
  ANDA #$FC
  BRA WRITE_MARTA0

WRITE_PAGE
  ;B=video page
  BSR READ_MARTA
  ANDA #$F7
  ANDB #$01
  ASLB
  ASLB
  ASLB
  BRA WRITE_MARTA0

WRITE_BANK
  ;B=memory bank
  BSR READ_MARTA
  ANDA #$EF
  ANDB #$01
  ASLB
  ASLB
  ASLB
  ASLB
  BRA WRITE_MARTA0  
      
