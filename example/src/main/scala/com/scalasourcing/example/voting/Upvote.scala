package com.scalasourcing.example.voting

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

