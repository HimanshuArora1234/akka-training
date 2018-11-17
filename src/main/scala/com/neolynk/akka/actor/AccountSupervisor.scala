package com.neolynk.akka.actor

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, SupervisorStrategy}
import com.neolynk.akka.actor.AccountActor.OutOfMoneyException

/**
  * Parent and supervisor of [[AccountActor]].
  */
class AccountSupervisor extends Actor with ActorLogging {


  // child creation
  val accountActor: ActorRef = context.actorOf(AccountActor.props, "account-actor")

  override def receive: Receive = {
    case message: Any => accountActor.forward(message)
  }

  // strategy to supervise children
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case e: OutOfMoneyException => Resume
    case _: Throwable => Restart
  }



  // Pre/post start/stop hooks
  override def preStart() = println("The Supervisor is ready to supervise")
  override def postStop() = println("Bye Bye from the Supervisor")

}
