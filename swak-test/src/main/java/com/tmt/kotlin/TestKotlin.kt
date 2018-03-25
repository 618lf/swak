package com.tmt.kotlin

fun main(args: Array<String>) {
    //sum(1, 2)
    //sum(1, 2, 3, 4, 5, 6)

    //var sunla: (Int, Int) -> Int = { x, y -> x + y }
    //println(sunla(10, 2))

    var a = 1
    a++

    /**
     * 我是来注释的
     */
    var s1 = "a is $a"

    a = 3
    var s2 = "${s1.replace("is", "was")}, but now is $a"

    println(s1)
    println(s2)

    var age = null
    val ages1 = age?.toInt()?:-1

    println(ages1)

    println(length(123))

    for(i in 1..10 step 2) {
        print(i)
    }

}

public fun sum(a: Int, b: Int) {
    println(a + b)
}

public fun sum(vararg v: Int) {
    for (vt in v) {
        println(vt)
    }
}

public fun length(obj:Any): Int? {
    if(obj is String) {
        // 做过类型判断以后，obj会被系统自动转换为String类型
        return obj.length
    }
    return null
}