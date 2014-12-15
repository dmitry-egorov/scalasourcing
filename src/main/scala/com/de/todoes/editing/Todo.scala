package com.de.todoes.editing

import com.de.scalasourcing.AggregateRoot

case class Todo(title: String)

object Todo extends AggregateRoot[Todo]
{
    implicit object ca extends CommandApplication
    {
        def apply(state: State, command: Command): CommandResult = state match
        {
            case None              => whenNone(command)
            case Some(Todo(title)) => whenSome(command, title)
        }

        def whenNone(command: Command): CommandResult = command match
        {
            case Add(title) => add(title)
            case Edit(_)    => DoesNotExist()
            case Remove()   => DoesNotExist()
        }

        def whenSome(command: Command, title: String): CommandResult = command match
        {
            case Add(_)         => AlreadyExists()
            case Edit(newTitle) => edit(title, newTitle)
            case Remove()       => Removed()
        }

        private def add(title: String): CommandResult =
        {
            if (title.trim.isEmpty) EmptyTitle()
            else Added(title)
        }

        private def edit(title: String, newTitle: String): CommandResult =
        {
            if (newTitle.trim.isEmpty) EmptyTitle()
            else if (newTitle == title) SameTitle()
            else Edited(newTitle)
        }
    }

    implicit object ea extends EventApplication
    {
        def apply(state: State, event: Event): State = (state, event) match
        {
            case (None, Added(title))           => Todo(title)
            case (Some(Todo(_)), Edited(title)) => Todo(title)
            case (Some(Todo(_)), Removed())     => None
            case _                              => state
        }
    }

    case class Add(initialTitle: String) extends Command
    case class Edit(newTitle: String) extends Command
    case class Remove() extends Command

    case class Added(initialTitle: String) extends Event
    case class Edited(newTitle: String) extends Event
    case class Removed() extends Event

    case class AlreadyExists() extends Error
    case class DoesNotExist() extends Error
    case class EmptyTitle() extends Error
    case class SameTitle() extends Error
}