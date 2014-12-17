package com.scalasourcing.services

import com.scalasourcing.model._
import com.scalasourcing.model.AggregateRootCompanion._

class CommandExecutor(storage: EventStorage)
{
    def execute[AR <: AggregateRoot[AR] : Factory : Manifest](id: AggregateId, command: CommandOf[AR]): CommandResultOf[AR] =
    {
        val events = storage.get[AR](id)

        val result = events ! command

        result match
        {
            case Left(newEvents) => storage.persist(id, newEvents)
            case _               =>
        }

        result
    }
}
