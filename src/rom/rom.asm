ERR_OK     EQU $00
ERR_BREAK  EQU $01
ERR_SYNTAX EQU $02
ERR_STOP   EQU $03

CARRY_BIT  EQU $01

NULL       EQU $00
SHORT_INT  EQU $01
HEX_NUM    EQU $02
HEX_END    EQU $03
NUM        EQU $04
STRING     EQU $05
CUR_LEFT   EQU $06
CUR_RIGHT  EQU $07
CUR_UP     EQU $08
CUR_DOWN   EQU $09

TRUE_VIDEO EQU $0b
INV_VIDEO  EQU $0c
EOL        EQU $0d
LABEL      EQU '@'
REMARK     EQU $27


FONTPTR    EQU $0000  ;2B
UDGPTR     EQU $0000  ;2B
PRINTATTR  EQU $0000
PRINTTATTR EQU $0000
PRINTINV   EQU $0000
PRINTTINV  EQU $0000
PRINTOVER  EQU $0000
PRINTTOVER EQU $0000  ;TODO
VIDEOMODE  EQU $0000  ;
PRINTLINE  EQU $0000
PRINTCOLU  EQU $0000
WINDOWRGHT EQU $0000  ;1B
WINDOWLEFT EQU $0000  ;1B
WINDOWBOT  EQU $0000  ;1B
WINDOWTOP  EQU $0000  ;1B
PRINTWRAP  EQU $0000  ;1B

;========================
;= TOKENIZER
;========================

findKeyword
    ;Y..first character in buffer, X..first character in keyword table, B..keyword id
    ;returns keyword code in B
    ;carry set if success
    PSHS Y
keywordLoop
    LDA ,Y                             ;read next character from buffer
    JSR isAplpha                       ;check, if it is alpha
    BCS keywordNotMatch
    CMPA #$61                          ;check lower case
    BCS keywordValidCase
    SUB #$20                           ;convert lower case to upper case
keywordValidCase
    XORA ,X                            ;xor character with character from token
    AND #$7F                           ;and discard bit 7
    BNE keywordNotMatch                ;are the characters same ?
    LDA ,X                             ;read character from token one more time
    BPL keywordContinue                ;last character should have bit 7 set, is it so ?
    LDA 1,Y                            ;check next character in buffer
    JSR isAlphaNum                     ;if identifier in buffer continues, then this is not a keyword
    BCS keywordSuccess
keywordNotMatch
    PULS Y                             ;not match, we start scan again
    INC B                              ;increment keyword id
    BNE keywordNotMatch1               ;there is more keywords to check
    ANDCC #$FE                         ;keyword not found, reset carry
    RTS
keywordNotMatch1
    LDA ,X+                            ;find end of current keyword
    BPL keywordNotMatch1
    BRA keywordNext                    ;continue to next keyword
keywordSuccess
    LEAS 2,S                           ;discard old Y
    LEAY 1,Y                           ;set Y to next character in buffer
    RTS
keywordContinue
    LEAX 1,X                           ;advance one character in keyword table
    LEAY 1,Y                           ;advance one character in buffer
    BRA keywordLoop                    ;continue to check next character

isAlphaNum
    ;reset carry if character in <a..z,A..Z,0..9>
    BSR isDigit                        ;check is digit
    BCC isDigit2                       ;if so, leave, otherwise continue to isAlpha
                                        
isAlpha
    ;reset carry if character in <a..z,A..Z>
    CMPA #$41                          ;carry set if character < 'A'
    BCS isDigit2                       ;leave with carry set
    CMPA #$5B                          ;carry set if character <= 'Z'
    BCS isDigit3                       ;leave to reset carry for 'A'..'Z'
    CMPA #$61                          ;carry set if character < 'a'
    BCS isDigit2                       ;leave with carry set
    CMPA #$7B                          ;carry set if character <= 'z'
    BCS isDigit3                       ;leave to reset carry for 'a'..'z'
    BRA isDigit1                       ;leave to carry set

isDigit
    ;reset carry if character in <0..9>
    CMPA #$30
    BCS isDigit2                       ;carry set if character < '0'
    CMPA #40
    BCC isDigit1                       ;carry reset if character > '9'
isDigit3    
    ANDCC #$FE                         ;reset carry
    RTS
isDigit1    
    ORCC #$CARRY_BIT                   ;carry set
isDigit2
    RTS

