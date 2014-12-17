package com.scalasourcing.backends.memory

import com.scalasourcing.backends.memory.Root.RootEvent
import com.scalasourcing.model.AggregateId
import org.scalatest.{FunSuite, Matchers}

class InMemoryEventStorageSuite extends FunSuite with Matchers
{
    test("Should return empty messages when nothing was added")
    {
        //given
        val id = new AggregateId("1")
        val es = new InMemoryEventStorage

        //when
        val events = es.get[Root](id)

        //then
        events should be(empty)
    }

    test("Should return persisted messages")
    {
        //given
        val id = new AggregateId("1")
        val es = new InMemoryEventStorage

        val persistedEvents = Seq(RootEvent())
        es.persist(id, persistedEvents)

        //when
        val events = es.get[Root](id)

        //then
        events should equal(persistedEvents)
    }

    test("Should return persisted messages for each aggregate instance")
    {
        //given
        val id1 = new AggregateId("1")
        val id2 = new AggregateId("2")
        val es = new InMemoryEventStorage

        val persistedEvents1 = Seq(RootEvent())
        val persistedEvents2 = Seq(RootEvent(), RootEvent())
        es.persist(id1, persistedEvents1)
        es.persist(id2, persistedEvents2)

        //when
        val events1 = es.get[Root](id1)
        val events2 = es.get[Root](id2)

        //then
        events1 should equal(persistedEvents1)
        events2 should equal(persistedEvents2)
    }
}
