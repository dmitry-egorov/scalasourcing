package com.scalasourcing.backend

import com.scalasourcing.model.Aggregate._
import com.scalasourcing.model._

trait CommandsExecutor extends EventStorage
{
    def execute[AR <: AggregateRoot[AR] : Factory : Manifest](id: AggregateId, command: CommandOf[AR]): CommandResultOf[AR] =
    {
        val events = get[AR](id)

        val result = events ! command

        result match
        {
            case Left(newEvents) => persist(id, newEvents)
            case _               =>
        }

        result
    }
}
