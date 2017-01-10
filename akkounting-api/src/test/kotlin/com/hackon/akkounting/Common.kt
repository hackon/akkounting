package com.hackon.akkounting

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on


/**
 * Created by haakon on 10.01.17.
 */
class Common : Spek({
  describe("commin") {
    on("anything") {
      it("should") {
        println(validateKidNr("004340570020320530"))
      }
    }
  }
})

private val BASE_MOD11_WEIGHTS = intArrayOf(2, 3, 4, 5, 6, 7)

fun calculateMod11CheckSum(weights: IntArray, number: String): Int {
  val c = calculateChecksum(weights, number, false) % 11
  return if (c == 1) -1 else if (c == 0) 0 else 11 - c
}

fun calculateMod10CheckSum(weights: IntArray, number: String): Int {
  val c = calculateChecksum(weights, number, true) % 10
  return if (c == 0) 0 else 10 - c
}

private fun calculateChecksum(weights: IntArray, number: String, sum: Boolean) =
    weights.mapIndexed { i, w -> w * number[weights.size - 1 - i].int() }
        .sumBy { if (sum) if (it > 9) it - 9 else it else it }

fun validateAllDigits(numberString: String) =
    numberString.all(Char::isDigit)

fun getMod10Weights(k: String) =
    IntRange(0, k.length - 1)
        .map { if (it % 2 == 0) 2 else 1 }
        .toIntArray()

fun getMod11Weights(k: String): IntArray {
  val weights = IntArray(k.length - 1)
  for (i in weights.indices) {
    weights[i] = BASE_MOD11_WEIGHTS[i % BASE_MOD11_WEIGHTS.size]
  }
  return weights
}

fun validateAccountNr(accountNr: String) =
    validateNotStartWith(accountNr, "0000")
        && validateLength(accountNr, 11)
        && validateAccountChecksum(accountNr)

fun validateLength(accountNr: String, length: Int) = accountNr.length == length

fun validateNotStartWith(nr: String, notStartWith: String) = nr.startsWith(notStartWith).not()

fun validateKidNr(kidnummer: String) = validateKidSyntax(kidnummer) && validateKidChecksum(kidnummer)

fun validateKidSyntax(kidnummer: String) = validateAllDigits(kidnummer) && validateLengthInRange(kidnummer, 2, 25)

fun validateLengthInRange(kidnummer: String, min: Int, max: Int) = kidnummer.length inRangeInc (min to max)

fun validateAccountChecksum(kontonummer: String) =
    calculateMod11CheckSum(getMod11Weights(kontonummer), kontonummer) == kontonummer.takeLast(1).toInt()


fun validateKidChecksum(kidnummer: String) =
    calculateMod10CheckSum(getMod10Weights(kidnummer), kidnummer) == kidnummer.takeLast(1).toInt()
        || calculateMod11CheckSum(getMod11Weights(kidnummer), kidnummer) == kidnummer.takeLast(1).toInt()

infix fun Int.inRangeInc(range: Pair<Int, Int>): Boolean = this >= range.first && this <= range.second