package todoes.contexts.editing

import scalasourcing.scalasourcing.Aggregation

object TodoItemRoot
{
    case class TodoItem(title: String)

    sealed trait TodoItemEvent
    case class Added(initialTitle: String) extends TodoItemEvent
    case class Edited(newTitle: String) extends TodoItemEvent
    case class Removed() extends TodoItemEvent

    sealed trait TodoItemCommand
    case class Add(initialTitle: String) extends TodoItemCommand
    case class Edit(newTitle: String) extends TodoItemCommand
    case class Remove() extends TodoItemCommand

    case class AlreadyExistsError() extends Exception
    case class DoesNotExistError() extends Exception
    case class EmptyTitleError() extends Exception
    case class SameTitleError() extends Exception

    implicit object TodoItem extends Aggregation[TodoItem, TodoItemEvent, TodoItemCommand]
    {
        def order(agg: Option[TodoItem], command: TodoItemCommand): Seq[TodoItemEvent] =
            agg match
            {
                case None                  =>
                    command match
                    {
                        case Add(title) => addItem(title)
                        case Edit(_)    => throw DoesNotExistError()
                        case Remove()   => throw DoesNotExistError()
                    }
                case Some(TodoItem(title)) =>
                    command match
                    {
                        case Add(_)         => throw AlreadyExistsError()
                        case Edit(newTitle) => editExistingItem(title, newTitle)
                        case Remove()       => ok(Removed())
                    }
            }

        def applyEvent(agg: Option[TodoItem], event: TodoItemEvent): Option[TodoItem] =
            (agg, event) match
            {
                case (None, Added(title))               => Some(TodoItem(title))
                case (Some(TodoItem(_)), Edited(title)) => Some(TodoItem(title))
                case (Some(TodoItem(_)), Removed())     => None
                case _                                  => agg
            }

        private def addItem(title: String): Seq[TodoItemEvent] =
        {
            if (title.trim.isEmpty) throw EmptyTitleError()
            else ok(Added(title))
        }

        private def editExistingItem(title: String, newTitle: String): Seq[TodoItemEvent] =
        {
            if (newTitle.trim.isEmpty) throw EmptyTitleError()
            if (newTitle == title) throw SameTitleError()
            ok(Edited(newTitle))
        }
    }
}
