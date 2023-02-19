package com.multilink.nodes

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}


object FileServer {

  sealed trait FileServerCommand

  final case class GetConnection(screenName: String, replyTo: ActorRef[ConnectionEvent]) extends FileServerCommand

  sealed trait FileServerEvent

  //final case class MessagePosted(screenName: String, message: String) extends SessionEvent

  sealed trait ConnectionCommand

  final case class AttemptLogin(username: String, password: String) extends ConnectionCommand

  final case class CrackLogin() extends ConnectionCommand

  final case class Disconnect() extends ConnectionCommand

  sealed trait ConnectionEvent

  final case class ConnectionEstablished(handle: ActorRef[ConnectionCommand]) extends ConnectionEvent

  final case class ConnectionFailed(reason: String) extends ConnectionEvent

  final case class LoginFailed(username: String, password: String) extends ConnectionEvent

  final case class Disconnected() extends ConnectionEvent


  def apply(): Behavior[FileServerCommand] =
    Behaviors.setup(context => new FileServerBehavior(context))

  class FileServerBehavior(context: ActorContext[FileServerCommand]) extends AbstractBehavior[FileServerCommand](context) {
    private var connections: List[ActorRef[ConnectionCommand]] = List.empty

    override def onMessage(message: FileServerCommand): Behavior[FileServerCommand] = {
      message match {
        case GetConnection(screenName, client) =>
          // create a child actor for further interaction with the client
          val connection = context.spawn(ConnectionBehavior(context.self, screenName, client),
                                         name = "banana")
          client ! ConnectionEstablished(connection)
          connections = connection :: connections
          this
      }
    }
  }

  // consider an FSM?
  private object ConnectionBehavior {
    def apply(fileServer: ActorRef[FileServerCommand],
              screenName: String,
              client: ActorRef[ConnectionEvent]): Behavior[ConnectionCommand] =
      Behaviors.setup(ctx => new ConnectionBehavior(ctx, fileServer, screenName, client))
  }

  private class ConnectionBehavior(context: ActorContext[ConnectionCommand],
                                   fileServer: ActorRef[FileServerCommand],
                                   screenName: String,
                                   client: ActorRef[ConnectionEvent])
    extends AbstractBehavior[ConnectionCommand](context) {

    override def onMessage(msg: ConnectionCommand): Behavior[ConnectionCommand] =
      msg match {
        case AttemptLogin(username, password) =>
          client ! LoginFailed(username, password)
          Behaviors.same
        case Disconnect() =>
          client ! Disconnected()
          Behaviors.stopped
        case CrackLogin() =>
          Behaviors.unhandled
      }
  }
}
