package com.scalasourcing

import com.scalasourcing.AggregateRootCompanion._

class CommandExecutor(storage: EventStorage)
{
    def execute[AR <: AggregateRoot[AR] : F : Manifest](id: AggregateId, command: CommandOf[AR]): CommandResultOf[AR] =
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
