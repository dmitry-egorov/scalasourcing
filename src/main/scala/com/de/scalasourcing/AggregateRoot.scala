package com.de.scalasourcing

trait AggregateRoot[S]
{
    def create: State = None

    implicit val applicator: Applicator
    implicit val sourcer: Sourcer

    trait Event
    trait Command
    trait Error
    type Events = Seq[Event]
    type Sourcing = Either[Events, Error]
    type State = Option[S]

    trait Applicator
    {
        def apply(state: State, event: Event): State

        def apply(state: State, events: Events): State =
            events.foldLeft(state)((a, e) => apply(a, e))

        def apply(events: Events): State =
            events.foldLeft(State.empty)((a, e) => apply(a, e))
    }

    trait Sourcer
    {
        def apply(agg: State, command: Command): Sourcing
    }

    object State
    {
        def empty: State = Option.empty[S]
    }
    
    implicit class StateEx(val state: Option[S])
    {
        def +(event: Event)(implicit a: Applicator): State =
        {
            a(state, event)
        }

        def +(events: Events)(implicit a: Applicator): State =
        {
            a(state, events)
        }

        def !(command: Command)(implicit s: Sourcer): Sourcing =
        {
            s(state, command)
        }

        def !+(command: Command): State =
        {
            state + (state ! command).left.get
        }
    }

    implicit class EventsEx(val events: Seq[Event])
    {
        def toState()(implicit a: Applicator): State =
        {
            a(events)
        }

        def !(events: Events, command: Command): Sourcing =
        {
            events.toState ! command
        }
    }

    implicit protected def ok(e: S): State = Some(e)
    implicit protected def ok(e: Event): Sourcing = Left(Seq(e))
    implicit protected def error(e: Error): Sourcing = Right(e)
}
