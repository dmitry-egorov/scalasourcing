package scalasourcing.scalasourcing

trait Sourcing[-A, +E, -C]
{
    def order(agg: Option[A], command: C): Seq[E]
}
