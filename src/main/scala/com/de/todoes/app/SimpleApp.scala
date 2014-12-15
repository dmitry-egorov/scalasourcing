package com.de.todoes.app

import com.de.todoes.editing.Todo
import com.de.todoes.editing.Todo._

object SimpleApp extends App
{
    val todo = Todo.create

    val modified = todo !+ Add("Greet") !+ Edit("Greet the World.")

    println(s"Todo: ${modified.get}")

    println("Hello world!")

    val removed = modified !+ Remove()

    println(s"Todo: $removed")
}
