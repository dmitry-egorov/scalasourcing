package com.scalasourcing.example.domain.voting

import com.scalasourcing.example.domain.editing.TodoId
import com.scalasourcing.model._

object Upvote extends Aggregate
{
    type Id = TodoId

    case class Cast() extends Command
    case class Cancel() extends Command

    case class Casted() extends Event
    case class Cancelled() extends Event

    case class WasAlreadyCastedError() extends Error
    case class WasNotCastedError() extends Error

    case class CastedUpvote() extends State
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

    case class NotCastedUpvote() extends State
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