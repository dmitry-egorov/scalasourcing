package com.scalasourcing.examples.todoes.editing

import com.scalasourcing.core.AggregateRoot

case class Todo(text: PlainText)

object Todo extends AggregateRoot[Todo]
{
    case class Add(initialText: PlainText) extends Command
    case class Edit(newText: PlainText) extends Command
    case class Remove() extends Command

    case class Added(initialText: PlainText) extends Event
    case class Edited(newText: PlainText) extends Event
    case class Removed() extends Event

    case class TodoAlreadyExists() extends Error
    case class TodoDoesNotExist() extends Error
    case class TextIsEmpty() extends Error
    case class TextIsTheSame() extends Error

    def apply(state: State, event: Event): State = (state, event) match
    {
        case (None, Added(text))           => Todo(text)
        case (Some(Todo(_)), Edited(text)) => Todo(text)
        case (Some(Todo(_)), Removed())    => None
        case _                             => state
    }

    def apply(state: State, command: Command): CommandResult = state match
    {
        case None             => whenNone(command)
        case Some(Todo(text)) => whenSome(command, text)
    }

    private def whenNone(command: Command): CommandResult = command match
    {
        case Add(text) => add(text)
        case Edit(_)   => TodoDoesNotExist()
        case Remove()  => TodoDoesNotExist()
    }

    private def whenSome(command: Command, text: PlainText): CommandResult = command match
    {
        case Add(_)        => TodoAlreadyExists()
        case Edit(newText) => edit(text, newText)
        case Remove()      => Removed()
    }

    private def add(text: PlainText): CommandResult =
    {
        if (text.isEmpty) TextIsEmpty()
        else Added(text)
    }

    private def edit(text: PlainText, newText: PlainText): CommandResult =
    {
        if (newText.isEmpty) TextIsEmpty()
        else if (newText == text) TextIsTheSame()
        else Edited(newText)
    }
}