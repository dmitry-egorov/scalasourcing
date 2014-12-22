package com.scalasourcing.example

import com.scalasourcing.bdd.AggregateBDD
import com.scalasourcing.example.domain.editing.Todo
import com.scalasourcing.example.domain.editing.Todo._
import org.scalatest._

class TodoSuite extends FunSuite with Matchers with AggregateBDD
{
    val agg = Todo

    test("A todo should be added")
    {
        given_nothing when_I Add("text") then_it_is Added("text")
    }

    test("A todo with empty text should not be added")
    {
        given_nothing when_I Add("  ") then_expect NewTextIsEmptyError()
    }

    test("A todo should not be added for the second time")
    {
        given it_was Added("text") when_I Add("text 2") then_expect TodoExistedError()
    }

    test("Added todo should be edited with different text")
    {
        given it_was Added("text") when_I Edit("text 2") then_it_is Edited("text 2")
    }

    test("Edited todo should be edited with different text")
    {
        given it_was Added("text") and Edited("text 2") when_I Edit("text 3") then_it_is Edited("text 3")
    }

    test("A non existing todo should not be edited")
    {
        given_nothing when_I Edit("text") then_expect TodoDidNotExistError()
    }

    test("Added todo should not be edited with the same text")
    {
        given it_was Added("text") when_I Edit("text") then_expect NewTextIsTheSameAsTheOldError()
    }

    test("Edited todo should not be edited again with the same text")
    {
        given it_was Added("text") and Edited("text 2") when_I Edit("text 2") then_expect NewTextIsTheSameAsTheOldError()
    }

    test("Added todo should not be edited to empty text")
    {
        given it_was Added("text") when_I Edit("  ") then_expect NewTextIsEmptyError()
    }

    test("A todo should be removed")
    {
        given it_was Added("text") when_I Remove() then_it_is Removed()
    }

    test("A todo should not be removed when it doesn't exist")
    {
        given_nothing when_I Remove() then_expect TodoDidNotExistError()
    }
}
