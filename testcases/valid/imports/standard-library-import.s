.data

msg_index_too_large:
	.word 45
	.ascii "ArrayIndexOutOfBoundsError: index too large\n\0"
msg_print_int:
	.word 3
	.ascii "%d\0"
msg_print_ln:
	.word 1
	.ascii "\0"
msg_throw_overflow_error:
	.word 82
	.ascii "OverflowError: the result is too small/large to store in a 4-byte signed-integer.\n"
msg_negative_index:
	.word 44
	.ascii "ArrayIndexOutOfBoundsError: negative index\n\0"
msg_print_string:
	.word 5
	.ascii "%.*s\0"

.text

.global main
f_min_p_array_:
	PUSH {lr}
	SUB sp, sp, #16
	LDR r4, [sp, #20]
	STR r4, [sp, #12]
	ADD r4, sp, #12
	LDR r5, =0
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #8]
	LDR r4, =1
	STR r4, [sp, #4]
	LDR r4, [sp, #12]
	LDR r4, [r4]
	STR r4, [sp]
	B L0
L1:
	ADD r4, sp, #12
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	LDR r5, [sp, #8]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #0
	BEQ L2
	ADD r4, sp, #12
	LDR r5, [sp, #4]
	LDR r4, [r4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #8]
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
	B L3
L2:
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
L3:
L0:
	LDR r4, [sp, #4]
	LDR r5, [sp]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ L1
	LDR r4, [sp, #8]
	MOV r0, r4
	ADD sp, sp, #16
	POP {pc}
	ADD sp, sp, #16
	POP {pc}
	.ltorg
main:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r0, =16
	BL malloc
	MOV r4, r0
	LDR r5, =2
	STR r5, [r4, #4]
	LDR r5, =1
	STR r5, [r4, #8]
	LDR r5, =3
	STR r5, [r4, #12]
	LDR r5, =3
	STR r5, [r4]
	STR r4, [sp, #4]
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_min_p_array_
	ADD sp, sp, #4
	MOV r4, r0
	STR r4, [sp]
	LDR r4, [sp]
	MOV r0, r4
	BL p_print_int
	BL p_print_ln
	ADD sp, sp, #8
	LDR r0, =0
	POP {pc}
	.ltorg
p_throw_runtime_error:
	BL p_print_string
	MOV r0, #-1
	BL exit
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
p_print_int:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_print_int
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_check_array_bounds:
	PUSH {lr}
	CMP r0, #0
	LDRLT r0, =msg_negative_index
	BLLT p_throw_runtime_error
	LDR r1, [r1]
	CMP r0, r1
	LDRCS r0, =msg_index_too_large
	BLCS p_throw_runtime_error
	POP {pc}
p_throw_overflow_error:
	LDR r0, =msg_throw_overflow_error
	BL p_throw_runtime_error
p_print_ln:
	PUSH {lr}
	LDR r0, =msg_print_ln
	ADD r0, r0, #4
	BL puts
	MOV r0, #0
	BL fflush
	POP {pc}
