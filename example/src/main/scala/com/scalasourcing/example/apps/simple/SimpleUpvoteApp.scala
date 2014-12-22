package com.scalasourcing.example.apps.simple

import com.scalasourcing.backend.memory.SingleThreadInMemoryEventStorage
import com.scalasourcing.example.domain.voting.Upvote
import com.scalasourcing.example.domain.voting.Upvote._

import scala.concurrent.duration._
import scala.concurrent._

object SimpleUpvoteApp extends App
{
    implicit val ec = ExecutionContext.Implicits.global
    val eventStorage = new SingleThreadInMemoryEventStorage
    {val a = Upvote}

    val id = "1"
    val f1 = eventStorage.execute(id, Cast()).map(print)
    val f2 = eventStorage.execute(id, Cancel()).map(print)

    Await.ready(Future.sequence(Seq(f1, f2)), 1 second)

    println("Thank you for using our beautiful app!")

    def print(result: CommandResult): Unit =
    {
        val readable = result match
        {
            case Left(Casted() :: Nil)    => s"Upvote casted."
            case Left(Cancelled() :: Nil) => s"Upvote cancelled."
            case Right(e)                 => s"Error $e."
            case _                        => s"Unknown result"
        }

        println(readable)
    }
}
