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
    case class Set() extends Command
    case class Cancel() extends Command

    case class $Set() extends Event
    case class Cancelled() extends Event

    case class WasSetError() extends Error
    case class WasNotSetError() extends Error

    case class SetUpvote() extends Upvote
    {
        def apply(event: Event): Upvote = event match
        {
            case Cancelled() => UnsetUpvote()
            case _           => this
        }

        def apply(command: Command): CommandResult = command match
        {
            case Set()    => WasSetError()
            case Cancel() => Cancelled()
        }
    }
    case class UnsetUpvote() extends Upvote
    {
        def apply(event: Event): Upvote = event match
        {
            case $Set() => SetUpvote()
            case _      => this
        }

        def apply(command: Command): CommandResult = command match
        {
            case Set()    => $Set()
            case Cancel() => WasNotSetError()
        }
    }

    def create: Upvote = UnsetUpvote()
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
    test("An upvote should be set")
    {
        given_nothing when_I Set() then_it_is $Set()
    }

    test("Set upvote should not be set again")
    {
        given it_was $Set() when_I Set() then_expect WasSetError()
    }

    test("Set upvote should be cancelled")
    {
        given it_was $Set() when_I Cancel() then_it_is Cancelled()
    }

    test("Unset upvote should not be cancelled")
    {
        given_nothing when_I Cancel() then_expect WasNotSetError()
    }

    test("An upvote should be set when it was set and then cancelled")
    {
        given it_was $Set() and Cancelled() when_I Set() then_it_is $Set()
    }

    test("An upvote should not be cancelled when it was set and then cancelled")
    {
        given it_was $Set() and Cancelled() when_I Cancel() then_expect WasNotSetError()
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
