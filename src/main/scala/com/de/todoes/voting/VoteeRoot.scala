package com.de.todoes.voting

import com.de.scalasourcing.EventSourcing._

object VoteeRoot
{
    case class Votee()
    type State = Option[Votee]
    type Command = CommandOf[Votee]
    type Event = EventOf[Votee]

    implicit object Sourcer extends Sourcer[Votee]
    {
        override def apply(state: State, command: Command): Seq[Event] = state match
        {
            case None            => applyToNone (command)
            case Some (Votee ()) => applyToSome (command)
        }

        def applyToNone(command: Command): Seq[Event] = command match
        {
            case Upvote ()       => Seq (Upvoted ())
            case CancelUpvote () => throw NotUpvoted ()
        }

        def applyToSome(command: Command): Seq[Event] = command match
        {
            case Upvote ()       => throw AlreadyUpvoted ()
            case CancelUpvote () => Seq (UpvoteCancelled ())
        }
    }

    implicit object Applicator extends Applicator[Votee]
    {
        override def apply(state: State, event: Event): State =
            (state, event) match
            {
                case (None, Upvoted ())                    => Some (Votee ())
                case (Some (Votee ()), UpvoteCancelled ()) => None
                case _                                     => state
            }
    }

    case class Upvote() extends Command
    case class CancelUpvote() extends Command

    case class Upvoted() extends Event
    case class UpvoteCancelled() extends Event

    case class AlreadyUpvoted() extends Exception
    case class NotUpvoted() extends Exception
}
