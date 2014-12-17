package com.scalasourcing.backend.memory

import com.scalasourcing.model.{AggregateRoot, Aggregate}

trait Root extends AggregateRoot[Root]

object Root extends Aggregate[Root]
{
    case class RootEvent() extends Event
    case class RootCommand() extends Command

    case class SimpleRoot() extends Root
    {
        def apply(event: Event) = SimpleRoot()
        def apply(command: Command) = RootEvent()
    }

    def seed = SimpleRoot()
}
