package com.de.scalasourcing

import scala.util.Try

object Operations
{
    implicit class StateEx[S](val state: Option[S]) extends AnyVal
    {
        def +[E](event: E)(implicit a: Applicator[S, E]): Option[S] =
        {
            a(state, event)
        }

        def +[E](events: Seq[E])(implicit a: Applicator[S, E]): Option[S] =
        {
            a(state, events)
        }

        def ![E, C](command: C)(implicit s: Sourcer[S, E, C]): Seq[E] =
        {
            s(state, command)
        }

        def !![E, C](command: C)(implicit a: Sourcer[S, E, C]): Try[Seq[E]] =
        {
            Try(a(state, command))
        }
    }

    implicit class EventsListEx[E](val events: Seq[E]) extends AnyVal
    {
        def toState[S]()(implicit a: Applicator[S, E]): Option[S] =
        {
            a(events)
        }

        def ![S, C](events: Seq[E], command: C)(implicit a: Applicator[S, E], s: Sourcer[S, E, C]): Seq[E] =
        {
            events.toState ! command
        }

        def !![S, C](events: Seq[E], command: C)(implicit a: Applicator[S, E], s: Sourcer[S, E, C]): Try[Seq[E]] =
        {
            events.toState !! command
        }
    }
}

