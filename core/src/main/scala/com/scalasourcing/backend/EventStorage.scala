package com.scalasourcing.backend

import com.scalasourcing.model.Aggregate._
import com.scalasourcing.model._

import scala.concurrent.{ExecutionContext, Future}

trait EventStorage
{
    type AR <: AggregateRoot[AR]

    implicit val f: Factory[AR]
    implicit val m: Manifest[AR]
    implicit val ec: ExecutionContext

    def get(id: AggregateId): Future[EventsSeqOf[AR]]
    def tryPersist(id: AggregateId, events: EventsSeqOf[AR], expectedVersion: Int): Future[Boolean]

    def persist(id: AggregateId, events: EventsSeqOf[AR], expectedVersion: Int): Future[Unit] =
    {
        tryPersist(id, events, expectedVersion)
        .flatMap(
                committed =>
                    if (committed) Future.successful(())
                    else persist(id, events, expectedVersion)
            )
    }

    def execute(id: AggregateId, command: CommandOf[AR]): Future[CommandResultOf[AR]] =
    {
        tryExecute(id, command)
        .flatMap(
                result =>
                    if (result.isDefined) Future.successful(result.get)
                    else execute(id, command)
            )
    }

    def tryExecute(id: AggregateId, command: CommandOf[AR]): Future[Option[CommandResultOf[AR]]] =
    {
        for
        {
            events <- get(id)
            result = events ! command
            persisted <- result match
            {
                case Left(newEvents) => tryPersist(id, newEvents, events.length)
                case _               => Future.successful(true)
            }
        }
        yield if (persisted) Some(result) else None
    }
}