import com.de.scalasourcing.AggregateRoot
import org.scalatest.Matchers._

trait ScalaTestDDD[S] extends AggregateRoot[S]
{
    def given(events: Event*): FlowGiven =
    {
        FlowGiven(events toState)
    }

    case class FlowGiven
    (state: State)
    {
        /**
         * When
         */
        def ??(command: Command): FlowWhen =
        {
            FlowWhen(state ! command)
        }
    }

    case class FlowWhen(eventsTry: Sourcing)
    {
        /**
         * Then ok
         */
        def -->(expected: Event*) = eventsTry.left.get should equal(expected)

        /**
         * Then error
         */
        def !!!(expected: Error): Unit =
        {
            eventsTry.right.get should equal(expected)
        }
    }
}