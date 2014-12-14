package com.de.scalasourcing

import scala.util.Try

object EventSourcing
{
    trait EventOf[S]
    trait CommandOf[S]
    trait ErrorOf[S]

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
        def apply(agg: Option[S], command: CommandOf[S]): Seq[EventOf[S]]
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

        def !(command: CommandOf[S])(implicit s: Sourcer[S]): Seq[EventOf[S]] =
        {
            s(state, command)
        }

        def !!(command: CommandOf[S])(implicit a: Sourcer[S]): Try[Seq[EventOf[S]]] =
        {
            Try(a(state, command))
        }
    }

    implicit class EventsListEx[S](val events: Seq[EventOf[S]]) extends AnyVal
    {
        def toState()(implicit a: Applicator[S]): Option[S] =
        {
            a(events)
        }

        def !(events: Seq[EventOf[S]], command: CommandOf[S])(implicit a: Applicator[S], s: Sourcer[S]): Seq[EventOf[S]] =
        {
            events.toState ! command
        }

        def !!(events: Seq[EventOf[S]], command: CommandOf[S])(implicit a: Applicator[S], s: Sourcer[S]): Try[Seq[EventOf[S]]] =
        {
            events.toState !! command
        }
    }
}
