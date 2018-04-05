package com.tmt.kotlin

public fun sum(a: Int, b: Int):Int {
    println(a + b)
    return 1
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