package com.scalasourcing.model

import com.scalasourcing.model.AggregateRootCompanion._

trait AggregateRoot[AR <: AggregateRoot[AR]]
{
    self: AR =>

    def apply(event: EventOf[AR]): AR
    def apply(command: CommandOf[AR]): CommandResultOf[AR]

    def apply(events: EventsSeqOf[AR]): AR = events.foldLeft(self)((ar, e) => ar(e))
    def append(event: EventOf[AR]): AR = self(event)
    def append(events: EventsSeqOf[AR]): AR = self(events)
    def execute(command: CommandOf[AR]): CommandResultOf[AR] = self(command)
    def appendResultOf(command: CommandOf[AR]): AR = self + (self ! command).left.get

    def +(event: EventOf[AR]): AR = self(event)
    def +(events: EventsSeqOf[AR]): AR = self(events)
    def !(command: CommandOf[AR]): CommandResultOf[AR] = self(command)
    def +!(command: CommandOf[AR]): AR = appendResultOf(command)
}
