.text

.global main
main:
PUSH {lr}
LDRSB r4, =1
MOV r0, r4
BL exit
LDRSB r0, =0
POP {pc}
.ltorg