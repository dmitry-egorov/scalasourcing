package com.de.scalasourcing

trait Applicator[S, -E]
{
    def apply(state: Option[S], event: E): Option[S]

    def apply(state: Option[S], events: Seq[E]): Option[S] =
        events.foldLeft(state)((a, e) => apply(a, e))

    def apply(events: Seq[E]): Option[S] =
        events.foldLeft(Option.empty[S])((a, e) => apply(a, e))
}