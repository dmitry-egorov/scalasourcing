package com.scalasourcing.backend.memory

import com.scalasourcing.backend.memory.Root.RootEvent
import com.scalasourcing.model.AggregateId
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}

class InMemoryEventStorageSuite extends FunSuite with Matchers with ScalaFutures
{
    implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
    val id1 = new AggregateId("1")
    val id2 = new AggregateId("2")

    test("Should return empty messages when nothing was added")
    {
        //given
        val es = createStorage

        //when
        val f = es.get(id1)

        //then
        whenReady(f)
        {
            events => events should be(empty)
        }
    }

    test("Should return persisted messages")
    {
        //given
        val es = createStorage

        val persistedEvents = Seq(RootEvent())
        es.tryPersist(id1, persistedEvents, 0)

        //when
        val f = es.get(id1)

        //then
        whenReady(f)
        {
            events => events should equal(persistedEvents)
        }
    }

    test("Should return persisted messages for each aggregate instance")
    {
        //given
        val es = createStorage

        val persistedEvents1 = Seq(RootEvent())
        val persistedEvents2 = Seq(RootEvent(), RootEvent())
        es.tryPersist(id1, persistedEvents1, 0)
        es.tryPersist(id2, persistedEvents2, 0)

        //when
        val f =
        for
        {
            events1 <- es.get(id1)
            events2 <- es.get(id2)
        }
            yield (events1, events2)


        //then
        whenReady(f)
        {
            (e) =>
            e._1 should equal(persistedEvents1)
            e._2 should equal(persistedEvents2)
        }

    }

    def createStorage: SingleThreadInMemoryEventStorage[Root] =
    {
        new SingleThreadInMemoryEventStorage[Root]
    }

}
