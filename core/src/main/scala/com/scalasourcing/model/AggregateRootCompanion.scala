package com.scalasourcing.model

import com.scalasourcing.model.AggregateRootCompanion._

trait AggregateRootCompanion[AR <: AggregateRoot[AR]] extends Factory[AR]
{
    implicit val factory: Factory[AR] = this

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

    trait Factory[AR]
    {
        def seed: AR
    }

    implicit class RichEventsSeqOf[AR <: AggregateRoot[AR]](val events: EventsSeqOf[AR]) extends AnyVal
    {
        def mkRoot()(implicit f: Factory[AR]): AR = events.foldLeft(f.seed)((ar, e) => ar(e))

        def !(command: CommandOf[AR])(implicit f: Factory[AR]): CommandResultOf[AR] = mkRoot ! command
    }
}
