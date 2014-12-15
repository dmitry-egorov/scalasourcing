package com.scalasoucing.example.voting

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

