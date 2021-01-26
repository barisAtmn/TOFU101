package com.tofu101.algebras

import com.tofu101.logging.DefaultLogger
import cats.{FlatMap, Parallel}
import cats.effect.Sync
import tofu.MonadThrow
import derevo.derive
import tofu.data.derived.ContextEmbed
import tofu.higherKind.derived.representableK
import tofu.lift.Lift
import tofu.syntax.monadic._
import tofu.syntax.lift._
import tofu.higherKind.Mid

import scala.reflect.ClassTag

/**
  * implicits will be added from here
  *  Algebra
 **/
@derive(representableK)
trait SumLogic[F[_]] {
  def sum(param1: Int, param2: Int): F[Int]
}

/**
  * ContextEmbed mixins for typeclass companion
  * * to add contextual embedded instance
 **/
object SumLogic extends ContextEmbed[SumLogic] {
  def apply[F[_]: SumLogic]: SumLogic[F] = implicitly

  /**
    * F[_] is for monad transformation
    * */
  def make[I[_]: Sync: Parallel: DefaultLogger, F[_]: MonadThrow: Lift[I, *[_]]]
    : I[SumLogic[F]] = (new Logger[I, F] attach new SumLogicImpl[I, F]).pure[I]

  /**
    * Implementation of algebra
   **/
  private final class SumLogicImpl[I[_]: Sync: Parallel: DefaultLogger, F[_]: MonadThrow: Lift[
    I,
    *[_]
  ]] extends SumLogic[F] {

    override def sum(param1: Int, param2: Int): F[Int] = {
      Sync[I]
        .delay {
          (param1 + param2)
        }
        .lift[F]
    }

  }

  /**
    *
   **/
  private final class Logger[I[_]: Sync: Parallel: DefaultLogger, F[_]: FlatMap: Lift[
    I,
    *[_]
  ]](implicit ct: ClassTag[SumLogic[Any]])
      extends SumLogic[Mid[F, *]] {
    override def sum(param1: Int, param2: Int): Mid[F, Int] = { x =>
      DefaultLogger[I]
        .info(s"Trying to sum $param1 and $param2")
        .lift[F] *> x.flatTap(
        value => DefaultLogger[I].info(s"result ${value}").lift[F]
      )
    }
  }

}
