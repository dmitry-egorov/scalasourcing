package com.de.scalasourcing

import com.de.scalasourcing.AggregateRoot._

trait AggregateRoot[S] extends CommandApplicationOf[S] with EventApplicationOf[S]
{
    implicit val ca: CA[S] = this
    implicit val ea: EA[S] = this

    type Command = CommandOf[S]
    type Event = EventOf[S]
    type Error = ErrorOf[S]
    type EventsSeq = EventsSeqOf[S]
    type CommandResult = CommandResultOf[S]
    type State = StateOf[S]

    implicit def toRichEventsSeq(events: EventsSeq): RichEventsSeqOf[S] = new RichEventsSeqOf[S](events)
    implicit def toRichState(state: State): RichStateOf[S] = new RichStateOf[S](state)

    implicit protected def ok(state: S): State = Some(state)
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
    type StateOf[S] = Option[S]

    type CA[S] = CommandApplicationOf[S]
    type EA[S] = EventApplicationOf[S]

    trait CommandApplicationOf[S]
    {
        def apply(state: StateOf[S], command: CommandOf[S]): CommandResultOf[S]
    }

    trait EventApplicationOf[S]
    {
        def apply(state: StateOf[S], event: EventOf[S]): StateOf[S]
    }

    implicit class RichEventsSeqOf[S](val events: EventsSeqOf[S]) extends AnyVal
    {
        def toState()(implicit ea: EA[S]): StateOf[S] = events.foldLeft(Option.empty[S])((s, e) => ea(s, e))

        def !(events: EventsSeqOf[S], command: CommandOf[S])(implicit ca: CA[S], ea: EA[S]): CommandResultOf[S] = toState ! command
    }

    implicit class RichStateOf[S](val state: StateOf[S]) extends AnyVal
    {
        def append(event: EventOf[S])(implicit ea: EA[S]): StateOf[S] = ea(state, event)
        def append(events: EventsSeqOf[S])(implicit ea: EA[S]): StateOf[S] = events.foldLeft(state)((s, e) => ea(s, e))
        def execute(command: CommandOf[S])(implicit ca: CA[S]): CommandResultOf[S] = ca(state, command)
        def appendResultOf(command: CommandOf[S])(implicit ca: CA[S], ea: EA[S]): StateOf[S] =
            state + (state ! command).left.get

        def +(event: EventOf[S])(implicit ea: EA[S]): StateOf[S] = append(event)
        def +(events: EventsSeqOf[S])(implicit ea: EA[S]): StateOf[S] = append(events)
        def !(command: CommandOf[S])(implicit ca: CA[S]): CommandResultOf[S] = execute(command)
        def +!(command: CommandOf[S])(implicit ca: CA[S], ea: EA[S]): StateOf[S] = appendResultOf(command)
    }
}
