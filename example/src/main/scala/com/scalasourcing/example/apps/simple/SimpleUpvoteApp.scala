package com.scalasourcing.example.apps.simple

import com.scalasourcing.backend.CommandsExecutor
import com.scalasourcing.backend.memory.SingleThreadInMemoryEventStorage
import com.scalasourcing.example.domain.voting.Upvote._

object SimpleUpvoteApp extends App
{
    val eventStorage = new SingleThreadInMemoryEventStorage with CommandsExecutor
    eventStorage.subscribe(print)

    val id = "1"
    eventStorage.execute(id, Cast())
    eventStorage.execute(id, Cancel())

    println("Thank you for using our beautiful app!")

    def print(result: Event): Unit =
    {
        val readable = result match
        {
            case Casted()    => s"Upvote casted."
            case Cancelled() => s"Upvote cancelled."
        }

        println(readable)
    }
}
