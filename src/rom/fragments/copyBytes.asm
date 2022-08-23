COPY
  ;X=source
  ;U=destination
  ;D=count
  PSHS Y
  CMPD #$0000
  BEQ copyEnd
copy1
  STU <TEMP0
  CMPX <TEMP0
  BCS copyDec
copyInc
  LDA ,X+
  STA ,U+
  LEAY -1,Y
  BNE copyInc
copyEnd
  PULS Y
  RTS
copyDec
  LEAX D,X
  LEAX -1,X
  LEAU D,U
  LEAU -1,U
copyDec1
  LDA ,X-
  STA ,U-
  LEAY -1,Y
  BNE copyDec1
  BRA copyEnd
