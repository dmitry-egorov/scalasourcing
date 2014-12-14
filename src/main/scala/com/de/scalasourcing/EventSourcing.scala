package com.de.scalasourcing

object EventSourcing
{
    trait EventOf[S]
    trait CommandOf[S]
    trait ErrorOf[S]
    type Sourcing[S] = Either[Seq[EventOf[S]], ErrorOf[S]]

    trait Applicator[S]
    {
        def apply(state: Option[S], event: EventOf[S]): Option[S]

        def apply(state: Option[S], events: Seq[EventOf[S]]): Option[S] =
            events.foldLeft(state)((a, e) => apply(a, e))

        def apply(events: Seq[EventOf[S]]): Option[S] =
            events.foldLeft(Option.empty[S])((a, e) => apply(a, e))
    }

    trait Sourcer[S]
    {
        def apply(agg: Option[S], command: CommandOf[S]): Sourcing[S]
    }

    implicit class StateEx[S](val state: Option[S]) extends AnyVal
    {
        def +(event: EventOf[S])(implicit a: Applicator[S]): Option[S] =
        {
            a(state, event)
        }

        def +(events: Seq[EventOf[S]])(implicit a: Applicator[S]): Option[S] =
        {
            a(state, events)
        }

        def !(command: CommandOf[S])(implicit s: Sourcer[S]): Sourcing[S] =
        {
            s(state, command)
        }
    }

    implicit class EventsListEx[S](val events: Seq[EventOf[S]]) extends AnyVal
    {
        def toState()(implicit a: Applicator[S]): Option[S] =
        {
            a(events)
        }

        def !(events: Seq[EventOf[S]], command: CommandOf[S])(implicit a: Applicator[S], s: Sourcer[S]): Sourcing[S] =
        {
            events.toState ! command
        }
    }

    implicit def ok[S](e: S): Option[S] = Some(e)

    implicit def ok[S](e: EventOf[S]): Sourcing[S] = Left(Seq(e))
    implicit def error[S](e: ErrorOf[S]): Sourcing[S] = Right(e)
}
