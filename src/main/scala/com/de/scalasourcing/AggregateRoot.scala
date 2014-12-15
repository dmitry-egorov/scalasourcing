package com.de.scalasourcing

import com.de.scalasourcing.AggregateRoot._

object AggregateRoot
{
    trait CommandOf[S]
    trait EventOf[S]
    trait ErrorOf[S]
    type EventsSeqOf[S] = Seq[EventOf[S]]
    type CommandResultOf[S] = Either[EventsSeqOf[S], ErrorOf[S]]
    type StateOf[S] = Option[S]

    trait CommandApplicationOf[S]
    {
        def apply(agg: StateOf[S], command: CommandOf[S]): CommandResultOf[S]
    }

    trait EventApplicationOf[S]
    {
        def apply(state: StateOf[S], event: EventOf[S]): StateOf[S]
        def apply(state: StateOf[S], events: EventsSeqOf[S]): StateOf[S] = events.foldLeft(state)((a, e) => apply(a, e))
        def apply(events: EventsSeqOf[S]): StateOf[S] = events.foldLeft(Option.empty[S])((a, e) => apply(a, e))
    }

    implicit class RichEventsSeqOf[S](val events: EventsSeqOf[S]) extends AnyVal
    {
        def toState()(implicit a: EventApplicationOf[S]): StateOf[S] = a(events)
        def !(events: EventsSeqOf[S], command: CommandOf[S])
             (implicit s: CommandApplicationOf[S], a: EventApplicationOf[S]): CommandResultOf[S] =
            events.toState ! command
    }

    implicit class RichStateOf[S](val state: StateOf[S]) extends AnyVal
    {
        def +(event: EventOf[S])(implicit a: EventApplicationOf[S]): StateOf[S] = a(state, event)
        def +(events: EventsSeqOf[S])(implicit a: EventApplicationOf[S]): StateOf[S] = a(state, events)
        def !(command: CommandOf[S])(implicit s: CommandApplicationOf[S]): CommandResultOf[S] = s(state, command)
        def !+(command: CommandOf[S])(implicit s: CommandApplicationOf[S], a: EventApplicationOf[S]): StateOf[S] =
            state + (state ! command).left.get
    }
}

trait AggregateRoot[S]
{
    def create: State = None

    implicit val ca: CommandApplication
    implicit val ea: EventApplication

    type Command = CommandOf[S]
    type Event = EventOf[S]
    type Error = ErrorOf[S]
    type EventsSeq = EventsSeqOf[S]
    type CommandResult = CommandResultOf[S]
    type State = StateOf[S]

    type CommandApplication = CommandApplicationOf[S]
    type EventApplication = EventApplicationOf[S]

    implicit def toRichEventsSeq(events: EventsSeq): RichEventsSeqOf[S] = events
    implicit def toRichState(state: State): RichStateOf[S] = state

    implicit protected def ok(e: S): State = Some(e)
    implicit protected def ok(e: Event): CommandResult = Left(Seq(e))
    implicit protected def error(e: Error): CommandResult = Right(e)
}
