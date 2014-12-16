package com.scalasourcing

import com.scalasourcing.AggregateFactory._

class CommandExecutor(storage: EventStorage)
{
    def execute[AR <: AggregateRoot[AR]](id: AggregateId, command: CommandOf[AR])(implicit f: FactoryOf[AR], m: Manifest[AR]): CommandResultOf[AR] =
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
