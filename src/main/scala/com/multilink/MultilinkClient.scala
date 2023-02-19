package com.multilink

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.multilink.nodes.FileServer
import com.multilink.nodes.FileServer.{AttemptLogin, ConnectionEstablished, GetConnection, LoginFailed}

object MultilinkClient {

  sealed trait MultilinkClientCommand

  final case class ConnectToFileServer(fileServer: ActorRef[FileServer.FileServerCommand]) extends MultilinkClientCommand


  def apply(): Behavior[Any] = {
    bot(0)
  }

  private def bot(greetingCounter: Int): Behavior[Any] =
    Behaviors.receive { (context, message) =>
      println(s"Greeting $greetingCounter for $message")
      message match
        case ConnectToFileServer(fileServer) =>
          fileServer ! GetConnection("banana", context.self)
          bot(greetingCounter + 1)
        case ConnectionEstablished(newConnection) =>
          println(s"new Connection: $newConnection")
          newConnection ! AttemptLogin("fcristovao", "12345")
          bot(greetingCounter + 1)
        case LoginFailed(username, password) =>
          println(s"Login Failed: $username & $password")
          Behaviors.stopped
    }
}
