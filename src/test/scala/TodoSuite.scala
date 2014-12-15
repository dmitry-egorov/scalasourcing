import com.scalasourcing.examples.todoes.editing.Todo
import com.scalasourcing.examples.todoes.editing.Todo._
import org.scalatest._

class TodoSuite extends FunSuite with Matchers with AggregateBDD[Todo]
{
    test("A todo should be added")
    {
        given_nothing when_I Add("text") then_it_is Added("text")
    }

    test("A todo with empty text should not be added")
    {
        given_nothing when_I Add("  ") then_error TextIsEmpty()
    }

    test("A todo should not be added for the second time")
    {
        given it_is Added("text") when_I Add("text 2") then_error TodoAlreadyExists()
    }

    test("Added todo should be edited with different text")
    {
        given it_is Added("text") when_I Edit("text 2") then_it_is Edited("text 2")
    }

    test("Edited todo should be edited with different text")
    {
        given it_is Added("text") and Edited("text 2") when_I Edit("text 3") then_it_is Edited("text 3")
    }

    test("A non existing todo should not be edited")
    {
        given_nothing when_I Edit("text") then_error TodoDoesNotExist()
    }

    test("Added todo should not be edited with the same text")
    {
        given it_is Added("text") when_I Edit("text") then_error TextIsTheSame()
    }

    test("Edited todo should not be edited again with the same text")
    {
        given it_is Added("text") and Edited("text 2") when_I Edit("text 2") then_error TextIsTheSame()
    }

    test("Added todo should not be edited to empty text")
    {
        given it_is Added("text") when_I Edit("  ") then_error TextIsEmpty()
    }

    test("A todo should be removed")
    {
        given it_is Added("text") when_I Remove() then_it_is Removed()
    }

    test("A todo should not be removed when it doesn't exist")
    {
        given_nothing when_I Remove() then_error TodoDoesNotExist()
    }
}
