package com.de.todoes.contexts.voting

import com.de.scalasourcing._

object VoteeRoot
{
    case class Votee()
    sealed trait VoteeCommand
    sealed trait VoteeEvent

    implicit object Sourcer extends Sourcer[Votee, VoteeEvent, VoteeCommand]
    {
        override def apply(state: Option[Votee], command: VoteeCommand): Seq[VoteeEvent] =
            (state, command) match
            {
                case (None, Upvote())                => Seq(Upvoted())
                case (None, CancelUpvote())          => throw NotUpvotedError()
                case (Some(Votee()), Upvote())       => throw AlreadyUpvotedError()
                case (Some(Votee()), CancelUpvote()) => Seq(UpvoteCancelled())
            }
    }

    implicit object Applicator extends Applicator[Votee, VoteeEvent]
    {
        override def apply(agg: Option[Votee], event: VoteeEvent): Option[Votee] =
            (agg, event) match
            {
                case (None, Upvoted())                  => Some(Votee())
                case (Some(Votee()), UpvoteCancelled()) => None
                case _                                  => agg
            }
    }

    case class Upvote() extends VoteeCommand
    case class CancelUpvote() extends VoteeCommand

    case class Upvoted() extends VoteeEvent
    case class UpvoteCancelled() extends VoteeEvent

    case class AlreadyUpvotedError() extends Exception
    case class NotUpvotedError() extends Exception
}
