import com.de.todoes.editing._
import org.scalatest._


class TodoSpec extends FunSuite with Matchers with ScalaTestDDD[Todo] with TodoRoot
{
    test("A new todo item should be added")
    {
        given() ??
        Add("title") -->
        Added("title")
    }

    test("A new todo item should not be added with empty title")
    {
        given() ??
        Add("  ") !!!
        EmptyTitle()
    }

    test("An existing todo item should not be added again")
    {
        given(Added("title")) ??
        Add("title2") !!!
        AlreadyExists()
    }

    test("A todo item should be edited")
    {
        given(Added("title")) ??
        Edit("title2") -->
        Edited("title2")
    }

    test("A todo item should not be edited if it doesn't exist")
    {
        given() ??
        Edit("title") !!!
        DoesNotExist()
    }

    test("A todo item should not be edited to the same title")
    {
        given(Added("title")) ??
        Edit("title") !!!
        SameTitle()
    }

    test("A todo item should not be edited to empty title")
    {
        given(Added("title")) ??
        Edit("  ") !!!
        EmptyTitle()
    }

    test("A todo item should be removed")
    {
        given(Added("title")) ??
        Remove() -->
        Removed()
    }

    test("A todo item should not be removed ?? it doesn't exist")
    {
        given() ??
        Remove() !!!
        DoesNotExist()
    }
}
