RESET
  CLRA                 ;reset timer
  CLRB
  STD TIME
  STD TIME+2
  LDD #$320A           ;set error sound to 1 second, set key sound 200 ms
  STA <ERR_BEEP
  STB <KEY_BEEP
  LDU #$BD30           ;set UDG to $BD30
  STU <UDG
  STU <RAMTOP          ;set RAMTOP to $BD30 
  LDX #UDG_
  LDB #$D0
copyUdg
  LDA ,X+
  STA ,U+
  DECB
  BNE copyUdg
EXEC_NEW
  ORCC #$50            ;disable all interrupts
  LDD DEFAULT_HANDLER  ;set default handlers for unused interrupts
  STD SWI3PTR
  STD SWI2PTR
  STD SWIPTR
  STD FIRQPTR
  LDD DEFAULT_IRQ      ;set default handler for IRQ
  STD IRQPTR
  LDA #$BE             ;set direct page
  TFR A,DP                          
  LDS <RAMTOP          ;set stack to RAMTOP
  ANDCC #$EF           ;enable IRQ
  
  LDB #COL_LGREY       ;set light grey border
  JSR WRITE_BORDER   

  LDX #DEF_PROG        ;init PROG, start of program 
  STX <PROG
