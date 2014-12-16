package com.scalasourcing

import com.scalasourcing.AggregateFactory._

class CommandExecutor(storage: EventStorage)
{
    def execute[S <: AggregateRoot[S]](id: AggregateId, command: CommandOf[S])(implicit f: F[S], m: Manifest[S]): CommandResultOf[S] =
    {
        val events = storage.get[S](id)

        val result = events ! command

        result match
        {
            case Left(newEvents) => storage.persist(id, newEvents)
            case _               =>
        }

        result
    }
}
