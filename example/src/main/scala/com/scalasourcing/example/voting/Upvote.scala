package com.scalasourcing.example.voting

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