;========================
;= LINE EDITOR
;========================
;input line can have inmutable header text (for use in INPUT and MONITOR)
;it starts on last line (23rd) but can take more lines, expanding up
;cursor is blinking
;cursor is always behind current position
;left and right to move along line
;up and down to move along line
;cancel/break operation
;autorepeat 
;character limit vs screen limit


;========================
;= MONITOR
;========================
;find string
;copy lines
;delete lines
;replace string
;merge basic
;0123456789012345
;FIND STRING
;what ? xxxxxxxx
;first line ? xxxx
;last line ? xxxx
;edit ?
;COPY LINE
;first line ?
;last line ? 
;where ?
;DELETE LINE
;first line ?
;last line ?
;are you sure ? (Yes)
;REPLACE STRING
;what ?
;by ?
;first line ?
;last line ?
;replace ? (Yes/Always)
;MERGE
;file ?
;where ?
;PROG
;VARS
;STACK
;FREE


;========================
;= COMMAND RUNTIME
;========================

commStop
    ;STOP
    ;expects no parameter
    ;throws error 'STOPP
    JSR expectColon
    LDA #ERR_STOP
    JMP error
commTrace
    JSR expectNumExpr
    

;========================
;= INTERPRETER
;========================

execLine
    LDD ,Y++                          ;read line number
    STD <LINE_NO                      ;store line number
    CMPA #$FF                         ;is it end of program ?
    BNE execLine1                     ;not end of program
    CLRA                              ;will report 'OK' error
    BRA execCommand4                  ;continue to error
execLine1    
    BSR getPackedLength               ;get line length
    LEAX D,Y                          ;compute ptr to next line
    STX <NXT_LINE                     ;store ptr to next line
    CLR <COMMAND_NO                   ;reset command counter
    BSR execCommand                   ;execute all commands in line
    LDY <NXT_LINE                     ;load pointer to next line
    BRA execLine                      ;loop program execution
    
execCommand
    INC <COMMAND_NO                   ;increase number of current command
    LDA <IS_BREAK                     ;check BREAK pressed
    BNE execCommand4                  ;A is filled by $01, so report Break
    LDA <TRACE_MODE                   ;check trace mode
    BEQ execCommand5                  ;no trace
    JSR traceHandler                  ;dispatch trace
execCommand5    
    BSR getPackedLength               ;get command length
    LEAX D, Y                         ;compute ptr of next command
    STX <NXT_COMMAND                  ;store address to next command
    BSR getNextNonWhite               ;read next non white character
    CMPA #LEADINGTOKEN                ;compare with first command token 
    BCS execCommand1                  ;if command token then execute it
    CMPA #EOL                         ;if end-of-line then leave inner execution loop
    BEQ execCommand2                  
    CMPA #REMARK                      ;if comment then ignore rest of line 
    BEQ execCommand2
    CMPA #LABEL                       ;if label then continue to next command
    BNE execCommand3                  ;otherwise report error 'Syntax error'
execNextCommand    
    LDY <NXT_COMMAND                  ;continue
    BRA execCommand
execCommand2
    RTS                               ;leave line
execCommand3
    LDA #ERR_SYNTAX                   ;report syntax error
execCommand4    
    JMP error
execCommand1
    SUBA #LEADINGTOKEN
    ASLA
    LDX #commandTable
    JMP [A,X]

getNextNonWhite
    LDA ,Y+
    CMP #$20
    BEQ getNextNonWhite
    RTS

getPackedLength
    ;get length from 1 or 2 bytes -> 1..511
    LDB ,Y+
    BEQ getPackedLength1
    CLRA
    RTS
getPackedLength1
    LDA #$01
    LDB ,Y+
    RTS
    
error
    STA <ERR                               ;store error number
    JSR printAtError                       ;set temporary print flags
    LDX #errorMsgs                         ;use error msg table
    LDA <ERR                               ;get index to table
    JSR printMsg                           ;print msg from table
    BSR printSpc                           ;print single space
    LDD <LINE_NO                           ;print line number
    JSR printNum16                         
    LDA #':'                               ;print ':'
    JSR printChar                          
    LDA <COMMAND_NO                        ;print statement number
    JSR printNum8
    ;TODO
    
printSpc
    LDA #$20
    JMP printChar
    
printMsg
    ;enters with X as pointer to table, A as index into table
    TSTA                                    ;is it this message ?
printMsg3    
    BEQ printMsg1                           ;yes
