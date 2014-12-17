package com.scalasourcing.model

import com.scalasourcing.model.Aggregate._

trait AggregateRoot[AR <: AggregateRoot[AR]]
{
    self: AR =>

    def apply(event: EventOf[AR]): AR
    def apply(command: CommandOf[AR]): CommandResultOf[AR]

    def apply(events: EventsSeqOf[AR]): AR = events.foldLeft(self)((ar, e) => ar(e))
    def apply(result: CommandResultOf[AR]): AR = result.fold(events => self(events), error => self)
    def append(event: EventOf[AR]): AR = self(event)
    def append(events: EventsSeqOf[AR]): AR = self(events)
    def execute(command: CommandOf[AR]): CommandResultOf[AR] = self(command)
    def appendResultOf(command: CommandOf[AR]): AR = self(self ! command)
    def stateAndResultOf(command: CommandOf[AR]): StateAndResultOf[AR] =
    {
        val result = self(command)
        StateAndResultOf(self(result), result)
    }

    def +(event: EventOf[AR]) = self(event)
    def +(events: EventsSeqOf[AR]) = self(events)
    def +(result: CommandResultOf[AR]) = self(result)
    def !(command: CommandOf[AR]) = self(command)
    def +!(command: CommandOf[AR]) = appendResultOf(command)
    def +!!(command: CommandOf[AR]) = stateAndResultOf(command)
}

