package com.scalasourcing.model

trait CompositeAggregateId extends AggregateId
{
    def ids: Seq[AggregateId]
    def value = ids.map(_.value).mkString("+")
}
