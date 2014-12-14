package scalasourcing.scalasourcing

trait EventProcessor[E]
{
    def ok(events: E*): Seq[E] = events
}
