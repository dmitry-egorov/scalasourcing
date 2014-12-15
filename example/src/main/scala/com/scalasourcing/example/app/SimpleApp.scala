package com.scalasourcing.example.app

import com.scalasourcing.example.editing.Todo._

object SimpleApp extends App
{
    val todo = None

    val modified = todo +! Add("Greet") +! Edit("Greet the World.")

    println(s"Todo: ${modified.get}")

    println("Hello world!")

    val removed = modified +! Remove()

    println(s"Todo: $removed")

    println("Done!")
}
