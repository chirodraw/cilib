package cilib

import _root_.scala.Predef.{any2stringadd => _}
import scalaz._
import scalaz.std.list._
import PSO._
import spire.algebra._
import spire.implicits._
import spire.syntax.module._

object Predef {

  // The function below needs the guides for the particle, for the standard PSO update
  // and will eventually live in the simulator
  def gbest[S,F[_]:Traverse](//:Memory:Velocity](
    w: Double,
    c1: Double,
    c2: Double,
    cognitive: Guide[S,F,Double],
    social: Guide[S,F,Double]
  )(implicit M: Memory[S,F,Double], V: Velocity[S,F,Double], MO: Module[F[Double],Double]): List[Particle[S,F,Double]] => Particle[S,F,Double] => Instruction[F,Double,Particle[S,F,Double]] =
    collection => x => for {
      cog     <- cognitive(collection, x)
      soc     <- social(collection, x)
      v       <- stdVelocity(x, soc, cog, w, c1, c2)
      p       <- stdPosition(x, v)
      p2      <- evalParticle(p)
      p3      <- updateVelocity(p2, v)
      updated <- updatePBest(p3)
    } yield updated

  def cognitive[S,F[_]:Traverse](//:Memory:Velocity](
    w: Double,
    c1: Double,
    cognitive: Guide[S,F,Double]
  )(implicit M: Memory[S,F,Double], V: Velocity[S,F,Double], MO: Module[F[Double],Double]): List[Particle[S,F,Double]] => Particle[S,F,Double] => Instruction[F,Double,Particle[S,F,Double]] =
    collection => x => {
      for {
        cog     <- cognitive(collection, x)
        v       <- singleComponentVelocity(x, cog, w, c1)
        p       <- stdPosition(x, v)
        p2      <- evalParticle(p)
        p3      <- updateVelocity(p2, v)
        updated <- updatePBest(p3)
      } yield updated
    }

  def social[S,F[_]:Traverse](//:Memory:Velocity](
    w: Double,
    c1: Double,
    social: Guide[S,F,Double]
  )(implicit M: Memory[S,F,Double], V: Velocity[S,F,Double], MO: Module[F[Double],Double]): List[Particle[S,F,Double]] => Particle[S,F,Double] => Instruction[F,Double,Particle[S,F,Double]] =
    collection => x => {
      for {
        soc     <- social(collection, x)
        v       <- singleComponentVelocity(x, soc, w, c1)
        p       <- stdPosition(x, v)
        p2      <- evalParticle(p)
        p3      <- updateVelocity(p2, v)
        updated <- updatePBest(p3)
      } yield updated
    }

  // This is only defined for the gbest topology because the "method" described in Edwin's
  // paper for alternate topologies _does not_ make sense. I can only assume that there is
  // some additional research that needs to be done to correctly create an algorithm to
  // apply gcpso to other topology structures. Stating that you simply "copy" something
  // into something else is not elegant and does not have a solid reasoning
  // attached to it.
  /*def gcpso[S,F[_]](//: Velocity: Memory](
    w: Double,
    c1: Double,
    c2: Double,
    cognitive: Guide[S,F,Double]
  )(implicit M:Memory[S,F,Double], V:Velocity[S,F,Double]): List[Particle[S,F,Double]] => Particle[S,F,Double] => StateT[({type l[a] = Instruction[F,Double,a]})#l, GCParams, Particle[S,F,Double]] =
    collection => x => {
      type I[A] = Instruction[F,Double,A]
      val S = StateT.stateTMonadState[GCParams, I]
      val hoist = StateT.StateMonadTrans[GCParams]
      val g = Guide.gbest[S,F]
      for {
        s       <- S.get
        gbest   <- hoist.liftM(g(collection, x))
        cog     <- hoist.liftM(cognitive(collection, x))
        isBest  <- hoist.liftM(Instruction.point(x._2 eq gbest))
        v       <- hoist.liftM(if (isBest) gcVelocity(x, gbest, w, s) else stdVelocity(x, gbest, cog, w, c1, c2)) // Yes, we do want reference equality
        p       <- hoist.liftM(stdPosition(x, v))
        p2      <- hoist.liftM(evalParticle(p))
        p3      <- hoist.liftM(updateVelocity(p2, v))
        updated <- hoist.liftM(updatePBest(p3))
        failure  <- hoist.liftM(Instruction.liftK(Fitness.compare(x._2, updated._2) map (_ eq x._2)))
        _       <- S.modify(params =>
          if (isBest) {
            params.copy(
              p = if (params.successes > params.e_s) 2.0*params.p else if (params.failures > params.e_f) 0.5*params.p else params.p,
              failures = if (failure) params.failures + 1 else 0,
              successes = if (!failure) params.successes + 1 else 0
            )
          } else params)
      } yield updated
    }*/

  def charged[S:Charge,F[_]:Traverse:Monad](
    w: Double,
    c1: Double,
    c2: Double,
    cognitive: Guide[S,F,Double],
    social: Guide[S,F,Double],
    distance: (Position[F,Double], Position[F,Double]) => Double,
    rp: Double,
    rc: Double
  )(implicit M:Memory[S,F,Double], V:Velocity[S,F,Double], MO: Module[F[Double],Double]): List[Particle[S,F,Double]] => Particle[S,F,Double] => Instruction[F,Double,Particle[S,F,Double]] =
    collection => x => for {
      cog     <- cognitive(collection, x)
      soc     <- social(collection, x)
      accel   <- acceleration(collection, x, distance, rp, rc)
      v       <- stdVelocity(x, soc, cog, w, c1, c2)
      p       <- stdPosition(x, v + accel)
      p2      <- evalParticle(p)
      p3      <- updateVelocity(p2, v)
      updated <- updatePBest(p3)
    } yield updated


}
