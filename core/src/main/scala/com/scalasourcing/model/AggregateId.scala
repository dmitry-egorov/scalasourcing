package com.scalasourcing.model

case class AggregateId(value: String)
{
    assert(!value.isEmpty)

    override def toString = value
}

object AggregateId
{
    implicit def from(s: String): AggregateId = AggregateId(s)
}