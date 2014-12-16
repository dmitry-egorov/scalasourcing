package com.scalasourcing

import com.scalasourcing.AggregateFactory.EventsSeqOf

class EventStorage
{
    private var aggregatesEventsMap: Map[String, Map[AggregateId, Seq[AnyRef]]] = Map.empty

    def get[S](id: AggregateId)(implicit m: Manifest[S]): EventsSeqOf[S] =
    {
        val clazz = getClassName(m)
        val eventsMap = getEventsMap(clazz)
        val events = getEventsSeq(id, eventsMap)

        events.asInstanceOf[EventsSeqOf[S]]
    }

    def persist[S](id: AggregateId, events: EventsSeqOf[S])(implicit m: Manifest[S]): Unit =
    {
        val clazz = getClassName(m)
        val eventsMap = getEventsMap(clazz)
        val eventsSeq = getEventsSeq(id, eventsMap)
        
        val newEventsSeq = eventsSeq ++ events
        val newEventsMap = eventsMap.updated(id, newEventsSeq)
        aggregatesEventsMap = aggregatesEventsMap.updated(clazz, newEventsMap)
    }

    private def getClassName[S](m: Manifest[S]): String =
    {
        m.getClass.getName
    }

    private def getEventsMap[S](clazz: String): Map[AggregateId, Seq[AnyRef]] =
    {
        aggregatesEventsMap.getOrElse(clazz, Map.empty)
    }

    private def getEventsSeq[S](id: AggregateId, eventsMap: Map[AggregateId, Seq[AnyRef]]): Seq[AnyRef] =
    {
        eventsMap.getOrElse(id, Seq.empty)
    }
}
