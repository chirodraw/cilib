import scalaz._

package object cilib {

  // Really want this? Should we use Show instances instead?
  def println[A](a: A) = System.out.println(a)

  type Step[F[_],A,B] = Kleisli[RVar,(Opt,Eval[F,A]),B]

  type Particle[S,F[_],A] = Entity[S,F,A]

  type Guide[S,F[_],A] = (List[Particle[S,F,A]], Particle[S,F,A]) => Step[F,A,Position[F,A]] // Should expand into a typeclass? Getter?

  type Selection[A] = (List[A], A) => List[A]

  type Y[A] = ReaderT[RVar, Opt, A]

  def positive(d: Double): Maybe[Double @@ Tags.Positive] =
    if (d > 0.0) Tag.subst(Maybe.just(d))
    else Maybe.empty

  def negative(d: Double): Maybe[Double @@ Tags.Negative] =
    if (d < 0.0) Tag.subst(Maybe.just(d))
    else Maybe.empty


  // Use Spire for this!
  def closed[A](point: A): Bound[A] =
    Closed(point)

  def open[A](point: A): Bound[A] =
    Open(point)


  // Find a better home for this
  implicit object DoubleMonoid extends Monoid[Double] {
    def zero = 0.0
    def append(a: Double, b: => Double) = a + b
  }

}
