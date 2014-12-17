package com.scalasourcing.example.apps.simple

import com.scalasourcing.example.domain.editing.Todo._

object SimpleTodoApp extends App
{
    var todo = factory.seed +! Add("Greet") +! Edit("Greet the World.")

    println(s"Todo: $todo")

    println("Hello world!")

    todo = todo +! Remove()

    println(s"Todo: $todo")

    println("Done!")
}
