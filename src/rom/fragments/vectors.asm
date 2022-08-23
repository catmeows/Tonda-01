  ORG $FFF0

  FDB $0000         ;reserved
  FDB SWI3_HANDLER
  FDB SWI2_HANDLER  
  FDB FIRQ_HANDLER  
  FDB IRQ_HANDLER
  FDB SWI_HANDLER
  FDB RESET         ;nmi is handled as reset 
  FDB RESET
