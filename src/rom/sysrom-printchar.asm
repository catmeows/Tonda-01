


    LDB #$08                      ;8 bytes of character
    PSHS Y
printCharMode1c    
    LDA ,X+
    TST <SYS_INVERSE              ;inverse bits?
    BPL printCharMode1a
    COMA
printCharMode1a
    STA ,Y
    LEAY 32,Y
    TST <SYS_CSIZE                ;double heigth?
    BPL printCharMode1b
    STA ,Y
    LEAY 32,Y
printCharMode1b
    DECB
    BNE printCharMode1c
    PULS Y
    JSR yPix2yAttr
    LDA <SYS_TEMPATTR
    STA ,Y
    TST <SYS_CSIZE
    BPL printCharMode1d
    STA 32,Y
printCharMode1d
    RTS
    
getCharPattern
    CMPA #$20
    BCC getCharPattern2
    LDX SYS_UDG
getCharPattern2
    LDX SYS_FONT
getCharPattern1
    LDB #$08
    MUL
    LEAX D,X
    RTS

yPix2yAttr
    PSHS A,B
    TFR Y,D
    ANDB #$1F
    ASLB
    ASLB
    ASLB
    ANDA #$1F
    ASRA
    RORB
    ASRA
    RORB
    ASRA
    RORB
    ADDA #$18
    TFR D,Y
    PULS A,B
    RTS
    
    
    
    
