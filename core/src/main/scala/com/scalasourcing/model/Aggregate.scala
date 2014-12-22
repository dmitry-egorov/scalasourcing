package com.scalasourcing.model

trait Aggregate
{
    def seed: State

    type Id <: AggregateId
    trait Command extends AggregateCommand
    trait Event extends AggregateEvent
    trait Error extends AggregateError
    type EventsSeq = Seq[Event]
    type CommandResult = Either[EventsSeq, Error]

    implicit protected def ok(event: Event): CommandResult = Left(Seq(event))
    implicit protected def error(error: Error): CommandResult = Right(error)

    trait State
    {
        def apply(event: Event): State
        def apply(command: Command): CommandResult

        def append(event: Event): State = apply(event)
        def append(events: EventsSeq): State = events.foldLeft(this)((ar, e) => ar + e)
        def append(result: CommandResult): State = result.fold(events => append(events), error => this)
        def execute(command: Command): CommandResult = apply(command)
        def appendResultOf(command: Command): State = append(execute(command))

        def +(event: Event) = append(event)
        def +(events: EventsSeq) = append(events)
        def +(result: CommandResult) = append(result)
        def !(command: Command) = execute(command)
        def +!(command: Command) = appendResultOf(command)
    }
}

object Aggregate
{
    type AggregateEventsSeq = Seq[AggregateEvent]
    type AggregateCommandResult = Either[AggregateEventsSeq, AggregateError]
}
