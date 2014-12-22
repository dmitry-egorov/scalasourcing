package com.scalasourcing.backend.memory

import com.scalasourcing.model.{AggregateId, Aggregate}

case class TesterId(value: String) extends AggregateId

object Tester extends Aggregate
{
    type Id = TesterId

    case class SomethingHappened() extends Event
    case class DoSomething() extends Command

    case class TesterState() extends State
    {
        def apply(event: Event) = TesterState()
        def apply(command: Command) = SomethingHappened()
    }

    def seed = TesterState()
}

