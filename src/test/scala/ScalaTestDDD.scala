import com.de.scalasourcing.EventSourcing._
import org.scalatest.Matchers._

trait ScalaTestDDD
{
    def given[S](events: EventOf[S]*)(implicit ea: Applicator[S]): FlowGiven[S] =
    {
        FlowGiven(events toState)
    }

    case class FlowGiven[S]
    (state: Option[S])
    {
        /**
         * When
         */
        def ??(command: CommandOf[S])(implicit es: Sourcer[S]): FlowWhen[S] =
        {
            FlowWhen(state ! command)
        }
    }

    case class FlowWhen[S](eventsTry: Sourcing[S])
    {
        /**
         * Then ok
         */
        def -->(expected: EventOf[S]*) = eventsTry.left.get should equal(expected)

        /**
         * Then error
         */
        def !!!(expected: ErrorOf[S]): Unit =
        {
            eventsTry.right.get should equal(expected)
        }
    }
}