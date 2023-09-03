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
    LDY #charA
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

    FCB $00, $0E, $1E, $36, $66, $7F, $06, $00 ;four 4
    FCB $00, $7F, $60, $7E, $03, $63, $3E, $00 ;five 5
    FCB $00, $3E, $60, $7E, $63, $63, $3E, $00 ;six 6
    FCB $00, $7F, $03, $06, $0C, $18, $18, $00 ;seven 7
    FCB $00, $3E, $63, $3E, $63, $63, $3E, $00 ;eight 8
    FCB $00, $3E, $63, $63, $3F, $03, $3E, $00 ;nine 9
    FCB $00, $00, $0C, $0C, $00, $0C, $0C, $00 ;colon :
    FCB $00, $00, $0C, $0C, $00, $0C, $0C, $18 ;semicolon ;
    FCB $00, $00, $06, $0C, $18, $0C, $06, $00 ;less-than <
    FCB $00, $00, $00, $7F, $00, $7F, $00, $00 ;equal =

    FCB $00, $00, $18, $0C, $06, $0C, $18, $00 ;greater-than >
    FCB $00, $3E, $63. $06, $0C, $00, $0C, $00 ;question mark ?
    FCB $00, $3E, $67, $6B, $6F, $60, $3E, $00 ;ampersand @
charA
    FCB $00, $3E, $63, $63, $7F, $63, $63, $00 ; A
    FCB $00, $7E, $63, $7E, $63, $63, $7E, $00 ; B
    FCB $00, $3E, $63, $60, $60, $63, $3E, $00 ; C
    FCB $00, $7C, $66, $63, $63, $66, $7C, $00 ; D
    FCB $00, $7F, $60, $7E, $60, $60, $7F, $00 ; E
    FCB $00, $7F, $60, $7E, $60, $60, $60, $00 ; F
    FCB $00, $3E, $63, $60, $6F, $63, $3E, $00 ; G

    FCB $00, $63, $63, $7F, $63, $63, $63, $00 ; H
    FCB $00, $3F, $0C, $0C, $0C, $0C, $3F, $00 ; I
    FCB $00, $03, $03, $03, $63, $63, $3E, $00 ; J
    FCB $00, $66, $6C, $78, $6C, $66, $63, $00 ; K
    FCB $00, $60, $60, $60, $60, $60, $7F, $00 ; L
    FCB $00, $63, $77, $7F, $6B, $63, $63, $00 ; M
    FCB $00, $63, $73, $7B, $6F, $67, $63, $00 ; N
    FCB $00, $3E, $63, $63, $63, $63, $3E, $00 ; O
    FCB $00, $7E, $63, $63, $7E, $60, $60, $00 ; P
    FCB $00, $3E, $63, $63, $6F, $6E, $3B, $00 ; Q

    FCB $00, $7E, $63, $63, $7E, $66, $63, $00 ; R
    FCB $00, $3E, $60, $3E, $03, $63, $3E, $00 ; S
    FCB $00, $3F, $0C, $0C, $0C, $0C, $0C, $00 ; T
    FCB $00, $63, $63, $63, $63, $63, $3E, $00 ; U
    FCB $00, $63, $63, $63, $63, $36, $1C, $00 ; V
    FCB $00, $63, $63, $63, $6B, $7F, $36, $00 ; W
    FCB $00, $63, $36, $1C, $1C, $36, $63, $00 ; X
    FCB $00, $63, $36, $1C, $0C, $0C, $0C, $00 ; Y
    FCB $00, $7F, $06, $0C, $18, $30, $7F, $00 ; Z
    FCB $00, $1E, $18, $18, $18, $18, $1E, $00 ; [

    FCB $00, $60, $30, $18, $0C, $06, $03, $00 ; backslash \
    FCB $00, $1E, $06, $06, $06, $06, $1E, $00 ; ]
    FCB $00, $0C, $1E, $3F, $0C, $0C, $0C, $00 ; arrow up ^
    FCB $00, $00, $00, $00, $00, $00, $00, $FF ; undercore _
    FCB $00, $1E, $33, $78, $30, $30, $7F, $00 ; pound £
    FCB $00, $00, $3E, $03, $3F, $63, $3F, $00 ; a
    FCB $00, $60, $60, $7E, $63, $63, $7E, $00 ; b
    FCB $00, $00, $3F, $60, $60, $60, $3F, $00 ; c
    FCB $00, $03, $03, $3F, $63, $63, $3F, $00 ; d
    FCB $00, $00, $3E, $63, $7E, $60, $3E, $00 ; e
    FCB $00, $1F, $30, $3C, $30, $30, $30, $00 ; f
    FCB $00, $00, $3F, $63, $63, $3F, $03, $3E ; g
    FCB $00, $60, $60, $7E, $63, $63, $63, $00 ; h
    FCB $00, $0C, $00, $1C, $0C, $0C, $3F, $00 ; i
    FCB $00, $03, $00, $07, $03, $03, $63, $3E ; j
    FCB $00, $60, $66, $7C, $7C, $66, $63, $00 ; k
    FCB $00, $30, $30, $30, $30, $30, $1F, $00 ; l
    FCB $00, $00, $76, $7F, $6B, $6B, $6B, $00 ; m
    FCB $00, $00, $7E, $63, $63, $63, $63, $00 ; n
    FCB $00, $00, $3E, $63, $63, $63, $3E, $00 ; o
    FCB $00, $00, $7E, $63, $63, $7E, $60, $60 ; p
    FCB $00, $00, $3F, $63, $63, $3F, $03, $03 ; q
    FCB $00, $00, $1F, $60, $60, $60, $60, $00 ; r
    FCB $00, $00, $3E, $60, $3E, $03, $7E, $00 ; s
    FCB $00, $30, $7C, $30, $30, $30, $1F, $00 ; t
    FCB $00, $00, $63, $63, $63, $63, $3E, $00 ; u
    FCB $00, $00, $63, $63, $36, $36, $1C, $00 ; v
    FCB $00, $00, $63, $6B, $6B, $7F, $36, $00 ; w
    FCB $00, $00, $63, $36, $1C, $36, $63, $00 ; x
    FCB $00, $00, $63, $63, $63, $3F, $03, $3E ; y
    FCB $00, $00, $7E, $0C, $18, $30, $7F, $00 ; z
    FCB $00, $07, $0C, $0C, $38, $0C, $0C, $07 ; {
    FCB $00, $0C, $0C, $0C, $0C, $0C, $0C, $00 ; |
    FCB $00, $38, $0C, $0C, $07, $0C, $0C, $38 ; }
    FCB $00, $3B, $6E, $00, $00, $00, $00, $00 ; ~
    FCB $00, $7E, $C3, $DB, $D3, $DB, $C3, $7E ; copyright (c)


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