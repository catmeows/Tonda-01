ERR_OK     EQU $00
ERR_BREAK  EQU $01
ERR_SYNTAX EQU $02
ERR_STOP   EQU $03

EOL        EQU $0d
LABEL      EQU '@'
REMARK     EQU $27

;========================
;= COMMAND RUNTIME
;========================

commStop
    LDA #ERR_STOP
    JMP error

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
    LDX #CommandTable
    JMP A,X

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