printMsg2    
    LDB ,X+                                 ;get byte
    BPL printMsg2                           ;not the last byte of this msg
    DECA                                    ;decrease msg counter
    BRA printMsg3                           ;try next
printMsg1
    LDA ,X                                  ;read next character 
    JSR printChar                           ;print character
    LDA ,X+                                 ;advance pointer
    BPL printMsg1                           ;check if it was last character
    RTS
    
expectColon
    ;check next non white character, throw error if not colon or EOL
    JSR getNextNonWhite                     ;get next non white character
    cp #$3a                                 ;compare with colon
    BEQ expectColon1
    cp #EOL                                 ;compare with EOL
    BEQ expectColon1
    LDA #$02                                ;otherwise leave with 'Synatx error'
    JMP error
expectColon1
    RTS
    
commandTable
    FDB 0
    FDB 0
    FDB 0

;========================
;= ERROR MESSAGES
;========================
errorMsgs
    FCC "O",$80+'K'                        ;error 0
    FCC "Brea",$80+'k'                     ;error 1
    FCC "Syntax erro",$80+'r'              ;error 2
    FCC "STOP statemen",$80+'t'            ;error 3
   
    
    FCC "TONDA BASIC ",$7F,"202",$80+'2'
   
;========================
;= TOKENS
;========================
tokenTable
    ;helpers
    FCC "THE",$80+'N'
    FCC "STE",$80+'P'
    FCC "T",$80+'O'
    FCC "LIN",$80+'E'
    FCC "TRU",$80+'E'
    FCC "FALS",$80+'E'
    ;operators
    FCC "<",$80+'>'
    FCC ">",$80+'='
    FCC "<",$80+'='
    FCC "=",$80+'='
    FCC "SH",$80+'L'
    FCC "SH",$80+'R'
    FCC "NO",$80+'T'
    FCC "O",$80+'R'
    FCC "AN",$80+'D'
    ;number functions   
    FCC "VA",$80+'L'
    FCC "MATC",$80+'H'
    FCC "LE",$80+'N'
    FCC "COD",$80+'E'
    FCC "ISCHA",$80+'R'
    FCC "RN",$80+'D'
    FCC "P",$80+'I'
    FCC "ATA",$80+'N'
    FCC "AC",$80+'S'
    FCC "AS",$80+'N'
    FCC "TA",$80+'N'
    FCC "CO",$80+'S'
    FCC "SI",$80+'N'
    FCC "MA",$80+'X'
    FCC 'MI',$80+'N'
    FCC 'LO',$80+'G'
    FCC 'EX',$80+'P'
    FCC 'SQ',$80+'R'
    FCC 'SG',$80+'N'
    FCC 'AB',$80+'S'
    FCC 'JO',$80+'Y'
    FCC 'DEE',$80+'K'
    FCC 'PEE',$80+'K'
    FCC 'TIM',$80+'E'
    FCC 'POIN',$80+'T'
    FCC 'ATT',$80+'R'
    FCC 'PO',$80+'S'
    ;string functions
    FCC 'STR',$80+'$'
    FCC 'SPC',$80+'$'
    FCC 'CHR',$80+'$'
    FCC 'INKEY',$80+'$'
    FCC 'SCREEN',$80+'$'
    FCC 'CAT',$80+'$'
    FCC 'FILE',$80+'$'
    ;leading tokens
    FCC 'PRTSC',$80+'R'
    FCC 'LLIS',$80+'T'
    FCC 'LPRIN',$80+'T'
    FCC 'SOUN',$80+'D'
    FCC 'BEE',$80+'P'
    FCC 'INPU',$80+'T'
    FCC 'RANDOMIZ',$80+'E'
    FCC 'SY',$80+'S'
    FCC 'DOK',$80+'E'
    FCC 'POK',$80+'E'
    FCC 'CLEA',$80+'R'
    FCC 'UD',$80+'G'
    FCC 'PAPE',$80+'R'
    FCC 'IN',$80+'K'
    FCC 'PE',$80+'N'
    FCC 'PRIN',$80+'T'
    FCC 'OVE',$80+'R'
    FCC 'INVERS',$80+'E'
    FCC 'BORDE',$80+'R'
    FCC 'COLO',$80+'R'
    FCC 'CSIZ',$80+'E'
    FCC 'FIL',$80+'L'
    FCC 'CIRCL',$80+'E'
    FCC 'DRA',$80+'W'
    FCC 'PLO',$80+'T'
    FCC 'CL',$80+'W'
    FCC 'WINDO',$80+'W'
    FCC 'CL',$80+'S'
    FCC 'CONS',$80+'T'
    FCC 'DI',$80+'M'
    FCC 'NE',$80+'W'
    FCC 'LIS',$80+'T'
    FCC 'TRAC',$80+'E'
    FCC 'CONTINU',$80+'E'
    FCC 'STO',$80+'P'
    FCC 'GOT',$80+'O'
    FCC 'RU',$80+'N'
    FCC 'PAUS',$80+'E'
    FCC 'SWA',$80+'P'
    FCC 'DAT',$80+'A'
    FCC 'REA',$80+'D'
    FCC 'RESTOR',$80+'E'
    FCC 'LE',$80+'T'
    FCC 'RETUR',$80+'N'
    FCC 'EN',$80+'D'
    FCC 'PRO',$80+'C'
    FCC 'DEFIN',$80+'E'
    FCC 'EXI',$80+'T'
    FCC 'REPEA',$80+'T'
    FCC 'WHIL',$80+'E'
    FCC 'D',$80+'O'
    FCC 'ENDI',$80+'F'
    FCC 'ELS',$80+'E'
    FCC 'I',$80+'F'
    FCC 'NEX',$80+'T'
    FCC 'FO',$80+'R'
    FCC 'DRIV',$80+'E'
    FCC 'FORMA',$80+'T'
    FCC 'BACKU',$80+'P'
    FCC 'RENAM',$80+'E'
    FCC 'LOC',$80+'K'
    FCC 'MERG',$80+'E'
    FCC 'VERIF',$80+'Y'
    FCC 'SAV',$80+'E'
    FCC 'LOA',$80+'D'

