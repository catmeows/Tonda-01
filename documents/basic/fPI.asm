func_PI
  ldy #func_PI2
  jmp store_num
func_PI2
  FCB $78, $12, $B9, $B0, $A1 ; 314 159 265 * 10^-8
