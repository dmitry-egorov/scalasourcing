package com.de.scalasourcing

trait Sourcer[-S, +E, -C]
{
    def apply(agg: Option[S], command: C): Seq[E]
}