;========================
;= SCREEN OUTPUT
;======================== 


printChar
    ;print character in A
    ;TODO tokens
    PSHS Y
    CMPA #$20                              ;compare with space
    BCC printCharNormal
    CMPA #$10                              ;compare with UDG  
    BCC printCharUdg
    CMPA #TRUE_VIDEO                       ;is it true video ?
    BNE printCharInverse
    CLRA                                   ;set true video
    BRA printCharInverse1
printCharInverse
    CMPA #INV_VIDEO                        ;is it inverse video ?
    BNE printCharControl
    LDA #$FF                               ;set inverse video
printCharInverse1    
    STA <PRINTTINV                         ;set true/inverse video in temporary flag
    RTS
printCharControl
    ;TODO: EOL, etc

printCharUdg
    LDX <UDGPTR                            ;look for udg pointer
    SUBA #$10                              ;first character in font is char #16
    BRA printCharNormal1
printCharNormal
    LDX <FONTPTR                           ;look for font pointer
    SUBA #$20                              ;first character in font is space 
printCharNormal1    
    LDB #$08
    MUL                                    ;multiply code by 8
    LEAX D,X                               ;add font base
    LDA <VIDEOMODE
    BEQ printCharM0                        ;print in mode 0
    DECA
    BEQ printCharM1                        ;print in mode 1
    DECA
    BEQ printCharM2                        ;print in mode 2
                                           ;print in mode 3
                                           
printCharM0
    LDY <PRINTLINE                         ;Y is now PRINTLINE*256+PRINTCOLU which is exactly the pointer into screen
    LDB #$08                               ;count 8 bytes of character pattern
printCharM0loop    
    LDA ,Y                                 ;read byte from screen
    ANDA <PRINTTOVER                       ;clear it or left it for over
    EORA ,X+                               ;do over (with empty byte when over is off)
    EORA <PRINTTINV                        ;do inverse (with empty byte when inverse is off)
    STA ,Y                                 ;and store byte into
    LEAY 32,Y                              ;move to next pixel line
    DECB                                 
    BNE printCharM0loop                    ;repeat
    LDA <PRINTLINE                         ;get print line to compute attr pointer
    LDB #$20                               
    MUL                                    ;multiply by 32
    ADDB <PRINTCOLU                        ;add column
    LDX #$1800                             ;start of attributes
    LEAX D,X                               ;complete attribute address
    LDA <PRINTTATTR                        ;read temporary attribute
    STA ,X                                 ;set character attribute
