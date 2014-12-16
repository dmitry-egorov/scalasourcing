package com.scalasourcing.example

import com.scalasourcing.bdd.AggregateBDD
import com.scalasourcing.example.voting.Upvote
import com.scalasourcing.example.voting.Upvote._
import org.scalatest._

class UpvoteSuite extends FunSuite with Matchers with AggregateBDD[Upvote]
{
    test("An upvote should be cast")
    {
        given_nothing when_I Cast() then_it_is $Cast()
    }

    test("Cast upvote should not be cast again")
    {
        given it_was $Cast() when_I Cast() then_expect WasAlreadyCastError()
    }

    test("Cast upvote should be cancelled")
    {
        given it_was $Cast() when_I Cancel() then_it_is Cancelled()
    }

    test("Not cast upvote should not be cancelled")
    {
        given_nothing when_I Cancel() then_expect WasNotCastError()
    }

    test("An upvote should be cast when it was cast and then cancelled")
    {
        given it_was $Cast() and Cancelled() when_I Cast() then_it_is $Cast()
    }

    test("An upvote should not be cancelled when it was cast and then cancelled")
    {
        given it_was $Cast() and Cancelled() when_I Cancel() then_expect WasNotCastError()
    }
}
