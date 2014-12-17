package com.scalasourcing.backend.memory

import com.scalasourcing.backend.EventStorage
import com.scalasourcing.model.Aggregate._
import com.scalasourcing.model._

class SingleThreadInMemoryEventStorage extends EventStorage
{
    private var aggregatesEventsMap: Map[String, Map[AggregateId, Seq[AnyRef]]] = Map.empty
    private var subscribersMap: Map[String, Seq[AnyRef => Unit]] = Map.empty

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

        subscribersMap
        .get(clazz)
        .map(subs => events.map(e => subs.foreach(s => s(e))))
    }

    def subscribe[AR: Manifest](f: EventOf[AR] => Unit): () => Unit =
    {
        val clazz = getClassName
        val callback: (AnyRef) => Unit = e => f(e.asInstanceOf[EventOf[AR]])

        val subs = subscribersMap.getOrElse(clazz, Seq.empty)
        val newSubs = subs ++ Seq(callback)
        subscribersMap = subscribersMap.updated(clazz, newSubs)

        () =>
        {
            val subs = subscribersMap.getOrElse(clazz, Seq.empty)
            val newSubs = subs.filter(i => i != callback)
            subscribersMap = subscribersMap.updated(clazz, newSubs)
        }
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
