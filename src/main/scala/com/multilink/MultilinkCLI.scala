package com.multilink

import com.monovore.decline.*
import com.multilink.MultilinkCLI.ConnectCommand

object MultilinkCLI {

  sealed trait Command

  case class ConnectCommand(address: Int) extends Command

  import scala.io.StdIn.readLine
  val cli = new MultilinkCLI()

  def run(): Unit = {
    while(true) {
      print("multilink> ")
      val line = readLine()
      val lines = line.split(' ')
      cli.parse(lines)
    }
  }
}

class MultilinkCLI {

  val address = Opts.argument[Int](metavar = "address")

  val connectCommand = Command(
    name = "connect",
    header = "Connect to a server"
    ) {
    address
  }.map(address => ConnectCommand(address))

  val multilinkCommand = Command(
    name = "multilink",
    header = "Multilink CLI"
    ) {
    Opts.subcommand(connectCommand)
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
        println(s"Address: $parsedValue")
      // Your program goes here!
    }
  }
}
