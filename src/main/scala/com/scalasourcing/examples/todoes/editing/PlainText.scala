package com.scalasourcing.examples.todoes.editing

case class PlainText(value: String)
{
    lazy val isEmpty = value.trim.isEmpty

    override def toString = value
}

object PlainText
{
    implicit def from(s: String): PlainText = PlainText(s)
}
