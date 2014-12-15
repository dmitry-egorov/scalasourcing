package com.de.todoes.voting

import com.de.scalasourcing.AggregateRoot

case class Upvote()

object Upvote
{
    trait UpvoteRoot extends AggregateRoot[Upvote]
    {
        implicit object sourcer extends Sourcer
        {
            def apply(state: State, command: Command): Sourcing = state match
            {
                case None          => applyToNone(command)
                case Some(Upvote()) => applyToSome(command)
            }

            def applyToNone(command: Command): Sourcing = command match
            {
                case Set()    => Set_()
                case Cancel() => NotSet()
            }

            def applyToSome(command: Command): Sourcing = command match
            {
                case Set()    => AlreadySet()
                case Cancel() => Cancelled()
            }
        }

        implicit object applicator extends Applicator
        {
            def apply(state: State, event: Event): State =
                (state, event) match
                {
                    case (None, Set_())               => Upvote()
                    case (Some(Upvote()), Cancelled()) => None
                    case _                            => state
                }
        }

        case class Set() extends Command
        case class Cancel() extends Command

        case class Set_() extends Event
        case class Cancelled() extends Event

        case class AlreadySet() extends Error
        case class NotSet() extends Error
    }
}

