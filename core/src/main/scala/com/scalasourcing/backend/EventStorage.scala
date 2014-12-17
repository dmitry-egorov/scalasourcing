package com.scalasourcing.backend

import com.scalasourcing.model.Aggregate._
import com.scalasourcing.model._

trait EventStorage
{
    def get[AR: Manifest](id: AggregateId): EventsSeqOf[AR]
    def persist[AR: Manifest](id: AggregateId, events: EventsSeqOf[AR]): Unit
    def subscribe[AR: Manifest](f: EventOf[AR] => Unit): () => Unit
}