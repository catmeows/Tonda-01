     
SWI3_VECTOR   EQU $BFB4
SWI2_VECTOR   EQU $BFB6
SWI_VECTOR    EQU $BFB8
FIRQ_VECTOR   EQU $BFBA
IRQ_VECTOR    EQU $BFBC
NMI_VECTOR    EQU $BFBE     


FastFill

A=fill byte
X=destination
Y=counter

FAST_FILL
    ;A fill byte
    ;X destination
    ;Y counter

	  EXG D,Y
	  BITB #$01
	  EXG D,Y
	  BEQ FILL_EVEN
	  STA ,X+
	  LEAY -1,Y
	  BEQ FILL_EXIT
FILL_EVEN
	  TFR A,B
FILL_LOOP
	  STD ,X++
	  LEAY -2,Y
	  BNE FILL_LOOP    ;3   -> 8clocks/byte
FILL_EXIT
	  RTS


DEFAULT_IRQ_HANDLER
    LDD SYS_VAR_TIMER+2 		          ;increase four byte timer
    ADDD #$0001
    STD SYS_VAR_TIMER+2
    BCC DEFAULT_IRQ_HANDLER1
    LDX SYS_VAR_TIMER
    LEAX +1,X 
    STX SYS_VAR_TIMER
DEFAULT_IRQ_HANDLER1    
    JSR SYS_SCAN_KEYS 		            ;scan keyboard
DEFAULT_HANDLER
    RTI  			                        ;simply do nothing and leave

DEFAULT_VECTORS
    FDB DEFAULT_HANDLER               ;default handler for SWI3
    FDB DEFAULT_HANDLER               ;default handler for SWI2
    FDB DEFAULT_HANDLER               ;default handler for SWI
    FDB DEFAULT_HANDLER               ;default handler for FIRQ
    FDB DEFAULT_IRQ_HANDLER           ;default handler for IRQ

NMI_HANDLER
    JMP [NMI_VECTOR]		              ;jump vector from NMI_VECTOR 

IRQ_HANDLER
    JMP [IRQ_VECTOR]		              ;jump vector from IRQ_VECTOR 

FIRQ_HANDLER
    JMP [FIRQ_VECTOR]		              ;jump vector from FIRQ_VECTOR 

SWI_HANDLER
    JMP [SWI_VECTOR]		              ;jump vector from SWI_VECTOR 

SWI2_HANDLER
    JMP [SWI2_VECTOR]		              ;jump vector from SWI2_VECTOR 

SWI3_HANDLER
    JMP [SWI3_VECTOR]		              ;jump vector from SWI3_VECTOR 

    ORG $FFF0

    FDB $FFFF			                    ;reserved
    FDB SWI3_HANDLER 		              ;swi3 handler
    FDB SWI2_HANDLER		              ;swi2 handler
    FDB FIRQ_HANDLER		              ;firq handler
    FDB IRQ_HANDLER		                ;irq handler
    FDB SWI_HANDLER 		              ;swi handler
    FDB NMI_HANDLER		                ;nmi handler
    FDB RESET_HANDLER		              ;reset handler
