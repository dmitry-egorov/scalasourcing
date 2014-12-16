package com.scalasourcing

import com.scalasourcing.AggregateRoot._

trait AggregateRoot[S] extends CommandApplicationOf[S] with EventApplicationOf[S]
{
    implicit val ca: CA[S] = this
    implicit val ea: EA[S] = this

    type Command = CommandOf[S]
    type Event = EventOf[S]
    type Error = ErrorOf[S]
    type EventsSeq = EventsSeqOf[S]
    type CommandResult = CommandResultOf[S]

    implicit def toRichEventsSeq(events: EventsSeq): RichEventsSeqOf[S] = new RichEventsSeqOf[S](events)
    implicit def toRichStateOption(state: Option[S]): RichStateOptionOf[S] = new RichStateOptionOf[S](state)

    implicit protected def ok(state: S): Option[S] = Some(state)
    implicit protected def ok(event: Event): CommandResult = Left(Seq(event))
    implicit protected def error(error: Error): CommandResult = Right(error)
}

object AggregateRoot
{
    trait CommandOf[S]
    trait EventOf[S]
    trait ErrorOf[S]

    type EventsSeqOf[S] = Seq[EventOf[S]]
    type CommandResultOf[S] = Either[EventsSeqOf[S], ErrorOf[S]]

    type CA[S] = CommandApplicationOf[S]
    type EA[S] = EventApplicationOf[S]

    trait CommandApplicationOf[S]
    {
        def apply(state: Option[S], command: CommandOf[S]): CommandResultOf[S]
    }

    trait EventApplicationOf[S]
    {
        def apply(state: Option[S], event: EventOf[S]): Option[S]
    }

    implicit class RichEventsSeqOf[S](val events: EventsSeqOf[S]) extends AnyVal
    {
        def toState()(implicit ea: EA[S]): Option[S] = events.foldLeft(Option.empty[S])((s, e) => ea(s, e))

        def !(command: CommandOf[S])(implicit ca: CA[S], ea: EA[S]): CommandResultOf[S] = toState ! command
    }

    implicit class RichStateOptionOf[S](val state: Option[S]) extends AnyVal
    {
        def append(event: EventOf[S])(implicit ea: EA[S]): Option[S] = ea(state, event)
        def append(events: EventsSeqOf[S])(implicit ea: EA[S]): Option[S] = events.foldLeft(state)((s, e) => ea(s, e))
        def execute(command: CommandOf[S])(implicit ca: CA[S]): CommandResultOf[S] = ca(state, command)
        def appendResultOf(command: CommandOf[S])(implicit ca: CA[S], ea: EA[S]): Option[S] =
            state + (state ! command).left.get

        def +(event: EventOf[S])(implicit ea: EA[S]): Option[S] = append(event)
        def +(events: EventsSeqOf[S])(implicit ea: EA[S]): Option[S] = append(events)
        def !(command: CommandOf[S])(implicit ca: CA[S]): CommandResultOf[S] = execute(command)
        def +!(command: CommandOf[S])(implicit ca: CA[S], ea: EA[S]): Option[S] = appendResultOf(command)
    }
}
