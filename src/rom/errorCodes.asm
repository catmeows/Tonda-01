; This is list of error codes used by BASIC
ERR_OK            EQU $00
ERR_OUTMEM        EQU $01
ERR_NXTWFOR       EQU $02

errorMsgs
  FCS "OK"                            ;0
  FCS "Out of memory"                 ;1
  FCS "NEXT without FOR"              ;2
  FCS "Variable not found"            ;3
  FCS "Subscript wrong"               ;4
  FCS "Out of screen"                 ;5
  FCS "Number too big"                ;6
  FCS "RETURN without GOSUB"          ;7
  FCS "STOP statement"                ;8
  FCS "Invalid argument"              ;9
  FCS "Integer out of range"          ;A
  FCS "Nonsence in BASIC"             ;B
  FCS "BREAK - CONT repeats"          ;C
  FCS "Out of data"                   ;D
  FCS "Invalid filename"              ;E
  FCS "FOR without NEXT"              ;F
  FCS "WHILE without DO"              ;G
  FCS "WHILE without REPEAT"          ;H
  FCS "EXIT outside loop"             ;I
  FCS "Invalid device"                ;J
  FCS "Invalid colour"                ;K
  FCS "BREAK into program"            ;L
  FCS "RAMTOP no good"                ;M
  FCS "Statement lost"                ;N
  FCS "Label not found"               ;O
  FCS " "                             ;P
  FCS " "                             ;Q
  FCS "Tape loading error"            ;R
  FCS " "                             ;S
  FCS "Device not ready"              ;T
  FCS "File is read only"             ;U
  FCS "Disk is write protected"       ;V
  FCS "File already exists"           ;W
  FCS "File not found"                ;X
  FCS "Disk read error"               ;Y
  FCS "Disk is full"                  ;Z

otherMsgs
  "Start tape, then press any key"
  "TONDA BASIC 2022"
  
