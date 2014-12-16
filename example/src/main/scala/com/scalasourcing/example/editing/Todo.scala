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

    def apply(state: Option[Todo], command: Command): CommandResult =
    {
        def r: CommandResult = state match
        {
            case None             => whenNone
            case Some(Todo(text)) => whenSome(text)
        }

        def whenNone: CommandResult =
        {
            def r: CommandResult = command match
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

            r
        }

        def whenSome(text: PlainText): CommandResult =
        {
            def r: CommandResult = command match
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

            r
        }

        r
    }
}