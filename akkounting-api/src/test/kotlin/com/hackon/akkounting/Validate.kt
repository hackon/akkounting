package com.hackon.akkounting

private val BASE_MOD11_WEIGHTS = intArrayOf(2, 3, 4, 5, 6, 7)

fun validateAccountNr(accountNr: String) =
    validateAllDigits(accountNr)
        && validateNotStartWith(accountNr, "0000")
        && validateLength(accountNr, 11)
        && validateAccountChecksum(accountNr)

fun validateKidNr(kidNr: String) =
    validateAllDigits(kidNr)
        && validateLengthInRange(kidNr, 2, 25)
        && validateKidChecksum(kidNr)


private fun calculateMod11CheckSum(weights: IntArray, number: String): Int {
  val c = calculateChecksum(weights, number, false) % 11
  return if (c == 1) -1 else if (c == 0) 0 else 11 - c
}

private fun calculateMod10CheckSum(weights: IntArray, number: String): Int {
  val c = calculateChecksum(weights, number, true) % 10
  return if (c == 0) 0 else 10 - c
}

private fun calculateChecksum(weights: IntArray, number: String, sum: Boolean) =
    weights.mapIndexed { i, w -> w * number[weights.size - 1 - i].int() }
        .sumBy { if (sum) if (it > 9) it - 9 else it else it }

private fun validateAllDigits(numberString: String) =
    numberString.all(Char::isDigit)

private fun getMod10Weights(k: String) =
    IntRange(0, k.length - 1)
        .map { if (it % 2 == 0) 2 else 1 }
        .toIntArray()

private fun getMod11Weights(k: String): IntArray {
  return IntRange(0, k.length - 1)
      .map { BASE_MOD11_WEIGHTS[it % BASE_MOD11_WEIGHTS.size] }
      .toIntArray()
}

private fun validateLength(value: String, length: Int) = value.length == length

private fun validateLengthInRange(value: String, min: Int, max: Int) = value.length inRangeInc (min to max)

private fun validateNotStartWith(value: String, notStartWith: String) = value.startsWith(notStartWith).not()

private fun validateAccountChecksum(accountNr: String) =
    calculateMod11CheckSum(getMod11Weights(accountNr), accountNr) == accountNr.takeLast(1).toInt()


private fun validateKidChecksum(kidNr: String) =
    calculateMod10CheckSum(getMod10Weights(kidNr), kidNr) == kidNr.takeLast(1).toInt()
        || calculateMod11CheckSum(getMod11Weights(kidNr), kidNr) == kidNr.takeLast(1).toInt()

private infix fun Int.inRangeInc(range: Pair<Int, Int>): Boolean = this >= range.first && this <= range.second