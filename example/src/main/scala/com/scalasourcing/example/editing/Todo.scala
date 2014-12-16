package com.scalasourcing.example.editing

import com.scalasourcing.AggregateRoot

case class Todo(text: PlainText)

object Todo extends AggregateRoot[Todo]
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

    def apply(state: Option[Todo], event: Event): Option[Todo] = (state, event) match
    {
        case (None, Added(text))           => Todo(text)
        case (Some(Todo(_)), Edited(text)) => Todo(text)
        case (Some(Todo(_)), Removed())    => None
        case _                             => state
    }

    def apply(state: Option[Todo], command: Command): CommandResult = state match
    {
        case None             => whenNone(command)
        case Some(Todo(text)) => whenSome(command, text)
    }

    private def whenNone(command: Command): CommandResult = command match
    {
        case Add(text) => add(text)
        case Edit(_)   => TodoDidNotExistError()
        case Remove()  => TodoDidNotExistError()
    }

    private def whenSome(command: Command, text: PlainText): CommandResult = command match
    {
        case Add(_)        => TodoExistedError()
        case Edit(newText) => edit(text, newText)
        case Remove()      => Removed()
    }

    private def add(text: PlainText): CommandResult =
    {
        if (text.isEmpty) NewTextIsEmptyError()
        else Added(text)
    }

    private def edit(text: PlainText, newText: PlainText): CommandResult =
    {
        if (newText.isEmpty) NewTextIsEmptyError()
        else if (newText == text) NewTextIsTheSameAsTheOldError()
        else Edited(newText)
    }
}