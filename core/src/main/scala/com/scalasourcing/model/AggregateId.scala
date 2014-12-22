package com.scalasourcing.model

trait AggregateId extends Product
{
    def value: String
    override def toString = value
}