package client.utils

import java.util.*

object InputUtil {

    fun getString(request: String): String {
        print("\n$request : ")
        return readLine() ?: ""
    }

    fun getInt(request: String): Int {
        while (true) {
            try {
                return when (val str = getString(request)) {
                    "--q" -> -1
                    else -> str.toInt()
                }
            } catch (e: Exception) {
                println("Please Enter Valid Input")
            }
        }
    }

    fun getLong(request: String): Long {
        while (true) {
            try {
                return when (val str = getString(request)) {
                    "--q" -> -1L
                    else -> str.toLong()
                }
            } catch (e: Exception) {
                println("Please Enter Valid Input")
            }
        }
    }

    fun getPhoneNumber(): Long {
        val input = getString("Please Enter Your 10 digit Number")
        if (input == "--q") return -1L
        val numberRegex = "^[0-9]{10}\$".toRegex()
        return if (numberRegex.matches(input)) input.toLong() else {
            println("Please Enter Valid 10 Digit Mobile Number")
            getPhoneNumber()
        }
    }

    fun getPassword(): String {
        val input = getString("Please Enter Your Password")
        if (input == "--q") return input
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex()
        return if (passwordRegex.matches(input)) input else {
            println(
                """
                Please Enter Valid Password
                Password should be 8 or more characters with a mix of capital letters(A-Z), small letters(a-z), numbers(0-9) & symbols(@,#,$,%,^,&,+,=)
            """.trimIndent()
            )
            getPassword()
        }
    }

    fun getMultiLineInput(request: String): String {
        println("\n$request : ")
        var input = ""
        val scanner = Scanner(System.`in`)
        while (true) {
            val i = scanner.nextLine()
            if (i == "--q") {
                return input
            }
            input += "\n" + i
        }
    }

}