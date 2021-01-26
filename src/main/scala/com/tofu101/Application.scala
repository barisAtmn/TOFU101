package com.tofu101

import com.tofu101.logging.DefaultLogger
import algebras.SumLogic
import cats.{Monad, Monoid, Semigroup, Applicative}
import cats.effect.{ExitCode, IO, IOApp}
import jobcontext.JobEnvironment
import types.{Execution, Init}
import tofu.concurrent.ContextT
import cats.syntax.monoid._

import tofu.lift.Lift
import tofu.syntax.monadic._
import tofu.syntax.lift._
import cats.tagless.syntax.functorK._

object Application extends IOApp {

  /**
    *  mapK F[_] -> G[_]
    *  ContextT
    **/
  def initEnv: Init[JobEnvironment[Execution]] = {
    for {
      implicit0(logger: DefaultLogger[Init]) <- DefaultLogger.makeI[Init, Init]
      sumLogic <- SumLogic.make[Init, Execution]
    } yield JobEnvironment(logger.mapK[Execution](ContextT.liftF), sumLogic)
  }

  def program[F[_]: Monad: SumLogic] = {
    for {
      _ <- SumLogic[F].sum(3, 5)
    } yield ()
  }

  override def run(args: scala.List[String]): IO[ExitCode] =
    initEnv.flatMap(env => program[Execution].run(env)) *> IO(ExitCode.Success)
}