printCharNextPos    
    LDD <PRINTLINE                         ;read PRINTLINE and PRINTCOLU
    INCB                                   ;one position to the right
    CMPB <WINDOWRGHT                       ;is it out of window ?
    BEQ printCharNxtLine
    STB <PRINTCOLU                         ;next position is still in window
    PULS Y,PC                              ;shorter version of PULS Y ; RTS
    
printCharNxtLine
    LDB <WINDOWLEFT                        ;set column to left window column    
    STB <PRINTCOLU                           
    INCA
    CMPA <WINDOWBOT                        ;is next line out of window ?
    BEQ printCharWrap                      ;go check if are going to scroll or wrap
printCharNxtLine1    
    STA <PRINTLINE                         ;next position is still in window
    PULS Y,PC                              ;shorter version of PULS Y ; RTS

printCharWrap
    TST <PRINTWRAP                         ;test whether scroll or wrap     
    BNE printCharScroll
    LDB <WINDOWLEFT                        ;set column to left
    STB <PRINTCOLU
    LDA <WINDOWTOP                         ;for wrap simply continue from top left corner of window
    BRA printCharNxtLine1
printCharScroll   
    
    
;========================
;= SYS UTILS
;======================== 

byteCopy
    ;copy Y bytes from X to U
    PSHS U,X
    LDD ,S                ;read X
    CMPD 2,S              ;compare with U
    PULS U,X              ;discard values on stack (flags are unchanged)
    BCC byteCopyUp        ;U<=X, will copy incrementaly
    TFR Y,D               ;copy length to D
    LEAX D,X              ;update pointers for decrementing copy
    LEAU D,U
    BRA byteCopyDown
   
byteCopyUp
    ;copy Y bytes from X to U, increment 
    BSR byteCopyInit      ;main loop will copy 8 bytes at time, before we need to compute bytes left
    BEQ byteCopyLoopUp    ;if length is multiple of 8 the skip slow bytes
byteCopyWarmUp    
    LDA ,X+               ;copy lenght modulo 8 bytes slowly
    STA ,U+
    LEAY -1,Y
    BNE byteCopyWarmUp1   ;may be we are done already
    RTS
byteCopyWarmUp1    
    DECB                  ;slow bytes counter
    BNE byteCopyWarmUp
byteCopyLoopUp                             
    LDD ,X                ;main copy loop
    STD ,U                ;64 cycles per 8 bytes
    LDD 2,X
    STD 2,U
    LDD 4,X
    STD 4,U
    LDD 6,X
    STD 6,U
    LEAX 8,X
    LEAU 8,X
    LEAY -8,Y
    BNE byteCopyLoopUp           
    RTS
    
byteCopyDown
    ;copy Y bytes from X to U, decrement
    BSR byteCopyInit                     ;compute lenght modulo 8
    BEQ byteCopyLoopDown                 ;skip slow bytes if length is multiple of 8
byteCopyWarmDown
    LDA ,-X
    STA ,-U   
    LEAY -1,Y
    BNE byteCopyWarmDown1
    RTS
byteCopyWarmDown1    
    DECB
    BNE byteCopyWarmDown
byteCopyLoopDown
    LDD -2,X
    STD -2,U
    LDD -4,X
    STD -4,U
    LDD -6,X
    STD -6,U
    LDD -8,X
    STD -8,U
    LEAX -8,X
    LEAU -8,U
    LEAY -8,Y
    BNE byteCopyLoopDown
    RTS
    
byteCopyInit
    TFR Y,D                                ;B = Y modulo 8 
    ANDB #$07
    RTS
                             
;========================
;= IRQ HANDLERS
;======================== 

defaultIrqHandler
          LDD SYS_TIMER+2 		       	    ;increase four byte timer
          ADDD #$0001
          STD SYS_TIMER+2
          BCC defaultIrqHandler1
          LDX SYS_TIMER
          LEAX +1,X 
          STX SYS_TIMER
defaultIrqHandler1    
          JSR scanKeyboard 		            ;scan keyboard
defaultHandler
          RTI  			                    ;simply do nothing and leave

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

    FDB $FFFF			                    ;reserved
    FDB swi3Handler 		                    ;swi3 handler
    FDB swi2Handler		                    ;swi2 handler
    FDB firqHandler		                    ;firq handler
    FDB irqHandler		                    ;irq handler
    FDB swiHandler 		                    ;swi handler
    FDB resetHandler		                    ;nmi handler
    FDB resetHandler		                    ;reset handler
