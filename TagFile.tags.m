           % processing: t1 := 10
           addi r1,r0,10
           sw t1(r0),r1
           % processing function definition: 
f1                   sw f1link(r0),r15
           % processing: t2 := 1
           addi r1,r0,1
           sw t2(r0),r1
           lw r1,t2(r0)
           sw i(r0) , r1
goFor1
           % processing: t3 := 10
           addi r2,r0,10
           sw t3(r0),r2
           % processing: t4 := i < t3
           lw r3,i(r0)
           lw r4,t3(r0)
           clt r2,r3,r4
           sw t4(r0),r2
           lw r1,t4(r0)
           bz r1,EndgoFor1
           % processing: put(var)
           lw r2,var(r0)
           % put value on stack
           sw -8(r14),r2
           % link buffer to stack
           addi r2,r0, buf
           sw -12(r14),r2
           % convert int to string for output
           jl r15, intstr
           sw -8(r14),r13
           % output to console
           jl r15, putstr
           % processing: t5 := 1
           addi r2,r0,1
           sw t5(r0),r2
           % processing: t6 := i + t5
           lw r3,i(r0)
           lw r5,t5(r0)
           add r2,r3,r5
           sw t6(r0),r2
           % processing: i := t6
           lw r2,t6(r0)
           sw i(r0),r2
           j goFor1
EndgoFor1
           % processing: t7 := 10
           addi r2,r0,10
           sw t7(r0),r2
           % processing: return(t7)
           lw r1,t7(r0)
           sw f1return(r0),r1
           lw r15,f1link(r0)
           jr r15
           entry
           addi r14,r0,topaddr
           % processing: t8 := 5
           addi r1,r0,5
           sw t8(r0),r1
           % processing: t9 := 10
           addi r1,r0,10
           sw t9(r0),r1
           % processing: t10 := 10
           addi r1,r0,10
           sw t10(r0),r1
           % processing: t11 := 10
           addi r1,r0,10
           sw t11(r0),r1
           % processing: c1.i1 := t11
           lw r1,t11(r0)
           sw c1.i1(r0),r1
           % processing: put(c1: .i1: )
           lw r1,c1: .i1: (r0)
           % put value on stack
           sw -8(r14),r1
           % link buffer to stack
           addi r1,r0, buf
           sw -12(r14),r1
           % convert int to string for output
           jl r15, intstr
           sw -8(r14),r13
           % output to console
           jl r15, putstr
           % processing: get(b)
           addi r1,r0, buf
           sw -8(r14),r1
           jl r15, getstr
           jl r15, strint
           sw b(r0),r13
           % processing: t12 := 1
           addi r1,r0,1
           sw t12(r0),r1
           % processing: t13 := 1
           addi r1,r0,1
           sw t13(r0),r1
           % processing: t14 := 1
           addi r1,r0,1
           sw t14(r0),r1
           % processing: get(a1[1,1,1])
           muli r1,r0,1
           lw r2,t12(r0)
           mul r1,r1,r2
           lw r2,t13(r0)
           mul r1,r1,r2
           lw r2,t14(r0)
           mul r1,r1,r2
           muli r1,r1,4
           addi r1,r0, buf
           sw -8(r14),r1
           jl r15, getstr
           jl r15, strint
           sw a1(r0),r13
           % processing: t15 := 1
           addi r1,r0,1
           sw t15(r0),r1
           % processing: t16 := 1
           addi r1,r0,1
           sw t16(r0),r1
           % processing: t17 := 1
           addi r1,r0,1
           sw t17(r0),r1
           % processing: t18 := 1
           addi r1,r0,1
           sw t18(r0),r1
           % processing: t19 := 1
           addi r1,r0,1
           sw t19(r0),r1
           % processing: t20 := 1
           addi r1,r0,1
           sw t20(r0),r1
           % processing: t21 := b * a1[1,1,1]
           lw r5,b(r0)
           muli r2,r0,1
           lw r6,t18(r0)
           mul r2,r2,r6
           lw r6,t19(r0)
           mul r2,r2,r6
           lw r6,t20(r0)
           mul r2,r2,r6
           muli r2,r2,4
           lw r3,a1(r2)
           mul r1,r5,r3
           sw t21(r0),r1
           % processing: t22 := a1[1,1,1] + t21
           muli r3,r0,1
           lw r6,t15(r0)
           mul r3,r3,r6
           lw r6,t16(r0)
           mul r3,r3,r6
           lw r6,t17(r0)
           mul r3,r3,r6
           muli r3,r3,4
           lw r5,a1(r3)
           lw r2,t21(r0)
           add r1,r5,r2
           sw t22(r0),r1
           % processing: result := t22
           lw r1,t22(r0)
           sw result(r0),r1
           % processing: t23 := 10
           addi r1,r0,10
           sw t23(r0),r1
           % processing: t24 := result > t23
           lw r2,result(r0)
           lw r5,t23(r0)
           cgt r1,r2,r5
           sw t24(r0),r1
           % processing: result := t24
           lw r1,t24(r0)
           sw result(r0),r1
           % processing: t25 := 1
           addi r1,r0,1
           sw t25(r0),r1
           % processing: function call to f1link(r0) 
           lw r1,t25(r0)
           sw par1(r0),r1
           jl r15,f1
           lw r1,f1return(r0)
           sw t26(r0),r1
           % processing: result1 := f1return
           lw r1,f1return(r0)
           sw result1(r0),r1
           % processing: put(result1)
           lw r1,result1(r0)
           % put value on stack
           sw -8(r14),r1
           % link buffer to stack
           addi r1,r0, buf
           sw -12(r14),r1
           % convert int to string for output
           jl r15, intstr
           sw -8(r14),r13
           % output to console
           jl r15, putstr
           hlt

           % space for variable i1
i1         res 4
           % space for constant 10
t1         res 4
a          res 240
f1link     res 4
f1return       res 4
           % space for variable par1
par1       res 4
           % space for variable s
s          res 4
           % space for variable var
var        res 4
           % space for constant 1
t2         res 4
           % space for constant 10
t3         res 4
           % space for i<t3
t4         res 4
           % space for constant 1
t5         res 4
           % space for i + t5
t6         res 4
           % space for variable i
i          res 4
           % space for constant 10
t7         res 4
c1         res 24
c2         res 268
           % space for variable f
f          res 4
           % space for variable b
b          res 4
           % space for variable result
result     res 4
           % space for variable result1
result1    res 4
           % space for constant 5
t8         res 4
           % space for constant 10
t9         res 4
           % space for constant 10
t10        res 4
           % space for variable a1
a1         res 2000
           % space for constant 10
t11        res 4
           % space for constant 1
t12        res 4
           % space for constant 1
t13        res 4
           % space for constant 1
t14        res 4
           % space for constant 1
t15        res 4
           % space for constant 1
t16        res 4
           % space for constant 1
t17        res 4
           % space for constant 1
t18        res 4
           % space for constant 1
t19        res 4
           % space for constant 1
t20        res 4
           % space for b * a1[1,1,1]
t21        res 4
           % space for a1[1,1,1] + t21
t22        res 4
           % space for constant 10
t23        res 4
           % space for result>t23
t24        res 4
           % space for constant 1
t25        res 4
           % space for function call expression factor
t26        res 4
           % buffer space used for console output
buf        res 20

