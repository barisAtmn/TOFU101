package com.tofu101

import com.tofu101.algebras.SumLogic
import com.tofu101.logging.DefaultLogger
import tofu.WithLocal
import tofu.optics.Contains
import tofu.optics.macros.ClassyOptics
import tofu.syntax.monadic._
import tofu.concurrent.ContextT

package object jobcontext {
  @ClassyOptics
  case class JobEnvironment[F[_]](logging: DefaultLogger[F],
                                  sumLogic: SumLogic[F])

  object JobEnvironment {
    implicit def contains[F[_], X](
      implicit
      lens: JobEnvironment[F] Contains X,
      ctx: F WithLocal JobEnvironment[F]
    ): F WithLocal X =
      ctx.subcontext(lens)
  }

  case object JobConfig

}
