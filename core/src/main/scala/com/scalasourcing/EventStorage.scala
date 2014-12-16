package com.scalasourcing

import com.scalasourcing.AggregateFactory.EventsSeqOf

class EventStorage
{
    private var aggregatesEventsMap: Map[String, Map[AggregateId, Seq[AnyRef]]] = Map.empty

    def get[AR](id: AggregateId)(implicit m: Manifest[AR]): EventsSeqOf[AR] =
    {
        val clazz = getClassName(m)
        val eventsMap = getEventsMap(clazz)
        val events = getEventsSeq(id, eventsMap)

        events.asInstanceOf[EventsSeqOf[AR]]
    }

    def persist[AR](id: AggregateId, events: EventsSeqOf[AR])(implicit m: Manifest[AR]): Unit =
    {
        val clazz = getClassName(m)
        val eventsMap = getEventsMap(clazz)
        val eventsSeq = getEventsSeq(id, eventsMap)
        
        val newEventsSeq = eventsSeq ++ events
        val newEventsMap = eventsMap.updated(id, newEventsSeq)
        aggregatesEventsMap = aggregatesEventsMap.updated(clazz, newEventsMap)
    }

    private def getClassName[T](m: Manifest[T]): String =
    {
        m.getClass.getName
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
