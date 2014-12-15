import com.de.todoes.voting.Upvote._
import com.de.todoes.voting._
import org.scalatest._

class UpvoteSuite extends FunSuite with Matchers with AggregateBDD[Upvote]
{
    test("An upvote should be set")
    {
        given_nothing when_I Set() then_it_is $Set()
    }

    test("Set upvote should not be set again")
    {
        given it_is $Set() when_I Set() then_error AlreadySet()
    }

    test("Set upvote should be cancelled")
    {
        given it_is $Set() when_I Cancel() then_it_is Cancelled()
    }

    test("Unset upvote should not be cancelled")
    {
        given_nothing when_I Cancel() then_error NotSet()
    }

    test("An upvote should be set when it was set and then cancelled")
    {
        given it_is $Set() and Cancelled() when_I Set() then_it_is $Set()
    }

    test("An upvote should not be cancelled when it was set and then cancelled")
    {
        given it_is $Set() and Cancelled() when_I Cancel() then_error NotSet()
    }
}
