import com.de.scalasourcing.EventSourcing._
import org.scalatest.Matchers._

import scala.reflect.Manifest
import scala.util.Try

trait ScalaTestDDD
{
    def given[S](events: EventOf[S]*)(implicit ea: Applicator[S]): FlowGiven[S] =
    {
        FlowGiven(events toState)
    }

    case class FlowGiven[S]
    (state: Option[S])
    {
        def ??(command: CommandOf[S])(implicit es: Sourcer[S]): FlowWhen[S] =
            FlowWhen(state !! command)
    }

    case class FlowWhen[S](eventsTry: Try[Seq[EventOf[S]]])
    {
        def -->(expected: EventOf[S]*) = eventsTry.get should equal(expected)

        def !!![T <: AnyRef](implicit m: Manifest[T]): T =
            intercept[T]
            {
                eventsTry.get
            }

        def !!![T <: AnyRef](expected: T)(implicit m: Manifest[T]): Unit =
        {
            !!![T] should equal(expected)
        }
    }
}