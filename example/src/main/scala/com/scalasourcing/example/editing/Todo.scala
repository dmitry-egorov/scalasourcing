package com.scalasourcing.example.editing

import com.scalasourcing._

sealed trait Todo extends AggregateRoot[Todo]

object Todo extends AggregateFactory[Todo]
{
    case class Add(initialText: PlainText) extends Command
    case class Edit(newText: PlainText) extends Command
    case class Remove() extends Command

    case class Added(initialText: PlainText) extends Event
    case class Edited(newText: PlainText) extends Event
    case class Removed() extends Event

    case class TodoExistedError() extends Error
    case class TodoDidNotExistError() extends Error
    case class NewTextIsEmptyError() extends Error
    case class NewTextIsTheSameAsTheOldError() extends Error

    case class NonExitingTodo() extends Todo
    {
        def apply(event: Event): Todo = event match
        {
            case Added(text) => ExistingTodo(text)
            case _           => this
        }

        def apply(command: Command): CommandResult = command match
        {
            case Add(text) => add(text)
            case Edit(_)   => TodoDidNotExistError()
            case Remove()  => TodoDidNotExistError()
        }

        def add(text: PlainText): CommandResult =
        {
            if (text.isEmpty) NewTextIsEmptyError()
            else Added(text)
        }
    }

    case class ExistingTodo(text: PlainText) extends Todo
    {
        def apply(event: Event): Todo = event match
        {
            case Edited(newText) => ExistingTodo(newText)
            case Removed()       => NonExitingTodo()
            case _               => this
        }

        def apply(command: Command): CommandResult = command match
        {
            case Add(_)        => TodoExistedError()
            case Edit(newText) => edit(newText)
            case Remove()      => Removed()
        }

        def edit(newText: PlainText): CommandResult =
        {
            if (newText.isEmpty) NewTextIsEmptyError()
            else if (newText == text) NewTextIsTheSameAsTheOldError()
            else Edited(newText)
        }
    }

    def create: Todo = NonExitingTodo()
}