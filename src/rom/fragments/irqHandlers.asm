SWI3_HANDLER
  JMP [SWI3PTR]

SWI2_HANDLER
  JMP [SWI2PTR]

SWI_HANDLER
  JMP [SWIPTR]

FIRQ_HANDLER
  JMP [FIRQPTR]

IRQ_HANDLER
  JMP [IRQPTR]

DEFAULT_HANDLER
  ;default empty interrupt handler
  RTI

DEFAULT_IRQ
  ;default IRQ handler
  LDX TIME2            ;update four byte TIME 
  LEAX 1,X
  STX TIME2
  BNE defaultIrq1
  LDX TIME
  LEAX 1,X
  STX TIME
defaultIrq1
  JSR KEYBOARD         ;check keyboard
  RTI
