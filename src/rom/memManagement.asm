; MEMORY MANAGEMENT
; BASIC uses 
;
; Program area is storage for tokenized BASIC lines. Whenever a line is added, removed or changes, program area size changes.
; It is expected that program area does not change during execution of program.
;
; Numeric array area is storage for numeric arrays. Whenever new numeric array is created using DIM command, space for the array
; is created in numeric array area. Commands CLEAR, NEW erase all variables and therefore colapse numeric array area.
;
; Numeric variables area is storage for numeric variables. When a new numeric variable is created, space for variable is created
; for the variable. Commands CLEAR, NEW erase all variables and therefore colapse numeric variables area.
;
; Labels area is storage for labels in program. Whenever commands GOTO, GOSUB, FN, RESTORE are looking for a label, they will check
; label area to quickly get address of label. If label is unknown, program is searched and if label is found, it is inserted into 
; label area. All labels are discarded and Label area is collapsed when BASIC leaves execution mode and enters edit mode.
;
; String variable area is storage for both of single strings and string arrays. When a string variable is created or changes its length,
; size of string variable area is adjusted. Commands CLEAR, NEW erase all variables and therefore colapses numeric array area.
;
; Loop area is storage for active loops FOR and WHILE and return pointers for GOSUB and FN. This area is collapsed by CLEAR, NEW, RUN.
;
; Edit line area is storage for edited line. It is used by program editor and by INPUT command. For most time, this area is collapsed.
;
; Workspace is temporary area used during evaluating expressions. It is also used as storage for tokenized program line.
;
; 6809's U stack is used as calculator stack.
;
; 6809's S stack is used as machine code stack.
;
; PROG start of program area
; NUMARRAYS start of numeric array area
; NUMVARS start of numeric variables area
; LABELS start of label area
; STRINGS start of string variable area 
; LOOPS start of loop area
; ELINE start of edit line area
; WRKSPC is start of workspace area
; FREEMEM is start of unused memory available to BASIC
; RAMTOP is start of memory area that is not available to BASIC

testRoom
;test if there is enough memory to reserve more space for BASIC
;count of bytes required in system variable <RESERVE
  TFR U,D                     ;take bottom of U stack
  SUBD <FREEMEM               ;compute free space between workspace and calculator stack
  BCS testRoomFail            ;we are out of space already, probably because of complex expression
  SUBD #$60                   ;add 96 more bytes to give reasonable space for expression evaluation
  BCS testRoomFail
  CMPD <RESERVE               ;now try if there is enough space
  BCS testRoomFail
  RTS
testRoomFail
  LDA #ERR_OUTMEM
  JMP reportError


updatePointers
  ;update all pointers from NUMARRAYS, NUMVARS, LABELS, STRINGS, LOOPS, ELINE, WRKSPC, FREEMEM
  ;that need to be updated
  ;since place of insertion is always less than FREEMEM, at least one pointer is found
  ; <INSERTPTR place where to insert space
  ; <RESERVE size of inserted space
  LDX NUMARRAYS
updatePointers1
  LDD ,X++                    ;take current value of pointer
  CMPD <INSERTPTR             ;compare with ptr to place where insert will happen
  BCC updatePointers1         ;if value of pointer is below place of change then go to next ptr
  LEAX -2,X                   ;roll two bytes back, we found first pointer to update
updatePointers2 
  LDD ,X                      ;read current value of pointer 
  ADDD <RESERVE               ;update pointer by size of change
  STD ,X++                    ;store updated pointer
  CMPX #FREEMEM+2             ;was it last pointer ?
  BNE updatePointers2
  RTS
  
makeOneByteRoom
  ;make space for one more byte
  LDD #$0001                  ;set <RESERVE to 1 and continue to makeSpace
  STD <RESERVE
makeSpace
  ;make space for bytes given by value in <RESERVE
  ;at the location given by <INSERTPTR
  BSR testRoom                ;first check if space is avalable, testRoom will throw OUT OF MEMORY error if there is not enough space 
  LDD <FREEMEM
  LDX <FREEMEM 
  SUBD <INSERTPTR             ;compute length of block to move
  PSHS D,X                    ;store old FREEMEM and length block on stack
  BSR updatePointers          ;update pointers
  PULS D,X                    ;restore old FREEMEM as source and length of block to move 
  LDY <FREEMEM                ;set new FREEMEM as destination
  JSR copyBackward            ;and copy everything up
  RTS
  
