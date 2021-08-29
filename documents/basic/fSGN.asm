fSGN
  TST 1,U                           ;check sign on most significant byte
  BMI fSGN1                         ;negative number           
  CLRA
  ORA 1,U
  ORA 2,U
  ORA 3,U
  ORA 4,U                           ;check other four bytes being zero
  BNE fSGN2
  RTS                               ;it was zero, return zero
fSGN1
  LDX #constMinusOne
  BRA fSGN3
fSGN2
  LDX #constPlusOne
  JSR dropNumber
  JSR storeNumberX
