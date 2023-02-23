package com.multilink

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.multilink.MultilinkClient.{NOT_STARTED, STARTED, STARTING, Status}
import com.multilink.nodes.FileServer
import com.multilink.nodes.FileServer.{AttemptLogin, ConnectionEstablished, GetConnection, LoginFailed}

import scala.concurrent.Future

object MultilinkClient {

  sealed trait Status

  case object NOT_STARTED extends Status

  case object STARTING extends Status

  case object STARTED extends Status
  
}

class MultilinkClient {
  import concurrent.ExecutionContext.Implicits.global
  
  private var status: Status = NOT_STARTED

  /*
  // Should this method be blocking? Not a great CLI experience
  // Or should it return a Future? Probably better for whomever is asking for something to be done
  // OR it should return a unit, and everything is event based? (the CLI would just register as a listener, and that
  // allows for reactive interfaces)
  // OR both, and the Future is actually just implemented by registering itself as a listener, and completes when it 
  // gets the event?
  */
  def start(username: String, password: String): Future[Either[String, Unit]] = {
    if (status == NOT_STARTED) {
      status = STARTING
      // Add code here for connecting to the multilink actors
      status = STARTED
      Future.successful(Right(()))
    } else {
      Future.successful(Left(s"Incorrect State: $status"))
    }
  }
}
