package com.de.todoes.editing

import com.de.scalasourcing.EventSourcing._

object TodoItemRoot
{
    case class TodoItem(title: String)
    type State = Option[TodoItem]
    type Command = CommandOf[TodoItem]
    type Event = EventOf[TodoItem]

    implicit object Sourcer extends Sourcer[TodoItem]
    {
        def apply(state: State, command: Command): Seq[Event] = state match
        {
            case None                    => applyToNone (command)
            case Some (TodoItem (title)) => applyToSome (command, title)
        }

        def applyToNone(command: Command): Seq[Event] = command match
        {
            case Add (title) => addItem (title)
            case Edit (_)    => throw DoesNotExistError ()
            case Remove ()   => throw DoesNotExistError ()
        }

        def applyToSome(command: Command, title: String): Seq[Event] = command match
        {
            case Add (_)         => throw AlreadyExistsError ()
            case Edit (newTitle) => editExistingItem (title, newTitle)
            case Remove ()       => Seq (Removed ())
        }

        private def addItem(title: String): Seq[Event] =
        {
            if (title.trim.isEmpty) throw EmptyTitleError ()

            Seq (Added (title))
        }

        private def editExistingItem(title: String, newTitle: String): Seq[Event] =
        {
            if (newTitle.trim.isEmpty) throw EmptyTitleError ()
            if (newTitle == title) throw SameTitleError ()

            Seq (Edited (newTitle))
        }
    }

    implicit object Applicator extends Applicator[TodoItem]
    {
        def apply(state: State, event: Event): State = (state, event) match
        {
            case (None, Added (title))                 => Some (TodoItem (title))
            case (Some (TodoItem (_)), Edited (title)) => Some (TodoItem (title))
            case (Some (TodoItem (_)), Removed ())     => None
            case _                                     => state
        }
    }

    case class Add(initialTitle: String) extends Command
    case class Edit(newTitle: String) extends Command
    case class Remove() extends Command

    case class Added(initialTitle: String) extends Event
    case class Edited(newTitle: String) extends Event
    case class Removed() extends Event

    case class AlreadyExistsError() extends Exception
    case class DoesNotExistError() extends Exception
    case class EmptyTitleError() extends Exception
    case class SameTitleError() extends Exception
}
