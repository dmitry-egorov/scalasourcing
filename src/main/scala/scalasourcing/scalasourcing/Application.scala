package scalasourcing.scalasourcing

trait Application[A, -E]
{
    def applyEvent(agg: Option[A], event: E): Option[A]

    def applyEvents(agg: Option[A], events: Seq[E]): Option[A] =
        events.foldLeft(agg)((a, e) => applyEvent(a, e))

    def applyEvents(events: Seq[E]): Option[A] =
        events.foldLeft(Option.empty[A])((a, e) => applyEvent(a, e))
}
