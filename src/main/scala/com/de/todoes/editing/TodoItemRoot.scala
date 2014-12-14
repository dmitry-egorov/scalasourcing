package com.de.todoes.editing

import com.de.scalasourcing.EventSourcing._

object TodoItemRoot
{
    case class TodoItem(title: String)
    type State = Option[TodoItem]
    type Command = CommandOf[TodoItem]
    type Event = EventOf[TodoItem]
    type Error = ErrorOf[TodoItem]

    implicit object Sourcer extends Sourcer[TodoItem]
    {
        def apply(state: State, command: Command): Sourcing[TodoItem] = state match
        {
            case None                  => applyToNone(command)
            case Some(TodoItem(title)) => applyToSome(command, title)
        }

        def applyToNone(command: Command): Sourcing[TodoItem] = command match
        {
            case Add(title) => addItem(title)
            case Edit(_)    => DoesNotExist()
            case Remove()   => DoesNotExist()
        }

        def applyToSome(command: Command, title: String): Sourcing[TodoItem] = command match
        {
            case Add(_)         => AlreadyExists()
            case Edit(newTitle) => editExistingItem(title, newTitle)
            case Remove()       => Removed()
        }

        private def addItem(title: String): Sourcing[TodoItem] =
        {
            if (title.trim.isEmpty) EmptyTitle()
            else Added(title)
        }

        private def editExistingItem(title: String, newTitle: String): Sourcing[TodoItem] =
        {
            if (newTitle.trim.isEmpty) EmptyTitle()
            else if (newTitle == title) SameTitle()
            else Edited(newTitle)
        }
    }

    implicit object Applicator extends Applicator[TodoItem]
    {
        def apply(state: State, event: Event): State = (state, event) match
        {
            case (None, Added(title))               => TodoItem(title)
            case (Some(TodoItem(_)), Edited(title)) => TodoItem(title)
            case (Some(TodoItem(_)), Removed())     => None
            case _                                  => state
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
