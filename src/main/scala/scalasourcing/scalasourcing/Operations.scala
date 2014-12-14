package scalasourcing.scalasourcing

import scala.util.Try

object Operations
{
    implicit class AggregateRootEx[A](val agg: Option[A]) extends AnyVal
    {
        def +[E](event: E)(implicit a: Application[A, E]): Option[A] =
        {
            a.applyEvent(agg, event)
        }

        def +[E](events: Seq[E])(implicit a: Application[A, E]): Option[A] =
        {
            a.applyEvents(agg, events)
        }

        def !![E, C](command: C)(implicit a: Sourcing[A, E, C]): Seq[E] =
        {
            a.order(agg, command)
        }

        def !!![E, C](command: C)(implicit a: Sourcing[A, E, C]): Try[Seq[E]] =
        {
            Try(a.order(agg, command))
        }

    }

    implicit class EventsListEx[E](val events: Seq[E]) extends AnyVal
    {
        def toAggregate[TAggregate]()(implicit a: Application[TAggregate, E]): Option[TAggregate] =
        {
            a.applyEvents(events)
        }
    }


}

