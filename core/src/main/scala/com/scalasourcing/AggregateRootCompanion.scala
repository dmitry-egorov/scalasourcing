package com.scalasourcing

import com.scalasourcing.AggregateRootCompanion._

trait AggregateRootCompanion[AR <: AggregateRoot[AR]] extends FactoryOf[AR]
{
    implicit val factory: FactoryOf[AR] = this

    type Command = CommandOf[AR]
    type Event = EventOf[AR]
    type Error = ErrorOf[AR]
    type EventsSeq = EventsSeqOf[AR]
    type CommandResult = CommandResultOf[AR]

    implicit def toRichEventsSeq(events: EventsSeq): RichEventsSeqOf[AR] = new RichEventsSeqOf[AR](events)

    implicit protected def ok(event: Event): CommandResult = Left(Seq(event))
    implicit protected def error(error: Error): CommandResult = Right(error)
}

object AggregateRootCompanion
{
    trait CommandOf[AR]
    trait EventOf[AR]
    trait ErrorOf[AR]

    type EventsSeqOf[AR] = Seq[EventOf[AR]]
    type CommandResultOf[AR] = Either[EventsSeqOf[AR], ErrorOf[AR]]
    type F[AR] = FactoryOf[AR]

    trait FactoryOf[AR]
    {
        def seed: AR
    }

    implicit class RichEventsSeqOf[AR <: AggregateRoot[AR]](val events: EventsSeqOf[AR]) extends AnyVal
    {
        def mkRoot()(implicit f: F[AR]): AR = events.foldLeft(f.seed)((ar, e) => ar(e))

        def !(command: CommandOf[AR])(implicit f: F[AR]): CommandResultOf[AR] = mkRoot ! command
    }
}