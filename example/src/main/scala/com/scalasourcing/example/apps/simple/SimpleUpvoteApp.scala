package com.scalasourcing.example.apps.simple

import com.scalasourcing.example.domain.voting.Upvote
import com.scalasourcing.example.domain.voting.Upvote._
import com.scalasourcing.model.AggregateRootCompanion.CommandResultOf

object SimpleUpvoteApp extends App
{
    var upvoted = factory.seed +!! Cast()

    println(readable(upvoted._2))

    val cancelled = upvoted._1 ! Cancel()

    println(readable(cancelled))

    println("Thank you for using our beautiful app!")

    def readable(result: CommandResultOf[Upvote]): Any =
    {
        result.fold(events => events.map
                              {
                                  case Casted()    => s"Upvote casted."
                                  case Cancelled() => s"Upvote cancelled."
                              }.mkString(", "),
                    error => "Unexpected error!"
        )
    }
}
