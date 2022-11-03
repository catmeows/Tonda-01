
printMessage
  ;will print x-th message from a table
  ;A holds message number, X holds pointer to message table
  ;this routine is used to print tokens and error messages

  TSTA                        ;initial test to see if message counter is zero already
printMsg3  
  BEQ printMsg1               ;is message counter zero? then we found message to print  
printMsg2  
  LDB ,X+                     ;find end of current message
  BPL printMsg2
  DECA                        ;decrease message counter
  BRA printMsg3               ;and repeat
printMsg1
  LDA ,X+                     ;get one character of message
  BMI printMsg4               ;last character would have bit 7 set
  BSR printAny                ;print the character
  BRA printMsg1               ;and continue to next character
printMsg4
  ANDA #$7F                   ;strip last character of highest bit
                              ;and exit via printAny

printAny
  ;will print character in A
  
  PSHS X,Y,U                  ;store registers used during printing
  CMPA #$20                   ;if code is less than $20 then it is a control code
  BCS printCtrl               ;go to print control code
  CMPA #$80                   ;if code is less than $80 then it is a regular character
  BCS printChar               ;print characters $20..$7F
  CMPA #$9A                   ;if code is less than $9A then it is a user defined graphics
  BCS printUdg                ;print characters $80..$9A
  LDX #TOKENS                 ;characters $9B..$FF are tokens
  SUBA #$9A
  JSR printMessage            ;print whole message
  PULS X,Y,U                  ;restore saved registers
  RTS                         ;and exit
printCtrl
  ;TODO

printUdg
  LDX UDGPTR                  ;will print user defined graphics, set pointer to graphics pattern area
  SUBA #$80                   ;first udg has code $80
  BRA printChar1              ;and continue to print character
printChar
  LDX FONTPTR                 ;set pointer to character font
  SUBA #$20                   ;first printable character is 'space'
printChar1
  LDB #$08                    ;character pattern is 8 bytes long
  MUL
  LEAX D, X                   ;now we have pointer to pattern
  LDA <PRTPOS_LINE            ;get current print line
  TST <GMODE                  ;check current graphics mode
  


  
  
  
