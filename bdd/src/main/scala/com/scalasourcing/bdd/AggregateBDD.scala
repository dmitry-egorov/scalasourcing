package com.scalasourcing.bdd

import com.scalasourcing.AggregateRoot._

trait AggregateBDD[S]
{
    def given: EmptyFlowGiven = EmptyFlowGiven()
    def given_nothing: FlowGiven = FlowGiven(None)

    case class EmptyFlowGiven()
    {
        def it_was(events: EventOf[S]*)(implicit ea: EventApplicationOf[S]): FlowGiven = FlowGiven(events toState)
        def nothing = FlowGiven(None)
    }

    case class FlowGiven(state: Option[S])
    {
        def and(events: EventOf[S]*)(implicit ea: EventApplicationOf[S]): FlowGiven = FlowGiven(state + events)

        def when_I(command: CommandOf[S])(implicit ca: CommandApplicationOf[S]): FlowWhen = FlowWhen(state ! command)
    }

    case class FlowWhen(eventsTry: CommandResultOf[S])
    {
        def then_it_is(expected: EventOf[S]*) =
        {
            val events = eventsTry.left.get
            assert(events == expected, s"Invalid events produced. Expected: $expected. Actual: $events")
        }

        def then_error(expected: ErrorOf[S]): Unit =
        {
            val error = eventsTry.right.get
            assert(error == expected, s"Invalid error produced. Expected: $expected. Actual: $error")
        }
    }
}