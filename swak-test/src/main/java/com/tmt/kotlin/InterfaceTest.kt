package com.tmt.kotlin

/**
 * 定义一个简单的接口
 */
interface Clickable {
    fun click()
    fun showOff() = println("I am clickable!")
}

/**
 * 另一个有默认实现的接口 showOff
 */
interface Focusable {
    fun setFocus(b: Boolean) = println("I ${if (b) "got" else "lost"} focus.")
    fun showOff() = println("I am focusable!")
}

/**
 * 实现接口，并实现了方法 alt + insert
 */
open class Button : Clickable, Focusable {
    override fun click() {
        println("我点击了")
    }

    /**
     * 强制要实现此方法
     */
    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}

/**
 * 继承一个类，指定父类的默认构造方法
 */
open class MyButton : Button() {
    override fun click() {
        println("我实现了点击  -- > 我点击了")
    }
}

/**
 * 入口函数
 */
fun main(args: Array<String>) {
    Button().click()
    MyButton().click()
    MyButton().showOff()
}