package com.de.todoes.editing

import com.de.scalasourcing.AggregateRoot

case class Todo(title: String)

object Todo extends TodoRoot

trait TodoRoot extends AggregateRoot[Todo]
{
    implicit object sourcer extends Sourcer
    {
        def apply(state: State, command: Command): Sourcing = state match
        {
            case None              => applyToNone(command)
            case Some(Todo(title)) => applyToSome(command, title)
        }

        def applyToNone(command: Command): Sourcing = command match
        {
            case Add(title) => addItem(title)
            case Edit(_)    => DoesNotExist()
            case Remove()   => DoesNotExist()
        }

        def applyToSome(command: Command, title: String): Sourcing = command match
        {
            case Add(_)         => AlreadyExists()
            case Edit(newTitle) => editExistingItem(title, newTitle)
            case Remove()       => Removed()
        }

        private def addItem(title: String): Sourcing =
        {
            if (title.trim.isEmpty) EmptyTitle()
            else Added(title)
        }

        private def editExistingItem(title: String, newTitle: String): Sourcing =
        {
            if (newTitle.trim.isEmpty) EmptyTitle()
            else if (newTitle == title) SameTitle()
            else Edited(newTitle)
        }
    }

    implicit object applicator extends Applicator
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


