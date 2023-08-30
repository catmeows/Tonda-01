;
; TONDA 01 Basic rom source
;
; This file is meant to be assembled by ASM6809 cross assembler
;


; DEFAULT DIRECT PAGE SYSTEM VARIABLES
; $BE00 - $BEFF

SETDP $BE                                      ;Basic always assume direct page at $BE00

SYS_CHARS                      EQU $BE00       ;pointer to character patterns
SYS_UDG                        EQU $BE02       ;pointer to udg patterns
SYS_PIP                        EQU $BE04       ;length of keyboard click
SYS_BUZZ                       EQU $BE05       ;lenght of warning buzz

; DEFAULT OTHER SYSTEM VARIABLES
; $BF00 - $BFBF

IRQ_VECTOR                     EQU $BF00       ;vector for IRQ
FIRQ_VECTOR		               EQU $BF02       ;vector for FIRQ
SWI_VECTOR		               EQU $BF04       ;vector for SWI
SWI2_VECTOR		               EQU $BF06       ;vector for SWI2
SWI3_VECTOR		               EQU $BF08       ;vector for SWI3
NMI_VECTOR                     EQU $BF0A       ;vector for NMI
SYS_RAMTOP                     EQU $BF0C       ;address of last byte usable for Basic


; ROM TRAPS
; $C000 - $C0FF

    ORG $C000



; ROM TRAPS
; $C100 - $C1FF

    ORG $C100



; 6821 MEMORY MAPPED I/O
; $DFC0 - $DFDF    32 bytes

    ORG $DFC0


; DISK MEMORY MAPPED I/O
; $DFE0 - $DFFF    32 bytes

    ORG $DFE0


; REGULAR ROM
; $E000 - $FFFF

    ORG $E000



    ; EMPTY IRQ HANDLER
    ; does nothing
    ; used as default for FIRQ, SWI, SWI2, SWI3
emptyIrq
    RTI


    ; RESET HANDLER
    ; will setup computer after cold start
resetHandler
    LDA #$BE
    TFR A, DP                                  ;set DP register
    LDD #resetHandler
    STD NMI_VECTOR                             ;set NMI vector to reset
    LDD #defaultIrq
    STD IRQ_VECTOR                             ;set default IRQ vector
    LDD #emptyIrq
    STD FIRQ_VECTOR                            ;set empty FIRQ vector
    LDD #$3205
    STD SYS_PIP                                ;set SYS_PIP (keyboard click) and SYS_BUZZ (error warning)
    LDD #$BD80
    STD SYS_UDG                                ;set user defined graphics pointer
    TFR D, S                                   ;set stack right under UDG
    LDB #$7F
    STD SYS_RAMTOP                             ;set SYS_RAMTOP (last byte usable by Basic)
    LDX #$0080
    LDY charA
    LDU SYS_UDG
    JSR copyBytes                              ;copy character patterns to UDG area


    ; COPY BYTES
    ; copy X bytes from Y to U
copyBytes
    LDA ,Y+
    STA ,U+
    LEAU -1,X
    BNE copyBytes
    RTS

irqHandler
    JMP [IRQ_VECTOR]		                   ;jump vector from IRQ_VECTOR
firqHandler
    JMP [FIRQ_VECTOR]		                   ;jump vector from FIRQ_VECTOR
swiHandler
    JMP [SWI_VECTOR]		                   ;jump vector from SWI_VECTOR
swi2Handler
    JMP [SWI2_VECTOR]		                   ;jump vector from SWI2_VECTOR
swi3Handler
    JMP [SWI3_VECTOR]		                   ;jump vector from SWI3_VECTOR
nmiHandler
    JMP [NMI_VECTOR]                           ;jump vector from NMI_VECTOR


; CHARACTER PATTERNS
;

font
    FCB $00, $00, $00, $00, $00, $00, $00, $00 ;space
    FCB $00, $0C, $0C, $0C, $0C, $00, $0C, $00 ;exclamation mark !
    FCB $00, $36, $36, $36, $00, $00, $00, $00 ;double quote "
    FCB $00, $36, $7F, $36, $36, $7F, $36, $00 ;hash #
    FCB $00, $0C, $3F, $6C, $3E, $1B, $7E, $18 ;dollar $
    FCB $00, $63, $66, $0C, $18, $33, $63, $00 ;percent %
    FCB $00, $1C, $36, $3C, $7B, $6E, $3F, $00 ;ampersand &
    FCB $00, $0C, $0C, $18, $00, $00, $00, $00 ;apostrophe '
    FCB $00, $0C, $18, $18, $18, $18, $0C, $00 ;left bracket (
    FCB $00, $18, $0C, $0C, $0C, $0C, $18, $00 ;right bracket )

    FCB $00, $00, $36, $1C, $7F, $1C, $36, $00 ;asterisk *
    FCB $00, $00, $0C, $0C, $3F, $0C, $0C, $00 ;plus +
    FCB $00, $00, $00, $00, $00, $0C, $0C, $18 ;comma ,
    FCB $00, $00, $00, $00, $7F, $00, $00, $00 ;minus -
    FCB $00, $00, $00, $00, $00, $0C, $0C, $00 ;period .
    FCB $00, $03, $06, $0C, $18, $30, $60, $00 ;slash /
    FCB $00, $3E, $67, $6F, $7B, $73, $3E, $00 ;zero 0
    FCB $00, $1C, $3C, $0C, $0C, $0C, $3F, $00 ;one 1
    FCB $00, $3E, $63, $03, $3E, $60, $7F, $00 ;two 2
    FCB $00, $3E, $63, $0E, $03, $63, $3E, $00 ;three 3




; CPU INTERRUPT VECTORS
; $FFF0 - $FFFF

    ORG $FFF0

    FDB $FFFF			                       ;reserved
    FDB swi3Handler                            ;swi3 handler
    FDB swi2Handler                            ;swi2 handler
    FDB firqHandler                            ;firq handler
    FDB irqHandler                             ;irq handler
    FDB swiHandler                             ;swi handler
    FDB nmiHandler                             ;nmi handler
    FDB resetHandler                           ;reset handler