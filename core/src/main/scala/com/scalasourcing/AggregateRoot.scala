package com.scalasourcing

import com.scalasourcing.AggregateFactory._

trait AggregateRoot[S <: AggregateRoot[S]]
{
    self: S =>

    def apply(event: EventOf[S]): S
    def apply(command: CommandOf[S]): CommandResultOf[S]

    def append(events: EventsSeqOf[S]): S = events.foldLeft(self)((s, e) => s(e))
    def appendResultOf(command: CommandOf[S]): S = self + (self ! command).left.get

    def +(event: EventOf[S]): S = self(event)
    def +(events: EventsSeqOf[S]): S = append(events)
    def !(command: CommandOf[S]): CommandResultOf[S] = self(command)
    def +!(command: CommandOf[S]): S = appendResultOf(command)
}
