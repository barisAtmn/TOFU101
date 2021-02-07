package com.tofu101

import algebras.SumLogic
import cats.Parallel
import cats.implicits._
import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp, Resource, Sync}
import com.tofu101.logging.DefaultLogger
import natchez.TraceValue.StringValue
import natchez.{EntryPoint, Kernel, Span, Tags, Trace}
object Application extends IOApp {

  def entryPoint[F[_]: Sync]: Resource[F, EntryPoint[F]] = {
    import natchez.jaeger.Jaeger
    import io.jaegertracing.Configuration.SamplerConfiguration
    import io.jaegertracing.Configuration.ReporterConfiguration
    Jaeger.entryPoint[F]("SumLogic_Tracing") { c =>
      Sync[F].delay {
        c.withSampler(SamplerConfiguration.fromEnv)
          .withReporter(ReporterConfiguration.fromEnv)
          .getTracer
      }
    }
  }

  def program[F[_]: Sync: Parallel: Trace] = Trace[F].span("program span") {
    for {
      implicit0(logger: DefaultLogger[F]) <- DefaultLogger.makeI[F, F]
      implicit0(sumLogic: SumLogic[F]) <- SumLogic.make[F, F]
      result <- SumLogic[F](sumLogic).sum(3, 5)
      _ <- Trace[F]
        .put(Tags.error(false))
        .flatMap(
          _ =>
            Trace[F].put(
              (
                "dependencies",
                StringValue(
                  "Default logger and sum logic got injected successfully!"
                )
              )
          )
        )
    } yield result
  }

  override def run(args: scala.List[String]): IO[ExitCode] = {
    entryPoint[IO].use { ep =>
      ep.continueOrElseRoot(
          "this is the root span",
          Kernel(Map("uber-trace-id" -> "f26cc:f26cc:0:1"))
        )
        .use { span =>
          program[Kleisli[IO, Span[IO], *]].run(span)
        }
        .as(ExitCode.Success)
    }
  }

}
