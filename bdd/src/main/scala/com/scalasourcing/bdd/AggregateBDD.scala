package com.scalasourcing.bdd

import com.scalasourcing.AggregateFactory._
import com.scalasourcing.AggregateRoot

trait AggregateBDD[S <: AggregateRoot[S]]
{
    def given: EmptyFlowGiven = EmptyFlowGiven()
    def given_nothing(implicit f: F[S]): FlowGiven = FlowGiven(f.create)

    case class EmptyFlowGiven()
    {
        def it_was(events: EventOf[S]*)(implicit f: F[S]): FlowGiven = FlowGiven(events toState)
        def nothing(implicit f: F[S]) = FlowGiven(f.create)
    }

    case class FlowGiven(state: S)
    {
        def and(events: EventOf[S]*): FlowGiven = FlowGiven(state + events)

        def when_I(command: CommandOf[S]): FlowWhen = FlowWhen(state ! command)
    }

    case class FlowWhen(eventsTry: CommandResultOf[S])
    {
        def then_it_is(expected: EventOf[S]*) =
        {
            val events = eventsTry.left.get
            assert(events == expected, s"Invalid events produced. Expected: $expected. Actual: $events")
        }

        def then_expect(expected: ErrorOf[S]): Unit =
        {
            val error = eventsTry.right.get
            assert(error == expected, s"Invalid error produced. Expected: $expected. Actual: $error")
        }
    }
}