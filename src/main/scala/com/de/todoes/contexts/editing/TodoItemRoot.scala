package com.de.todoes.contexts.editing

import com.de.scalasourcing._

object TodoItemRoot
{
    case class TodoItem(title: String)
    sealed trait TodoItemCommand
    sealed trait TodoItemEvent

    implicit object Sourcer extends Sourcer[TodoItem, TodoItemEvent, TodoItemCommand]
    {
        def apply(state: Option[TodoItem], command: TodoItemCommand): Seq[TodoItemEvent] =
            state match
            {
                case None                  => applyToNone(command)
                case Some(TodoItem(title)) => applyToSome(command, title)
            }

        def applyToSome(command: TodoItemCommand, title: String): Seq[TodoItemEvent] =
        {
            command match
            {
                case Add(_)         => throw AlreadyExistsError()
                case Edit(newTitle) => editExistingItem(title, newTitle)
                case Remove()       => Seq(Removed())
            }
        }
        def applyToNone(command: TodoItemCommand): Seq[TodoItemEvent] =
        {
            command match
            {
                case Add(title) => addItem(title)
                case Edit(_)    => throw DoesNotExistError()
                case Remove()   => throw DoesNotExistError()
            }
        }
        private def addItem(title: String): Seq[TodoItemEvent] =
        {
            if (title.trim.isEmpty) throw EmptyTitleError()

            Seq(Added(title))
        }

        private def editExistingItem(title: String, newTitle: String): Seq[TodoItemEvent] =
        {
            if (newTitle.trim.isEmpty) throw EmptyTitleError()
            if (newTitle == title) throw SameTitleError()

            Seq(Edited(newTitle))
        }
    }

    implicit object Applicator extends Applicator[TodoItem, TodoItemEvent]
    {
        def apply(state: Option[TodoItem], event: TodoItemEvent): Option[TodoItem] =
            (state, event) match
            {
                case (None, Added(title))               => Some(TodoItem(title))
                case (Some(TodoItem(_)), Edited(title)) => Some(TodoItem(title))
                case (Some(TodoItem(_)), Removed())     => None
                case _                                  => state
            }
    }

    case class Add(initialTitle: String) extends TodoItemCommand
    case class Edit(newTitle: String) extends TodoItemCommand
    case class Remove() extends TodoItemCommand

    case class Added(initialTitle: String) extends TodoItemEvent
    case class Edited(newTitle: String) extends TodoItemEvent
    case class Removed() extends TodoItemEvent

    case class AlreadyExistsError() extends Exception
    case class DoesNotExistError() extends Exception
    case class EmptyTitleError() extends Exception
    case class SameTitleError() extends Exception
}
