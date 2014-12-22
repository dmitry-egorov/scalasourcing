package com.scalasourcing.backend.memory

import com.scalasourcing.backend.EventStorage
import com.scalasourcing.model.Aggregate._
import com.scalasourcing.model._

import scala.concurrent.{Future, ExecutionContext}

class SingleThreadInMemoryEventStorage(implicit val ec: ExecutionContext) extends EventStorage
{
    private var aggregatesEventsMap: Map[String, Map[AggregateId, Seq[AnyRef]]] = Map.empty

    def get[AR: Manifest](id: AggregateId): Future[EventsSeqOf[AR]] =
    {
        val clazz = getClassName
        val eventsMap = getEventsMap(clazz)
        val events = getEventsSeq(id, eventsMap)

        Future.successful(events.asInstanceOf[EventsSeqOf[AR]])
    }

    def tryPersist[AR: Manifest](id: AggregateId, events: EventsSeqOf[AR], expectedVersion: Int): Future[Boolean] =
    {
        val clazz = getClassName
        val eventsMap = getEventsMap(clazz)
        val eventsSeq = getEventsSeq(id, eventsMap)

        val newEventsSeq = eventsSeq ++ events
        val newEventsMap = eventsMap.updated(id, newEventsSeq)
        aggregatesEventsMap = aggregatesEventsMap.updated(clazz, newEventsMap)

        Future.successful(true)
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
