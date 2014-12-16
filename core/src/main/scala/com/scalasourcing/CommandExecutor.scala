package com.scalasourcing

import com.scalasourcing.AggregateRoot._

class CommandExecutor
{
    private val storage = new EventStorage

    def execute[S](id: AggregateId, command: CommandOf[S])(implicit ea: EA[S], ca: CA[S], m: Manifest[S]): CommandResultOf[S] =
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
