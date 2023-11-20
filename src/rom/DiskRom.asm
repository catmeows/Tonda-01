
; FORMAT DISK
;
dskFormat


; LOAD TO MEMORY
;
dskLoadToMemory


; SAVE FROM MEMORY
;
dskSaveFromMemory


; VERIFY MEMORY
dskVerify


; LOAD CATALOGUE
;
dskLoadCatalogue


; GOTO TRACK 0
; while (!isTrack0) {
;     stepDown
;     }
dskGotoTrackZero
    LDA #$81                                   ;tries
dskGotoTrackZeroMore
    DECA
    BEQ dskGotoTrackZeroError
    JSR dskIsTrackZero
    BEQ dskGotoTrackZeroDone
    JSR dskStepDown
    BRA dskGotoTrackZeroMore
dskGotoTrackZeroDone
    CLRA
    STA dskVarCurrentTrack
    RTS
dskGotoTrackZeroError
    LDA #DSKERR01                              ;after 80 steps down still not on track zero, report error
    JMP dskError                               ;report "Disk failure"

; GOTO TRACK X
; register A = destination track
;
; let steps = destinationTrack - currentTrack
; let absSteps = abs (steps)
; if (sgn(steps) == +1) {
;     for index = 1 to absSteps {
;         stepUp
;     }
; } else if (sgn(steps) == -1) {
;     for index = 1 to absSteps {
;         stepDown
;     }
; }
; verifyTrack(destinationTrack)
dskGotoTrackX
    STA dskVarDestinationTrack
    LDA #$05
    STA dskVarTryOuts
dskGotoTrackRetry
    LDA dskVarDestinationTrack
    SUBA dskVarCurrentTrack
    BEQ dskGotoTrackXNoStep
    BMI dskGotoTrackX
dskGotoTrackXUp
    JSR dskStepUp
    DECA
    BNE dskGotoTrackXUp
    BRA dskGotoTrackXNoStep
dskGotoTrackX1
    NEGA
dskGotoTrackXDown
    JSR dskStepDown
    DECA
    BNE dskGotoTrackXDown
dskGotoTrackXNoStep
    LDA dskVarDestinationTrack
    JSR dskVerifyTrackId
    BNE dskGotoTrackXFail
    RTS
dskGotoTrackXFail
    DEC dskVarTryOuts
    BEQ dskGotoTrackXError
    JSR dskGotoTrackZero
    BRA dskGotoTrackRetry
dskGotoTrackXError
    LDA #DSKERR02                              ;after 4 retries we are not on target track
    JMP dskError                               ;report "Can not read disk"

; VERIFY TRACK
;
dskVerifyTrackId
    JSR dskReadHeader
    LDA dskVarCurrentTrack
    CMPA dskVarDestinationTrack
    RTS

; READ HEADER
; read header and write header id to dskVarCurrentTrack
;
dskReadHeader


; READ TRACK
;
;
dskReadTrack


; VERIFY TRACK
;
;
dskVerifyTrack


; WRITE HEADER
;
;
dskWriteHeader

; WRITE TRACK
;
;
dskWriteTrack


; STEP DOWN
;
;
dskStepDown


; STEP UP
;
;
dskStepUp








