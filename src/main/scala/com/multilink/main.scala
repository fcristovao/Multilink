package com.multilink

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.LoggerOps
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import com.multilink.nodes.FileServer
import com.multilink.nodes.FileServer.{AttemptLogin, ConnectionEstablished, ConnectionEvent, GetConnection, LoginFailed}




object HelloWorldMain {

  final case class SayHello(name: String)

  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      val fileServer = context.spawn(FileServer(), "fileServer")

      Behaviors.receiveMessage { message =>
        val multilinkClient = context.spawn(MultilinkClient(), message.name)
        multilinkClient ! MultilinkClient.ConnectToFileServer(fileServer)
        Behaviors.same
      }
    }


  def main(args: Array[String]): Unit = {
    /*
    val system: ActorSystem[HelloWorldMain.SayHello] =
      ActorSystem(HelloWorldMain(), "MultiLink")

    system ! HelloWorldMain.SayHello("World")
    */
    MultilinkCLI.run()
    
  }
}