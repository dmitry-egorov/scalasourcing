package com.de.todoes.voting

import com.de.scalasourcing.AggregateRoot

case class Upvote()

object Upvote extends AggregateRoot[Upvote]
{
    implicit object ca extends CommandApplication
    {
        def apply(state: State, command: Command): CommandResult = state match
        {
            case None           => toNone(command)
            case Some(Upvote()) => toSome(command)
        }

        def toNone(command: Command): CommandResult = command match
        {
            case Set()    => $Set()
            case Cancel() => NotSet()
        }

        def toSome(command: Command): CommandResult = command match
        {
            case Set()    => AlreadySet()
            case Cancel() => Cancelled()
        }
    }

    implicit object ea extends EventApplication
    {
        def apply(state: State, event: Event): State = (state, event) match
        {
            case (None, $Set())                => Upvote()
            case (Some(Upvote()), Cancelled()) => None
            case _                             => state
        }
    }

    case class Set() extends Command
    case class Cancel() extends Command

    case class $Set() extends Event
    case class Cancelled() extends Event

    case class AlreadySet() extends Error
    case class NotSet() extends Error
}

