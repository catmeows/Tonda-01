


SYS_VAR_TIMER EQU $BFB0

SWI3_VECTOR   EQU $BFB4
SWI2_VECTOR   EQU $BFB6
SWI_VECTOR    EQU $BFB8
FIRQ_VECTOR   EQU $BFBA
IRQ_VECTOR    EQU $BFBC
NMI_VECTOR    EQU $BFBE     

MARTA_IO      EQU $BFC0

SET_VIDEO
     ;A video mode
     CLRB
     STB 
     LDB #$FC
     
     
     
SET_BITS
     ;A value
     ;B mask
     ;X memory ptr
           ANDB ,X
	   STB ,X
	   ORA ,X
	   STA ,X
	   RTS
fastCopyDec
        ;Y source
        ;X destination
        ;D count
	PSHS U
        TFR Y,U
        TFR D,Y
        BITB #$01
        BEQ fastCopyDecEven
        LDA ,U-
        STA ,X-
        LEAY -1,Y
        BEQ fastCopyExit
fastCopyDecEven
        BITB #$02
        BEQ fastCopyDec4
        LDD ,U--
        STD ,X--
        LEAY -2,Y
        BEQ fastCopyExit
fastCopyDec4
        LDD ,U--
        STD ,X--
        LDD ,U--
        STD ,X--
        LEAY -4,Y
        BNE fastCopyDec4
        BRA fastCopyExit
        
fastCopyInc
	;Y source
        ;X destination
        ;D count
	PSHS U
        TFR Y,U
        TFR D,Y
        BITB #$01
	BEQ fastCopyIncEven
	LDA ,U+
        STA ,X+
        LEAY -1,Y
        BEQ fastCopyExit
fastCopyIncEven
        BITB #$02
        BEQ fastCopyInc4
        LDD ,U++
        STD ,X++
        LEAY -2,Y
        BEQ fastCopyExit
fastCopyInc4
        LDD ,U++    ;5+3
        STD ,X++    ;5+3
        LDD ,U++    ;5+3
        STD ,X++    ;5+3
        LEAY -4,Y   ;4+1
        BNE fastCopyInc4 ;3
fastCopyExit
        PULS U
        RTS           

SHORT_COPY
     ;X destination
     ;Y source
     ;B counter
           LDA ,Y+
	   STA ,X+
	   DECB
	   BNE SHORT_COPY
	   RTS

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
	  
RESET_HANDLER
	  LDD #RESET_HANDLER
	  STD NMI_VECTOR                       ;set default NMI handler to RESET
	  LDS #$BDFE                           ;set stack below system variables
	  LDX #SWI3_VECTOR		       ;vectors in ram
	  LDY #DEFAULT_VECTORS                 ;default vectors
	  LDB #10                              ;5x2 bytes
	  JSR SHORT_COPY                       ;copy default to vectors
	  LDD #$0000		               ;reset timer
          STD SYS_VAR_TIMER
          STD SYS_VAR_TIMER+2
          LDA #$BF                             ;set direct page 
          TFR A,DP

DEFAULT_IRQ_HANDLER
          LDD SYS_VAR_TIMER+2 		       ;increase four byte timer
          ADDD #$0001
          STD SYS_VAR_TIMER+2
          BCC DEFAULT_IRQ_HANDLER1
          LDX SYS_VAR_TIMER
          LEAX +1,X 
          STX SYS_VAR_TIMER
DEFAULT_IRQ_HANDLER1    
          JSR SYS_SCAN_KEYS 		       ;scan keyboard
DEFAULT_HANDLER
          RTI  			               ;simply do nothing and leave

DEFAULT_VECTORS
         FDB DEFAULT_HANDLER                        ;default handler for SWI3
         FDB DEFAULT_HANDLER                        ;default handler for SWI2
         FDB DEFAULT_HANDLER                        ;default handler for SWI
         FDB DEFAULT_HANDLER                        ;default handler for FIRQ
         FDB DEFAULT_IRQ_HANDLER                    ;default handler for IRQ

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
