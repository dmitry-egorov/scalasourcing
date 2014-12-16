package com.scalasourcing.example.apps.simpleapp

import com.scalasourcing.example.editing.Todo
import com.scalasourcing.example.editing.Todo._

object SimpleApp extends App
{
    var todo = Option.empty[Todo]

    todo = todo +! Add("Greet") +! Edit("Greet the World.")

    println(s"Todo: $todo")

    println("Hello world!")

    todo = todo +! Remove()

    println(s"Todo: $todo")

    println("Done!")
}
