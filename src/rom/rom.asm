

SYS_TEMP0     EQU $BF00
SYS_TEMP1     EQU $BF01
SYS_TEMP2     EQU $BF02
SYS_TEMP3     EQU $BF03

SYS_GATTR     EQU $BFAE
SYS_MARTAREG  EQU $BFAF
SYS_TIMER     EQU $BFB0

SWI3_VECTOR   EQU $BFB4
SWI2_VECTOR   EQU $BFB6
SWI_VECTOR    EQU $BFB8
FIRQ_VECTOR   EQU $BFBA
IRQ_VECTOR    EQU $BFBC
NMI_VECTOR    EQU $BFBE     

MARTA_IO      EQU $BFC0



setBorder
     ;A border color
     LDB #$02
     JMP setMartaRegister

clearVideo
     ;clear screen
     LDA >SYS_MARTAREG
     ANDA #$03
     BEQ clearVideo0 
     DECA
     BNE clearVideo2
     ;clear video 1
     LDA >SYS_GATTR				;get global paper
     ANDA #$F0
     STA >SYS_TEMP0
     LSRA
     LSRA
     LSRA
     LSRA
     ORA >SYS_TEMP0                             ;construct fill byte 
     LDY #$1800
     BRA clearVideoA				;fill $1800 bytes by PAPER*16+PAPER
clearVideo2
     DECA
     BNE clearVideo3
     ;clear video 0,2
clearVideo0     
     CLRA                                        ;clear $1800 bytes from $0000
     LDY #$1800
     BSR clearVideoA
     LDA >SYS_GATTR				 ;clear $300 bytes by PAPER*16+INK
     LDX $1800
     LDY $300
     JMP fastFill
clearVideo3
     ;clear video mode 3
     LDA >SYS_MARTAREG
     ORA #$08                                    ;page shadow ram in
     CLRB
     JSR setMartaRegister
     LDY #$1E00
     JSR clearVideo9                             ;clear second half of screen by PAPER*64+PAPER*16+PAPER*4+PAPER
     LDA >SYS_MARTAREG
     AND #$F3                                    ;page base ram in and set video page 
     STA >SYS_MARTAREG
     CLRB
     JSR setMartaRegister
     LDY #$1E00                                  ;clear first half of screen by PAPER*64+PAPER*16+PAPER*4+PAPER
clearVideo9
     LDA >SYS_GATTR                              ;get global paper
     AND #$30
     LSLA
     LSLA
     STA >SYS_TEMP0
     LDB #$03
clearVideo9a
     LSRA >SYS_TEMP0
     LSRA >SYS_TEMP0
     ORA >SYS_TEMP0                              ;construct fill byte            
     DECB
     BNE clearVideo9a
clearVideoA     
     LDX #$0000                                  ;start fill from $0000
     JMP fastFill
     

setVideo
     ;A video mode
     ;write to Marta control register
     LDB >SYS_MARTAREG
     ANDB #$FC
     STB >SYS_MARTAREG
     ANDA #$03
     ORA >SYS_MARTAREG
     STA >SYS_MARTAREG
     CLRB
     JSR setMartaRegister
     RTS
  
setMartaRegister
     ;B register to set
     ;A value
     ANDB #$1F
     STB >$BFC0
     ORA #$80
     STA >$BFC0
     RTS
     
     
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

shortCopy
     ;X destination
     ;Y source
     ;B counter
           LDA ,Y+
	   STA ,X+
	   DECB
	   BNE shortCopy
	   RTS

fastFill
    ;A fill byte
    ;X destination
    ;Y counter

	  EXG D,Y
	  BITB #$01
	  EXG D,Y
	  BEQ fillEven
	  STA ,X+
	  LEAY -1,Y
	  BEQ fillExit
fillEven
	  TFR A,B
fillLoop
	  STD ,X++
	  LEAY -2,Y
	  BNE fillLoop                         ;3   -> 8clocks/byte
fillExit
	  RTS
	  
resetHandler
	  LDD #resetHandler
	  STD NMI_VECTOR                       ;set default NMI handler to RESET
	  LDS #$BDFE                           ;set stack below system variables
	  LDX #SWI3_VECTOR		       ;vectors in ram
	  LDY #defaultVectors                  ;default vectors
	  LDB #10                              ;5x2 bytes
	  JSR shortCopy                        ;copy default to vectors
	  LDD #$0000		               ;reset timer
          STD SYS_TIMER
          STD SYS_TIMER+2
          LDA #$BF                             ;set direct page 
          TFR A,DP
	  CLRA
	  JSR setVideo			       ;set graphics mode 0
	  LDA $10
	  JSR setBorder                        ;set border
	  
	  
	  
defaultIrqHandler
          LDD SYS_TIMER+2 		       ;increase four byte timer
          ADDD #$0001
          STD SYS_TIMER+2
          BCC defaultIrqHandler1
          LDX SYS_TIMER
          LEAX +1,X 
          STX SYS_TIMER
defaultIrqHandler1    
          JSR SYS_SCAN_KEYS 		            ;scan keyboard
defaultHandler
          RTI  			                    ;simply do nothing and leave

defaultVectors
         FDB defaultHandler                         ;default handler for SWI3
         FDB defaultHandler                         ;default handler for SWI2
         FDB defaultHandler                         ;default handler for SWI
         FDB defaultHandler                         ;default handler for FIRQ
         FDB defaultIrqHandler                      ;default handler for IRQ

nmiHandler
    JMP [NMI_VECTOR]		                    ;jump vector from NMI_VECTOR 

irqHandler
    JMP [IRQ_VECTOR]		                    ;jump vector from IRQ_VECTOR 

firqHandler
    JMP [FIRQ_VECTOR]		                    ;jump vector from FIRQ_VECTOR 

swiHandler
    JMP [SWI_VECTOR]		                    ;jump vector from SWI_VECTOR 

swi2Handler
    JMP [SWI2_VECTOR]		                    ;jump vector from SWI2_VECTOR 

swi3Handler
    JMP [SWI3_VECTOR]		                    ;jump vector from SWI3_VECTOR 

    ORG $FFF0

    FDB $FFFF			                    ;reserved
    FDB SWI3_HANDLER 		                    ;swi3 handler
    FDB SWI2_HANDLER		                    ;swi2 handler
    FDB FIRQ_HANDLER		                    ;firq handler
    FDB IRQ_HANDLER		                    ;irq handler
    FDB SWI_HANDLER 		                    ;swi handler
    FDB NMI_HANDLER		                    ;nmi handler
    FDB RESET_HANDLER		                    ;reset handler
