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
; for the variable. Commands CLEAR, NEW erase all variables and therefore colapse numeric array area.
;
; Labels are is storage for labels in program. Whenever commands GOTO, GOSUB, FN, RESTORE are looking for a label, they will check
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
  
updatePtrNumArrays  
  LDD <NUMARRAYS              ;update NUMARRAYS
  ADDD <RESERVE
  STD <NUMARRAYS
updatePtrNumVars  
  LDD <NUMVARS                ;update NUMVARS
  ADDD <RESERVE
  STD <NUMVARS
updatePtrLabels  
  LDD <LABELS                 ;update LABELS
  ADDD <RESERVE
  STD <LABELS
updatePtrStrings  
  LDD <STRINGS                ;update STRINGS
  ADDD <RESERVE
  STD <STRINGS
updatePtrLoops
  LDD <LOOPS                  ;update LOOPS
  ADDD <RESERVE
  STD <LOOPS
updatePtrELine  
  LDD <ELINE                  ;update ELINE
  ADDD <RESERVE
  STD <ELINE
updatePtrWrkSpc  
  LDD <WRKSPC                 ;update WRKSPC
  ADDD <RESERVE
  STD <WRKSPC
updatePtrFreeMem
  LDD <FREEMEM                ;update FREEMEM
  ADDD <RESERVE
  STD <FREEMEM
  RTS

