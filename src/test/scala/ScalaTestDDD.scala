import scalasourcing.scalasourcing.Operations._
import org.scalatest.Matchers._

import scala.reflect.Manifest
import scala.util.Try
import scalasourcing.scalasourcing._

trait ScalaTestDDD
{
    def given[A, E](initialEvents: E*)(implicit ea: Application[A, E]): FlowGiven[A] =
    {
        FlowGiven(initialEvents toAggregate())
    }

    case class FlowGiven[A]
    (state: Option[A])
    {
        def ??[E, C](command: C)(implicit es: Sourcing[A, E, C]): FlowWhen[E] =
            FlowWhen(state !!! command)
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