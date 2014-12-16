package com.scalasourcing.example.voting

import com.scalasourcing.AggregateRoot

case class Upvote()

object Upvote extends AggregateRoot[Upvote]
{
    case class Set() extends Command
    case class Cancel() extends Command

    case class $Set() extends Event
    case class Cancelled() extends Event

    case class WasSetError() extends Error
    case class WasNotSetError() extends Error

    def apply(state: Option[Upvote], event: Event): Option[Upvote] = (state, event) match
    {
        case (None, $Set())                => Upvote()
        case (Some(Upvote()), Cancelled()) => None
        case _                             => state
    }

    def apply(state: Option[Upvote], command: Command): CommandResult =
    {
        def r = state match
        {
            case None           => whenNone
            case Some(Upvote()) => whenSome
        }

        def whenNone: CommandResult = command match
        {
            case Set()    => $Set()
            case Cancel() => WasNotSetError()
        }

        def whenSome: CommandResult = command match
        {
            case Set()    => WasSetError()
            case Cancel() => Cancelled()
        }

        r
    }
}

