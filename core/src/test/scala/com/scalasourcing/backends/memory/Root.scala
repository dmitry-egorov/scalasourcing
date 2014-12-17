package com.scalasourcing.backends.memory

import com.scalasourcing.model.{AggregateRoot, AggregateRootCompanion}

trait Root extends AggregateRoot[Root]

object Root extends AggregateRootCompanion[Root]
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
