import com.de.scalasourcing.AggregateRoot._
import org.scalatest.Matchers._

trait AggregateBDD[S]
{
    def given: EmptyFlowGiven = EmptyFlowGiven()
    def given_nothing: FlowGiven = FlowGiven(None)

    case class EmptyFlowGiven()
    {
        def it_is(events: EventOf[S]*)(implicit a: EventApplicationOf[S]): FlowGiven = FlowGiven(events toState)
        def nothing = FlowGiven(None)
    }

    case class FlowGiven(state: StateOf[S])
    {
        def and(events: EventOf[S]*)(implicit a: EventApplicationOf[S]): FlowGiven = FlowGiven(state + events)
        /**
         * When
         */
        def when_I(command: CommandOf[S])(implicit s: CommandApplicationOf[S]): FlowWhen = FlowWhen(state ! command)
    }

    case class FlowWhen(eventsTry: CommandResultOf[S])
    {
        /**
         * Then ok
         */
        def then_it_is(expected: EventOf[S]*) = eventsTry.left.get should equal(expected)

        /**
         * Then error
         */
        def then_error(expected: ErrorOf[S]): Unit =
        {
            eventsTry.right.get should equal(expected)
        }
    }
}