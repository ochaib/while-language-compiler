.data

msg_print_ln:
	.word 1
	.ascii "\0"
msg_0:
	.word 11
	.ascii "hello world"
msg_print_string:
	.word 5
	.ascii "%.*s\0"

.text

.global main
f_helloworld_p_:
	PUSH {lr}
	LDR r4, =msg_0
	MOV r0, r4
	POP {pc}
	POP {pc}
	.ltorg
main:
	PUSH {lr}
	SUB sp, sp, #4
	BL f_helloworld_p_
	ADD sp, sp, #0
	MOV r4, r0
	STR r4, [sp]
	LDR r4, [sp]
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	ADD sp, sp, #4
	LDR r0, =0
	POP {pc}
	.ltorg
p_print_string:
	PUSH {lr}
	LDR r1, [r0]
	ADD r2, r0, #4
	LDR r0, =msg_print_string
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_print_ln:
	PUSH {lr}
	LDR r0, =msg_print_ln
	ADD r0, r0, #4
	BL puts
	MOV r0, #0
	BL fflush
	POP {pc}
