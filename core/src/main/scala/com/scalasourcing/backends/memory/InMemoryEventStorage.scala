package com.scalasourcing.backends.memory

import com.scalasourcing.model.AggregateId
import com.scalasourcing.model.AggregateRootCompanion.EventsSeqOf
import com.scalasourcing.services.EventStorage

class InMemoryEventStorage extends EventStorage
{
    private var aggregatesEventsMap: Map[String, Map[AggregateId, Seq[AnyRef]]] = Map.empty

    def get[AR: Manifest](id: AggregateId): EventsSeqOf[AR] =
    {
        val clazz = getClassName
        val eventsMap = getEventsMap(clazz)
        val events = getEventsSeq(id, eventsMap)

        events.asInstanceOf[EventsSeqOf[AR]]
    }

    def persist[AR: Manifest](id: AggregateId, events: EventsSeqOf[AR]): Unit =
    {
        val clazz = getClassName
        val eventsMap = getEventsMap(clazz)
        val eventsSeq = getEventsSeq(id, eventsMap)

        val newEventsSeq = eventsSeq ++ events
        val newEventsMap = eventsMap.updated(id, newEventsSeq)
        aggregatesEventsMap = aggregatesEventsMap.updated(clazz, newEventsMap)
    }

    private def getClassName[T: Manifest]: String =
    {
        implicitly[Manifest[T]].getClass.getName
    }

    private def getEventsMap(clazz: String): Map[AggregateId, Seq[AnyRef]] =
    {
        aggregatesEventsMap.getOrElse(clazz, Map.empty)
    }

    private def getEventsSeq(id: AggregateId, eventsMap: Map[AggregateId, Seq[AnyRef]]): Seq[AnyRef] =
    {
        eventsMap.getOrElse(id, Seq.empty)
    }
}
