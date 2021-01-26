package com.tofu101.logging

import cats.effect.Sync
import derevo.derive
import tofu.data.derived.ContextEmbed
import tofu.higherKind.derived.representableK
import tofu.lift.Lift
import tofu.logging.{Logging, Logs}
import tofu.syntax.monadic._
import tofu.syntax.lift._
import tofu.syntax.embed._

import scala.reflect.ClassTag

// Annotation for deriving of RepresentableK instance, which is used by ContextT
@derive(representableK)
trait DefaultLogger[F[_]] {

  /** Send to logs Info msg
    * A bit dirty algebra to log something. Need and Idea how to not implicitly request ClassTag
    *
    * @param msg Message for logs
    * @return
    */
  def info[M: ClassTag](msg: String): F[Unit]
}
object DefaultLogger extends ContextEmbed[DefaultLogger] {

  /** Summon JobLogging instance from context
    *
    * @tparam F Abstract effect
    * @return
    */
  def apply[F[_]: DefaultLogger]: DefaultLogger[F] =
    implicitly[DefaultLogger[F]]

  /** Make instance of DefaultLogger with 1 effect
    *
    * @tparam F Abstract effect
    * @return
    */
  def make[F[_]: Sync]: DefaultLogger[F] = new LoggerImpl[F, F].init.embed

  /** Make instance of DefaultLogger with I[_] as Init effect and F[_] as working effect
    *
    * @tparam I Initialization effect
    * @tparam F Execution effect
    * @return
    */
  def makeI[I[_]: Sync, F[_]: Sync: Lift[I, *[_]]]: I[DefaultLogger[F]] =
    new LoggerImpl[I, F].init

  private final class LoggerImpl[I[_]: Sync, F[_]: Sync: Lift[I, *[_]]] {
    private val logs = Logs.sync[I, F]
    private def logInfo[M: ClassTag](msg: String): I[F[Unit]] =
      logs.forService[M].map(log => log.info(msg))

    private[LoggerImpl] final class Internal extends DefaultLogger[F] {

      /** Send to logs Info msg
        * A bit dirty algebra to log something. Need and Idea how to not implicitly request ClassTag
        *
        * @param msg Message for logs
        * @return
        */
      override def info[M: ClassTag](msg: String): F[Unit] =
        logInfo[M](msg).lift[F].flatten
    }

    def init: I[DefaultLogger[F]] = (new Internal: DefaultLogger[F]).pure[I]
  }

}
