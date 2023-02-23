package com.multilink

import com.monovore.decline.*
import com.multilink.MultilinkCLI.{ConnectCommand, StartCommand}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.*

object MultilinkCLI {

  sealed trait Command

  case class ConnectCommand(address: Int) extends Command
  case class StartCommand(username: String, password: String) extends Command
}

class MultilinkCLI(val multilinkClient: MultilinkClient) {
  import cats.implicits._
  import scala.io.StdIn.readLine


  val address = Opts.argument[Int](metavar = "address")

  val username = Opts.argument[String](metavar = "username")
  val password = Opts.argument[String](metavar = "password")


  val connectCommand = Opts.subcommand(
    name = "connect",
    help = "Connect to a server"
    ) {
    address
  }.map(ConnectCommand.apply)

  val startCommand = Opts.subcommand(
    name = "start",
    help = "Start your remote Multilink server"
    ) {
    (username, password).tupled
  }.map(StartCommand.apply)

  val multilinkCommand: Command[MultilinkCLI.Command] = Command(
    name = "multilink",
    header = "Multilink CLI"
    ) {
    connectCommand orElse startCommand
  }

  def run(): Unit = {
    while (true) {
      print("multilink> ")
      val line = readLine()
      val lines = line.split(' ')
      parse(lines)
    }
  }

  def parse(args: Seq[String]): Unit = {
    multilinkCommand.parse(args) match {

      case Left(help) if help.errors.isEmpty =>
        // help was requested by the user, i.e.: `--help`
        println(help)
        sys.exit(0)

      case Left(help) =>
        // user needs help due to bad/missing arguments
        System.err.println(help)
        sys.exit(1)

      case Right(parsedValue) =>
        println(s"Command: $parsedValue")
        parsedValue match {
          case StartCommand(username, password) =>
            val result = Await.result(multilinkClient.start(username, password), 0.nanos)
            println(result)

        }

      // Your program goes here!
    }
  }
}
