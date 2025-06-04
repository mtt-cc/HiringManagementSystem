package it.polito.waii_24.g20.crm_old.unit.common

import it.polito.waii_24.g20.crm_old.util.Category
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomString(length: Int) = ThreadLocalRandom.current()
    .ints(length.toLong(), 0, charPool.size)
    .asSequence()
    .map(charPool::get)
    .joinToString("")

fun randomCategory(): Category {
    val seed = (Math.random() * 3).toInt()

    return when(seed) {
        0 -> Category.Customer
        1 -> Category.Professional
        else -> Category.Unknown
    }
}