reclaimRoom
  ;reclaim space for bytes given by value in <RESERVE
  ;at the location given by <INSERTPTR
  LDD <RESERVE                ;take number of bytes to be reclaimed
  PSHS D                      ;save it for later
  COMA                        ;to update pointers, negate length of reclaimed area
  COMB
  ADDD #$0001
  STD <RESERVE
  BSR updatePointers          ;update system pointers
  PULS D                      ;restore length
  LDY <INSERTPTR              ;copy destination is first reclaimed byte
  LEAX D,Y                    ;copy source is first reclaimed byte + length of reclaimed area
                              ;then continue directly to copyForward
  
copyForward
  ;copy from bottom to top
  ;Y is destination
  ;X is source
  ;D is length
  CMPD #$0000                 ;leave if length is 0   
  BEQ copyBackward2
  PSHS U
  TFR Y,U
  TFR D,Y
  ANDB #$07
  BEQ copyForward4
copyForward3
  LDA ,X+
  STA ,U+
  LEAY -1,Y
  BEQ copyBackward5
  DECB
  BNE copyForward3
copyForward1
  LDD ,X
  STD ,U
  LDD +2,X
  STD +2,U
  LDD +4,X
  STD +4,U
  LDD +6,X
  STD +6,U
  LEAX +8,X
  LEAU +8,U
  LEAU -8,Y
  BNE copyForward1
  BRA copyBackward5 
  
copyBackward
  ;copy from top to bottom
  ;Y is destination
  ;X is source
  ;D is length
  CMPD #$0000                 ;leave if length is 0
  BEQ copyBackward2
  PSHS U                      ;save U
  TFR Y,U                     ;U is destination
  TFR D,Y                     ;Y is counter, because LEAY sets ZERO flag
  ANDB #$07                   ;is counter already aligned to 8 ?
  BEQ copyBackward4           ;then copy by 8 bytes
copyBackward3
  LDA ,X                      ;copy single byte 
  STA ,U
  LEAX -1,X                   ;source--
  LEAU -1,U                   ;destination--
  LEAY -1,Y                   ;counter--
  BEQ copyBackward5           ;check end of copy
  DECB                        ;loop until counter is divisible by 8
  BNE copyBackward3
copyBackward4
  LEAX -1,X                   ;adjust source for copy through D
  LEAU -1,U                   ;adjust destination for copy through D
copyBackward1   
  LDD ,X                      ;5+0  copy 8 bytes at once 
  STD ,U                      ;5+0  
  LDD -2,X                    ;5+1  8B / 64cycles -> 1B / 8cycles 
  STD -2,U                    ;5+1
  LDD -4,X                    ;5+1
  STD -4,U                    ;5+1
  LDD -6,X                    ;5+1
  STD -6,U                    ;5+1
  LEAX -8,X                   ;4+1
  LEAU -8,U                   ;4+1
  LEAY -8,Y                   ;4+1
  BNE copyBackward1           ;3
copyBackward5  
  PULS U
copyBackward2
  RTS

;memory banks
;since it is not guaranteed that S stack points above paged area, we must move S stack above banked area
  
pageBank1
  ;page in bank 1
  STD <TEMPE                  ;store D reg to temporary location
  LDD ,S                      ;load return address
  STS OLDSTACK               ;store S reg to temporary location
  LDS #TMPSTACK
  STD ,S                      ;store return address on new stack
  CLRA
  STA $BF80                   ;select Marta register R0
  LDA $BF80                   ;read Marta register R0
  ANDA #$0F                   ;mask bits
  ORA #$10                    ;set paged bank 1
  STA $BF80                   ;write Marta R0
  LDD <TEMPE                  ;restore D reg
  RTS
  
pageBank0
  ;page in bank 0 
  STD <TEMPE                  ;store D reg to temporary location
  CLRA
  STA $BF80                   ;select Marta register R0
  LDA $BF80                   ;read Marta register R0
  ANDA #$0F                   ;mask bits
  STA $BF80                   ;write Marta R0, page bank 0
  LDD ,S                      ;load return address
  LDS <OLDSTACK               ;restore original stack
  STD ,S                      ;replace old return address
  RTS
  
  
