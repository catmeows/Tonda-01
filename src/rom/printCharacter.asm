
printMessage
  ;will print x-th message from a table
  ;A holds message number, X holds pointer to message table
  ;this routine is used to print tokens and error messages
  ;
  ;mode 0 - 
  ;mode 1 -
  ;mode 2 -

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
  LDA <PRTPOS_LINE            ;get current print line into A
  LDB #$A0                    ;320/2=160
  TST <GMODE                  ;check current graphics mode
  BEQ printPosM0              ;GMODE==0 it is mode 0
  BPL printPosM1              ;GMODE==1 it is mode 1, GMODE==128 it is mode 2
  
  ;compute position for mode 2
  ASLA
  ASLA                        ;multiply line by 4
  MUL                         ;line*4*160=line*640
  STD <TEMP0                  ;store start of line to TEMP0, TEMP1
  LDB <PRTPOS_COL             ;get print column
  ASLB
  ASLB                        ;column*4, since column is in range 0..19, it will never overflow
  CLRA                        ;clear MSB of D
  ADDD <TEMP0                 ;line*640+column*4, we have ptr to screen memory now
  BRA printPosDone            ;continue to common part of print routine
  
printPosM0
  ;compute position for mode 0
  ASLA                        ;multiply line by 2
  MUL                         ;line*2*160=line*320
  ADDB <PRTPOS_COL            ;add column
  ADCA #$00                   ;update MSB of ptr
  BRA printPosDone
  
printPosM1
  ;compute position for mode 1
  ASLA
  ASLA                        ;multiply line by 4
  MUL                         ;line*4*160=line*640
  ADDB <PRTPOS_COL
  ADCA #$00
  ADDB <PRTPOS_COL
  ADCA #$00                   ;line*640+column*2
printPosDone
  TFR D, Y                    ;store pointer to screen into Y
  
  ;usage of temporary variables
  ;variable mode0        mode1        mode2
  ;TEMP0    charByte                  charByte
  ;TEMP1                              bitsLoop 
  ;TEMP2
  ;TEMP3    byteLoop     byteLoop     byteLoop
  
  LDA #$08                    ;counter for pixel lines in character
  STA <TEMP3                  
printLoop
  LDA ,X+                     ;take one byte of character
  EORA <INVERSE               ;invert byte when INVERSE is $FF
  TST <GMODE                  ;test graphics mode
  BEQ printByteM0             ;GMODE==0 it is mode 0
  BPL printByteM1
  
  ;print byte for mode 2
  LDU #printTabM2
  STA <TEMP0
  LDA #$04
  STA <TEMP1
printByteM2loop
  LDA <TEMP0
  ROLA
  ROLA
  STA <TEMP0
  ANDA #$03
  
  
  
printByteM1
  ;print byte for mode 1
  STB <TEMP2                  ;store
  LDU #printTabM1
  STA <TEMP0                  ;temporary store character byte
  LSRA                        ;isolate higher nibble
  LSRA                        ;and shift it right
  LSRA
  LSRA
  JSR printByteM1sub          ;print left 4px of character byte 
  LDA <TEMP0
  ANDA #$0F                   ;print right 4px of character byte
  JSR printByteM1sub
  LEAY +79, Y                 ;move one pixel line down
  LDB <TEMP2
  DECB
  BNE printLoop
  BRA
    
printByteM1sub   
  LDB A, U                    ;get mask for the nibble 
  ANDB <MXINK                 ;and mask with ink
  STB <TEMP1                  ;store all ink bits
  LDB A,U                     ;read mask again
  COMB                        ;swap bits to create paper mask
  TST <OVER                   ;depending on OVER flag, use mask either against screen memory or against paper value
  BEQ printByteM1over0
  ANDA ,Y
  BRA printByteM1end
printByteM1over0
  ANDA <MXPAPER
printByteM1end  
  ORA <TEMP1
  STA ,Y+
  RTS 

printByteM0
  ;print byte for mode 0
  TST <OVER                   ;test over mode
  BEQ printByteM0over0       
  ORA ,Y                      ;over is on, combine character byte with byte on screen
printByteM0over0  
  STA ,Y                      ;store character byte
  LEAY +40,Y                  ;continue to next pixel line
  DECB                        ;decrease loop counter
  BNE printLoop               ;repeat until done
  


printTabM1
  FCB $00, $03, $0C, $0F, $30, $33, $3C, $3F
  FCB $C0, $C3, $CC, $CF, $F0, $F3, $FC, $FF
  
printTabM2
  FCB $
  
  