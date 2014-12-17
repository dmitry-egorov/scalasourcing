package com.scalasourcing.example

import com.scalasourcing.example.domain.voting.Upvote
import com.scalasourcing.example.domain.voting.Upvote._
import com.scalasourcing.bdd.AggregateBDD
import org.scalatest._

class UpvoteSuite extends FunSuite with Matchers with AggregateBDD[Upvote]
{
    test("An upvote should be casted")
    {
        given_nothing when_I Cast() then_it_is Casted()
    }

    test("Casted upvote should not be casted again")
    {
        given it_was Casted() when_I Cast() then_expect WasAlreadyCastedError()
    }

    test("Casted upvote should be cancelled")
    {
        given it_was Casted() when_I Cancel() then_it_is Cancelled()
    }

    test("Not casted upvote should not be cancelled")
    {
        given_nothing when_I Cancel() then_expect WasNotCastedError()
    }

    test("An upvote should be casted when it was casted and then cancelled")
    {
        given it_was Casted() and Cancelled() when_I Cast() then_it_is Casted()
    }

    test("An upvote should not be cancelled when it was casted and then cancelled")
    {
        given it_was Casted() and Cancelled() when_I Cancel() then_expect WasNotCastedError()
    }
}
