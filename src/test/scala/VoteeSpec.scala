import todoes.contexts.voting.VoteeRoot._
import org.scalatest._

class VoteeSpec extends FunSuite with Matchers with ScalaTestDDD
{
    test("A target should be upvoted")
    {
        given() ?? Upvote() --> Upvoted()
    }

    test("An upvoted target should not be upvoted")
    {
        given(Upvoted()) ?? Upvote() !!! AlreadyUpvotedError()
    }

    test("A target with upvote should be cancelled")
    {
        given(Upvoted()) ?? CancelUpvote() --> UpvoteCancelled()
    }

    test("A target without upvote should not be cancelled")
    {
        given() ?? CancelUpvote() !!! NotUpvotedError()
    }

    test("A target should be upvoted when it was upvoted and then cancelled")
    {
        given(Upvoted(), UpvoteCancelled()) ?? Upvote() --> Upvoted()
    }

    test("A target's upvote should not be cancelled when it was upvoted and then cancelled")
    {
        given(Upvoted(), UpvoteCancelled()) ?? CancelUpvote() !!! NotUpvotedError()
    }
}
