# ScalaSourcing

This is a library for writing Domain Driven and Event Sourced application in Scala.

It's just started and doesn't allow much for now. But I want to grow it into the real deal and use it on my own project (which is not open source yet).


## Highlights

Here's how an aggregate root is defined:

```scala
package com.scalasourcing.example.voting

import com.scalasourcing.AggregateRoot

case class Upvote()

object Upvote extends AggregateRoot[Upvote]
{
    case class Set() extends Command
    case class Cancel() extends Command

    case class $Set() extends Event
    case class Cancelled() extends Event

    case class AlreadySet() extends Error
    case class NotSet() extends Error

    def apply(state: State, event: Event): State = (state, event) match
    {
        case (None, $Set())                => Upvote()
        case (Some(Upvote()), Cancelled()) => None
        case _                             => state
    }

    def apply(state: State, command: Command): CommandResult = state match
    {
        case None           => whenNone(command)
        case Some(Upvote()) => whenSome(command)
    }

    private def whenNone(command: Command): CommandResult = command match
    {
        case Set()    => $Set()
        case Cancel() => NotSet()
    }

    private def whenSome(command: Command): CommandResult = command match
    {
        case Set()    => AlreadySet()
        case Cancel() => Cancelled()
    }
}
```

Here's a suit of tests for that root:

```scala
package com.scalasourcing.example

import com.scalasourcing.bdd.AggregateBDD
import com.scalasourcing.example.voting.Upvote
import com.scalasourcing.example.voting.Upvote._
import org.scalatest._

class UpvoteSuite extends FunSuite with Matchers with AggregateBDD[Upvote]
{
    test("An upvote should be set")
    {
        given_nothing when_I Set() then_it_is $Set()
    }

    test("Set upvote should not be set again")
    {
        given it_was $Set() when_I Set() then_error AlreadySet()
    }

    test("Set upvote should be cancelled")
    {
        given it_was $Set() when_I Cancel() then_it_is Cancelled()
    }

    test("Unset upvote should not be cancelled")
    {
        given_nothing when_I Cancel() then_error NotSet()
    }

    test("An upvote should be set when it was set and then cancelled")
    {
        given it_was $Set() and Cancelled() when_I Set() then_it_is $Set()
    }

    test("An upvote should not be cancelled when it was set and then cancelled")
    {
        given it_was $Set() and Cancelled() when_I Cancel() then_error NotSet()
    }
}
```

## Installation
The library is not published yet. Sorry! I actually never done this before in jvm infrastructure and would appreciate any help!
