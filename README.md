# ScalaSourcing

This is a library for writing Domain Driven and Event Sourced applications in Scala.

It's just started and doesn't allow much for now. But I want to grow it into the real deal and use it on my own project (which is not open source yet).


## Highlights

Here's how an aggregate root is defined:

```scala
import com.scalasourcing._

sealed trait Upvote extends AggregateRoot[Upvote]

object Upvote extends AggregateFactory[Upvote]
{
    case class Cast() extends Command
    case class Cancel() extends Command

    case class $Cast() extends Event
    case class Cancelled() extends Event

    case class WasAlreadyCastError() extends Error
    case class WasNotCastError() extends Error

    case class CastUpvote() extends Upvote
    {
        def apply(event: Event): Upvote = event match
        {
            case Cancelled() => NotCastUpvote()
            case _           => this
        }

        def apply(command: Command): CommandResult = command match
        {
            case Cast()    => WasAlreadyCastError()
            case Cancel() => Cancelled()
        }
    }
    case class NotCastUpvote() extends Upvote
    {
        def apply(event: Event): Upvote = event match
        {
            case $Cast() => CastUpvote()
            case _      => this
        }

        def apply(command: Command): CommandResult = command match
        {
            case Cast()    => $Cast()
            case Cancel() => WasNotCastError()
        }
    }

    def create: Upvote = NotCastUpvote()
}
```

Here's a suit of tests for that root:

```scala
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
```

## Installation

The library is not published yet. Sorry! I actually never done this before in jvm infrastructure and would appreciate any help!

## Inspirations

Inspired by:

Akka event sourcing library [eventsourced](https://github.com/eligosource/eventsourced).

This little beautiful [aggregate in Haskell](https://gist.github.com/Fristi/7327904).

Another elegant [aggregate in Haskell](https://gist.github.com/philipnilsson/9200533).
