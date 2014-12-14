package scalasourcing.scalasourcing

trait Aggregation[A, E, -C]
    extends Sourcing[A, E, C]
    with Application[A, E]
    with EventProcessor[E]