package com.scalasourcing.example.domain.editing

import com.scalasourcing.model._

case class TodoId(value: String) extends AggregateId

object TodoId
{
    implicit def from(s: String): TodoId = TodoId(s)
}

object Todo extends Aggregate
{
    type Id = TodoId

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

    case class NonExitingTodo() extends State
    {
        def apply(event: Event) = event match
        {
            case Added(text) => ExistingTodo(text)
            case _           => this
        }

        def apply(command: Command) = command match
        {
            case Add(text) => add(text)
            case Edit(_)   => TodoDidNotExistError()
            case Remove()  => TodoDidNotExistError()
        }

        private def add(text: PlainText): CommandResult =
        {
            if (text.isEmpty) NewTextIsEmptyError()
            else Added(text)
        }
    }

    case class ExistingTodo(text: PlainText) extends State
    {
        def apply(event: Event) = event match
        {
            case Edited(newText) => ExistingTodo(newText)
            case Removed()       => NonExitingTodo()
            case _               => this
        }

        def apply(command: Command) = command match
        {
            case Add(_)        => TodoExistedError()
            case Edit(newText) => edit(newText)
            case Remove()      => Removed()
        }

        private def edit(newText: PlainText): CommandResult =
        {
            if (newText.isEmpty) NewTextIsEmptyError()
            else if (newText == text) NewTextIsTheSameAsTheOldError()
            else Edited(newText)
        }
    }

    def seed = NonExitingTodo()
}
