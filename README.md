# ScalaSourcing

This is a library for writing Domain Driven and Event Sourced applications in Scala.

It's just started and doesn't allow much for now. But I want to grow it into the real deal and use it on my own project (which is not open source yet).


## Highlights

Here's how an aggregate root is defined:

```scala
package com.scalasourcing.example.domain.voting

import com.scalasourcing.model._

sealed trait Upvote extends AggregateRoot[Upvote]

object Upvote extends AggregateRootCompanion[Upvote]
{
    case class Cast() extends Command
    case class Cancel() extends Command

    case class Casted() extends Event
    case class Cancelled() extends Event

    case class WasAlreadyCastedError() extends Error
    case class WasNotCastedError() extends Error

    case class CastedUpvote() extends Upvote
    {
        def apply(event: Event) = event match
        {
            case Cancelled() => NotCastedUpvote()
            case _           => this
        }

        def apply(command: Command) = command match
        {
            case Cast()   => WasAlreadyCastedError()
            case Cancel() => Cancelled()
        }
    }

    case class NotCastedUpvote() extends Upvote
    {
        def apply(event: Event) = event match
        {
            case Casted() => CastedUpvote()
            case _        => this
        }

        def apply(command: Command) = command match
        {
            case Cast()   => Casted()
            case Cancel() => WasNotCastedError()
        }
    }

    def seed = NotCastedUpvote()
}
```

Here's a suit of tests for that root:

```scala
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
```

And here's how you can use it in an application:

```scala
import com.scalasourcing.backend.CommandsExecutor
import com.scalasourcing.backend.memory.SingleThreadInMemoryEventStorage
import com.scalasourcing.example.domain.voting.Upvote._

object SimpleUpvoteApp extends App
{
    val eventStorage = new SingleThreadInMemoryEventStorage with CommandsExecutor
    eventStorage.subscribe(print)

    val id = "1"
    eventStorage.execute(id, Cast())
    eventStorage.execute(id, Cancel())

    println("Thank you for using our beautiful app!")

    def print(result: Event): Unit =
    {
        val readable = result match
        {
            case Casted()    => s"Upvote casted."
            case Cancelled() => s"Upvote cancelled."
        }

        println(readable)
    }
}
```

## Installation

The library is not published yet. Sorry! I actually never done this before in jvm infrastructure and would appreciate any help!

## Inspirations

Inspired by:

Akka event sourcing library [eventsourced](https://github.com/eligosource/eventsourced).

This little beautiful [aggregate in Haskell](https://gist.github.com/Fristi/7327904).

Another elegant [aggregate in Haskell](https://gist.github.com/philipnilsson/9200533).
