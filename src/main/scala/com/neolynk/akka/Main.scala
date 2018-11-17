package com.neolynk.akka

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import com.neolynk.akka.actor.AccountActor.{Credit, Debit, ShowMeTheMoney}
import com.neolynk.akka.actor.AccountSupervisor
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Main class.
  */
object Main extends App {

  implicit val system = ActorSystem("account-actor-system")

  val supervisorActor: ActorRef = system.actorOf(Props[AccountSupervisor], "account-supervisor")

  supervisorActor.tell(Credit(50.0), ActorRef.noSender)
  supervisorActor.tell(Debit(20.0), ActorRef.noSender)

  val balance: Future[Any] = supervisorActor.ask(ShowMeTheMoney)(timeout = Timeout(1, TimeUnit.SECONDS), ActorRef.noSender)

  balance onComplete {
    case Success(bal: Double) => println(s"Balance is = $bal")
    case Failure(e) => println(s"Oops, $e")
  }

  supervisorActor.tell(Debit(40.0), ActorRef.noSender)

  Thread.sleep(500)

  supervisorActor.ask(ShowMeTheMoney)(timeout = Timeout(1, TimeUnit.SECONDS), ActorRef.noSender) onComplete {
    case Success(bal: Double) => println(s"Balance is = $bal")
    case Failure(e) => println(s"Oops, $e")
  }

  supervisorActor.tell("Lol, you are gonna restart", ActorRef.noSender)

  Thread.sleep(500)

  supervisorActor.ask(ShowMeTheMoney)(timeout = Timeout(1, TimeUnit.SECONDS), ActorRef.noSender) onComplete {
    case Success(bal: Double) => println(s"Balance is = $bal")
    case Failure(e) => println(s"Oops, $e")
  }
}
