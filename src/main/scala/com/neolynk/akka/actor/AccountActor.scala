package com.neolynk.akka.actor

import akka.actor.{Actor, Props}
import com.neolynk.akka.actor.AccountActor._

/**
  * Account actor, contains the actual balance state and performs credit/debit ops.
  */
class AccountActor extends Actor {

  // Initial state
  private var balance = 0.0

  // Message processing
  override def receive: Receive = {

    case Debit(amount) =>
      if (balance - amount < 0) throw OutOfMoneyException("Sorry you are poor !!") else balance -= amount

    case Credit(amount) =>
      balance += amount

    case ShowMeTheMoney =>
      sender.tell(balance, self)

    // Simulate exception
    case _: String => throw new Exception("Random exception")

  }


  // Pre/post start/stop hooks
  override def preRestart(reason: Throwable, message: Option[Any]) = {
    println("Yo, I am restarting...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    println("...restart completed!")
    super.postRestart(reason)
  }

  override def preStart() = println("Yo, I am alive!")
  override def postStop() = println("Goodbye world!")

}

/**
  * Companion object.
  */
object AccountActor {

  // Factory of AccountActor, props for configuration
  def props: Props = Props[AccountActor]


  // Protocol of this actor
  case class Debit(amount: Double)
  case class Credit(amount: Double)
  case object ShowMeTheMoney

  case class OutOfMoneyException(message: String) extends Exception(message)

}



