package com.test.mdc

import cats.Applicative
import cats.effect.{IO, IOApp}
import org.slf4j.{Logger, MDC}

import scala.jdk.CollectionConverters.MapHasAsJava

object Main extends IOApp.Simple {

  override def run: IO[Unit] = {
    implicit val ctx: Ctx = Ctx(123, "my-request-id")
    val a: ServiceA[IO]   = new ServiceAImpl[IO]
    val b: ServiceB[IO]   = new ServiceBImpl[IO]
    for {
      _ <- a.doA()
      _ <- b.doB()
    } yield ()
  }
}

trait ServiceA[F[_]] {
  def doA()(implicit ctx: Ctx): F[Unit]
}
case class ServiceAImpl[F[_]: Applicative]() extends ServiceA[F] {
  implicit val logger: Logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  override def doA()(implicit ctx: Ctx): F[Unit] = {
    MdcLog.info("A !!!")
  }
}

trait ServiceB[F[_]] {
  def doB()(implicit ctx: Ctx): F[Unit]
}
case class ServiceBImpl[F[_]: Applicative]() extends ServiceB[F] {
  implicit val logger: Logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  override def doB()(implicit ctx: Ctx): F[Unit] = {
    MdcLog.info("B !!!")
  }
}

object MdcLog {

  private def ctxLog(logFn: => Unit)(implicit ctx: Ctx): Unit = {
    try {
      MDC.setContextMap(
        List(
          Some("x-client-id"  -> ctx.clientId.toString),
          Some("x-request-id" -> ctx.requestId)
        ).flatten.toMap.asJava
      )
      logFn
    } finally {
      MDC.clear()
    }
  }

  def info[F[_]: Applicative](msg: => String)(implicit ctx: Ctx, l: Logger): F[Unit] = implicitly[Applicative[F]].pure(ctxLog(l.info(msg)))
}

case class Ctx(clientId: Long, requestId: String)
