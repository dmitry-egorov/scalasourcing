package com.de.todoes.voting

import com.de.scalasourcing.EventSourcing._

object VoteeRoot
{
    case class Votee()
    type State = Option[Votee]
    type Command = CommandOf[Votee]
    type Event = EventOf[Votee]
    type Error = ErrorOf[Votee]

    implicit object Sourcer extends Sourcer[Votee]
    {
        override def apply(state: State, command: Command): Sourcing[Votee] = state match
        {
            case None          => applyToNone(command)
            case Some(Votee()) => applyToSome(command)
        }

        def applyToNone(command: Command): Sourcing[Votee] = command match
        {
            case Upvote()       => Upvoted()
            case CancelUpvote() => NotUpvoted()
        }

        def applyToSome(command: Command): Sourcing[Votee] = command match
        {
            case Upvote()       => AlreadyUpvoted()
            case CancelUpvote() => UpvoteCancelled()
        }
    }

    implicit object Applicator extends Applicator[Votee]
    {
        override def apply(state: State, event: Event): State =
            (state, event) match
            {
                case (None, Upvoted())                  => Votee()
                case (Some(Votee()), UpvoteCancelled()) => None
                case _                                  => state
            }
    }

    case class Upvote() extends Command
    case class CancelUpvote() extends Command

    case class Upvoted() extends Event
    case class UpvoteCancelled() extends Event

    case class AlreadyUpvoted() extends Error
    case class NotUpvoted() extends Error
}
