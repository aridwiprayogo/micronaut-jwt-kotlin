package com.aridwiprayogo

import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
    build().args(*args).packages("com.aridwiprayogo").start()
}

