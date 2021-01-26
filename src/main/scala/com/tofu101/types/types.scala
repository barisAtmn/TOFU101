package com.tofu101

import cats.effect.IO
import jobcontext.JobEnvironment
import tofu.concurrent.ContextT
import tofu.syntax.monadic._

package object types {
  type Init[+A] = IO[A]
  type Execution[+A] = ContextT[IO, JobEnvironment, A]
}
