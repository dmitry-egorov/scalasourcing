package com.scalasourcing

import com.scalasourcing.AggregateFactory._

trait AggregateFactory[S <: AggregateRoot[S]] extends F[S]
{
    implicit val f: F[S] = this

    type Command = CommandOf[S]
    type Event = EventOf[S]
    type Error = ErrorOf[S]
    type EventsSeq = EventsSeqOf[S]
    type CommandResult = CommandResultOf[S]

    implicit def toRichEventsSeq(events: EventsSeq): RichEventsSeqOf[S] = new RichEventsSeqOf[S](events)

    implicit protected def ok(event: Event): CommandResult = Left(Seq(event))
    implicit protected def error(error: Error): CommandResult = Right(error)
}

object AggregateFactory
{
    trait CommandOf[S]
    trait EventOf[S]
    trait ErrorOf[S]

    type EventsSeqOf[S] = Seq[EventOf[S]]
    type CommandResultOf[S] = Either[EventsSeqOf[S], ErrorOf[S]]

    type F[S] = FactoryOf[S]

    trait FactoryOf[S]
    {
        def create: S
    }

    implicit class RichEventsSeqOf[S <: AggregateRoot[S]](val events: EventsSeqOf[S]) extends AnyVal
    {
        def toState()(implicit f: F[S]): S = events.foldLeft(f.create)((s, e) => s(e))

        def !(command: CommandOf[S])(implicit f: F[S]): CommandResultOf[S] = toState ! command
    }
}
