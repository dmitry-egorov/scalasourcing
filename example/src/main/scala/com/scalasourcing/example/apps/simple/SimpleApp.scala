package com.scalasourcing.example.apps.simple

import com.scalasourcing.example.editing.Todo._

object SimpleApp extends App
{
    var todo = f.create

    todo = todo +! Add("Greet") +! Edit("Greet the World.")

    println(s"Todo: $todo")

    println("Hello world!")

    todo = todo +! Remove()

    println(s"Todo: $todo")

    println("Done!")
}
