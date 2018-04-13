package com.tmt.kotlin

/**
 *  扩展函数
 */
class User(val id: Int, val name: String ="1", val address: String = "1") {

    fun out() :String {
        return "$name .. $address"
    }

}
fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalAccessError("Can't save user $id: empty $fieldName")
        }
    }
    validate(name, "Name")
    validate(address, "address")
}

fun saveUser(user: User) {
    if (user.name.isEmpty()) {
        throw IllegalAccessError("Can't save user ${user.id}: empty Name")
    }

    if(user.address.isEmpty()) {
        throw IllegalAccessError("Can't save user ${user.id}: empty Address")
    }
    println(user.out())
}

fun saveUser2(user: User) {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalAccessError("Can't save user ${user.id}: empty $fieldName")
        }
    }
    validate(user.name, "Name")
    validate(user.address, "address")
    println(user.out())
}

fun saveUser3(user: User) {
    user.validateBeforeSave()
    println(user.out())
}


fun main(args: Array<String>) {
    saveUser(User(1, "李锋","深圳市南头街道前海路海岸时代大厦"))
    saveUser2(User(1, "韩倩","深圳市南头街道前海路海岸时代大厦"))
    saveUser3(User(1, "小豆","深圳市南头街道前海路海岸时代大厦"))
}
