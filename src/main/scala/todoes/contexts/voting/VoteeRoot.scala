package todoes.contexts.voting

import scalasourcing.scalasourcing.Aggregation

object VoteeRoot
{
    case class Votee()

    sealed trait VoteeEvent
    case class Upvoted() extends VoteeEvent
    case class UpvoteCancelled() extends VoteeEvent

    sealed trait VoteeCommand
    case class Upvote() extends VoteeCommand
    case class CancelUpvote() extends VoteeCommand

    case class AlreadyUpvotedError() extends Exception
    case class NotUpvotedError() extends Exception

    implicit object Votee extends Aggregation[Votee, VoteeEvent, VoteeCommand]
    {
        override def order(agg: Option[Votee], command: VoteeCommand): Seq[VoteeEvent] =
            (agg, command) match
            {
                case (None, Upvote())                => ok(Upvoted())
                case (None, CancelUpvote())          => throw NotUpvotedError()
                case (Some(Votee()), Upvote())       => throw AlreadyUpvotedError()
                case (Some(Votee()), CancelUpvote()) => ok(UpvoteCancelled())
            }

        override def applyEvent(agg: Option[Votee], event: VoteeEvent): Option[Votee] =
            (agg, event) match
            {
                case (None, Upvoted())                  => Some(Votee())
                case (Some(Votee()), UpvoteCancelled()) => None
                case _                                  => agg
            }
    }
}
