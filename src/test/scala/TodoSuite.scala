import com.de.todoes.editing.Todo._
import com.de.todoes.editing._
import org.scalatest._

class TodoSuite extends FunSuite with Matchers with AggregateBDD[Todo]
{
    test("A todo should be added")
    {
        given_nothing when_I Add("title") then_it_is Added("title")
    }

    test("A todo with empty title should not be added")
    {
        given_nothing when_I Add("  ") then_error EmptyTitle()
    }

    test("A todo should not be added for the second time")
    {
        given it_is Added("title") when_I Add("title 2") then_error AlreadyExists()
    }

    test("Added todo should be edited with different title")
    {
        given it_is Added("title") when_I Edit("title 2") then_it_is Edited("title 2")
    }

    test("Edited todo should be edited with different title")
    {
        given it_is Added("title") and Edited("title 2") when_I Edit("title 3") then_it_is Edited("title 3")
    }

    test("A non existing todo should not be edited")
    {
        given_nothing when_I Edit("title") then_error DoesNotExist()
    }

    test("Added todo should not be edited with the same title")
    {
        given it_is Added("title") when_I Edit("title") then_error SameTitle()
    }

    test("Edited todo should not be edited again with the same title")
    {
        given it_is Added("title") and Edited("title 2") when_I Edit("title 2") then_error SameTitle()
    }

    test("Added todo should not be edited to empty title")
    {
        given it_is Added("title") when_I Edit("  ") then_error EmptyTitle()
    }

    test("A todo should be removed")
    {
        given it_is Added("title") when_I Remove() then_it_is Removed()
    }

    test("A todo should not be removed when it doesn't exist")
    {
        given_nothing when_I Remove() then_error DoesNotExist()
    }
}
