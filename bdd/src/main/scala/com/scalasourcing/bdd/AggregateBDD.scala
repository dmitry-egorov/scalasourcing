package com.scalasourcing.bdd

import com.scalasourcing.model.Aggregate._
import com.scalasourcing.model._

trait AggregateBDD[AR <: AggregateRoot[AR]]
{
    def given(implicit f: Factory[AR]): EmptyFlowGiven = EmptyFlowGiven()
    def given_nothing(implicit f: Factory[AR]): FlowGiven = FlowGiven(f.seed)

    case class EmptyFlowGiven()(implicit f: Factory[AR])
    {
        def it_was(events: EventOf[AR]*): FlowGiven = FlowGiven(events mkRoot)
        def nothing = FlowGiven(f.seed)
    }

    case class FlowGiven(state: AR)
    {
        def and(events: EventOf[AR]*): FlowGiven = FlowGiven(state + events)
        def when_I(command: CommandOf[AR]): FlowWhen = FlowWhen(state ! command)
    }

    case class FlowWhen(eventsTry: CommandResultOf[AR])
    {
        def then_it_is(expected: EventOf[AR]*) =
        {
            eventsTry.fold(
                events => assert(events == expected, s"Invalid events produced. Expected: $expected. Actual: $events"),
                error => assert(assertion = false, s"Expected events: $expected, but was error: $error")
            )
        }

        def then_expect(expected: ErrorOf[AR]): Unit =
        {
            eventsTry.fold(
                events => assert(assertion = false, s"Expected error $expected, but was events: $events"),
                error => assert(error == expected, s"Invalid error produced. Expected: $expected. Actual: $error")
            )
        }
    }
}