package com.tofu101

import cats.effect.IO

package object types {
  type Init[+A] = IO[A]
}
