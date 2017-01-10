package com.hackon.akkounting

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.endsWith
import com.natpryce.hamkrest.should.describedAs
import com.natpryce.hamkrest.should.shouldMatch
import com.natpryce.hamkrest.startsWith
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.time.LocalDate
import kotlin.reflect.memberProperties
import kotlin.test.assertTrue

/**
 * Created by haakon on 09.01.17.
 */

class InvoiceSpek : Spek({
  describe("a thing") {
    on("an action") {
      it("should do something") {
        "xy" describedAs "xy" shouldMatch (startsWith("x") and endsWith("y"))
        assertThat("xy", startsWith("x") and endsWith("y"))
      }
    }
  }
  describe("creating an invoice") {
    val invoice = Invoice(AccountNr("99990000001"), KidNr("2345676"), LocalDate.now())

    on("new invoice") {
      it("should be valid") {
        assertTrue { invoice.validate() }
      }
    }
  }
})

data class Invoice(
    val kontoNr: AccountNr,
    val kidNr: KidNr,
    val dueDate: LocalDate

) {
  fun validate(): Boolean {
    val list = Invoice::class.memberProperties
        .map { it.get(this) }
        .filterIsInstance<Validator<*>>().toList()
    return list.all { it.validate() }
  }
}

data class KidNr(val kidNr: String) : Validator<KidNr> {
  override fun validate() = validateKidNr(kidNr)
}

data class AccountNr(val accountNr: String) : Validator<AccountNr> {
  override fun validate() = validateAccountNr(accountNr)

  fun regNr() = accountNr.take(4)
  fun accountType() = accountNr.substring(4, 6)
  fun accountRest() = accountNr.substring(6, 10)
  fun account() = accountType() + accountRest()
}


interface Validator<T> {
  fun validate(): Boolean
}

private fun String.mod10() = 10 - this.dropLast(1)
    .foldRightIndexed(0) { i, next, total -> total + greaterThan10(next.int() * (i % 2 + 1)) }
    .mod(10) == this.takeLast(1).toInt()

private fun String.mod11(): Boolean {
  val weight = arrayOf(2, 3, 4, 5, 6, 7)
  return 11 - this.dropLast(1)
      .reversed()
      .foldIndexed(0) { i, total, next -> total + (next.int() * weight[i % weight.size]) }
      .mod(11) == this.takeLast(1).toInt()
}

fun greaterThan10(it: Int): Int = if (it > 9) (1 + it.mod(10)) else it

fun Char.int() = this.toString().toInt()
