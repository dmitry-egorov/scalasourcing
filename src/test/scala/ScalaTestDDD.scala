import com.de.scalasourcing._
import Operations._
import org.scalatest.Matchers._

import scala.reflect.Manifest
import scala.util.Try

trait ScalaTestDDD
{
    def given[A, E](events: E*)(implicit ea: Applicator[A, E]): FlowGiven[A] =
    {
        FlowGiven(events toState)
    }

    case class FlowGiven[A]
    (state: Option[A])
    {
        def ??[E, C](command: C)(implicit es: Sourcer[A, E, C]): FlowWhen[E] =
            FlowWhen(state !! command)
    }

    case class FlowWhen[E]
    (eventsTry: Try[Seq[E]])
    {
        def -->(expected: E*) = eventsTry.get should equal(expected)

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