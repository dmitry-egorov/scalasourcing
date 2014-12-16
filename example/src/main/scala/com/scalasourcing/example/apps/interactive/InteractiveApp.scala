package com.scalasourcing.example.apps.interactive

import com.scalasourcing.AggregateFactory._
import com.scalasourcing._
import com.scalasourcing.example.editing.Todo._
import com.scalasourcing.example.voting.Upvote._

import scala.io.StdIn

object InteractiveApp extends App
{
    val eventStorage = new EventStorage
    val executor = new CommandExecutor(eventStorage)

    main()

    def main() =
    {
        println("Welcome to The Todo App!")

        var commandText = ""
        while (commandText != "q")
        {
            print("command> ")
            commandText = StdIn.readLine()

            if (commandText == "read")
            {
                println("Your todo items list is empty")
            }
            else
            {
                execute(commandText) match
                {
                    case Left(text)   => println(text)
                    case Right(error) => System.err.println(error)
                }
            }
        }
    }

    def execute(commandText: String): Either[String, String] =
    {
        val tokens = commandText.split(" ").toList.filter(i => !i.trim.isEmpty)

        tokens match
        {
            case "add" :: id :: text        => command(id, new Add(text.mkString(" ")))
            case "edit" :: id :: text       => command(id, new Edit(text.mkString(" ")))
            case "remove" :: id :: _        => command(id, new Remove())
            case "upvote" :: id :: _        => command(id, new Cast())
            case "cancel-upvote" :: id :: _ => command(id, new Cancel())
            case "q" :: _                   => Left("Thank you for using The Todo App! Please, come back!")
            case _                          => Right("Bad command")
        }
    }

    def command[AR <: AggregateRoot[AR]](id: String, command: CommandOf[AR])(implicit f: FactoryOf[AR], m: Manifest[AR]): Either[String, String] =
    {
        executor.execute(id, command) match
        {
            case Left(seq)    => Left(readableEvents(id, seq))
            case Right(error) => Right(readableError(id, error))
        }
    }

    def readableEvents(id: String, seq: Seq[AnyRef]): String =
    {
        seq
        .map
        {
            case Added(text)  => s"the item '$id' was added with text '$text'"
            case Edited(text) => s"the item '$id' was edited to '$text'"
            case Removed()    => s"the item '$id' was removed"

            case Casted()      => s"the item '$id' was upvoted"
            case Cancelled() => s"'$id' item's upvote was cancelled"

            case _ => "unknown event"
        }
        .mkString(", ")
    }

    def readableError(id: String, error: AnyRef): String = error match
    {
        case TodoExistedError()              => s"item '$id' already exists"
        case TodoDidNotExistError()          => s"item '$id' does not exist"
        case NewTextIsEmptyError()           => s"todo items can't have empty text"
        case NewTextIsTheSameAsTheOldError() => s"item '$id' already has this text"

        case WasAlreadyCastedError()    => s"you have already upvoted item '$id'"
        case WasNotCastedError() => s"you did not upvote item '$id'"

        case _ => "Unknown error"
    }
}
