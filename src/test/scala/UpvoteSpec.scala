import com.de.todoes.voting.Upvote
import com.de.todoes.voting.Upvote.UpvoteRoot
import org.scalatest._

class UpvoteSpec extends FunSuite with Matchers with ScalaTestDDD[Upvote] with UpvoteRoot
{
    test("A votee should be upvoted")
    {
        given() ??
        Set() -->
        Set_()
    }

    test("An upvoted votee should not be upvoted")
    {
        given(Set_()) ??
        Set() !!!
        AlreadySet()
    }

    test("A votee with upvote should be cancelled")
    {
        given(Set_()) ??
        Cancel() -->
        Cancelled()
    }

    test("A votee without upvote should not be cancelled")
    {
        given() ??
        Cancel() !!!
        NotSet()
    }

    test("A votee should be upvoted when it was upvoted and then cancelled")
    {
        given(Set_(), Cancelled()) ??
        Set() -->
        Set_()
    }

    test("A votee's upvote should not be cancelled when it was upvoted and then cancelled")
    {
        given(Set_(), Cancelled()) ??
        Cancel() !!!
        NotSet()
    }
}
