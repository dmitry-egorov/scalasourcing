package com.scalasourcing.services

import com.scalasourcing.model.AggregateId
import com.scalasourcing.model.AggregateRootCompanion._

trait EventStorage
{
    def get[AR: Manifest](id: AggregateId): EventsSeqOf[AR]
    def persist[AR: Manifest](id: AggregateId, events: EventsSeqOf[AR]): Unit
}
