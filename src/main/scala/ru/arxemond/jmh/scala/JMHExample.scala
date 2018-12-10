package ru.arxemond.jmh.scala

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import ru.arxemond.jmh.scala.JMHExample._

/**
  * @author <a href="mailto:1arxemond1@gmail.com">Glushenkov Yuri</a>
  *
  * > sbt
  * > jmh:run -i 10 -wi 5 -f1 -t1
  * -i 10   - we want to run each benchmark with 10 iterations
  * -wi 5   - 5 warmup iterations
  * -f1     - fork once on each benchmark
  * -t1     - says to run on one thread
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class JMHExample {

  /** The answer example
    *
    * [info] Result "parSeqMeasure":
    * [info]   0.042 ±(99.9%) 0.001 ops/ms [Average]
    * [info]   (min, avg, max) = (0.022, 0.042, 0.045), stdev = 0.004
    * [info]   CI (99.9%): [0.041, 0.043] (assumes normal distribution)
    * [info]
    * [info] # Run complete. Total time: 00:27:56
    * [info]
    * [info] Benchmark                   Mode  Cnt  Score    Error   Units
    * [info] JMHExample.defMeasure      thrpt  200  0.015 ±  0.001  ops/ms
    * [info] JMHExample.onlySeqMeasure  thrpt  200  0.017 ±  0.001  ops/ms
    * [info] JMHExample.parSeq          thrpt  200  0.032 ±  0.001  ops/ms
    * [info] JMHExample.parSeqMeasure   thrpt  200  0.042 ±  0.001  ops/ms
    * [success] Total time: 1681 s, completed Dec 3, 2018 2:47:26 PM
    */

  @Benchmark
  def defMeasure(setState: SetState, blackhole: Blackhole): Unit = {
    val res: Array[Int] = setState.stream.filter(_ % 2 == 0).map(_ * 3).toArray

    blackhole.consume(res)
  }

  @Benchmark
  def onlySeqMeasure(setState: SetState, blackhole: Blackhole): Unit = {
    val res: Array[Int] = setState.stream.filter(_ % 2 == 0).seq.map(_ * 3).toArray

    blackhole.consume(res)
  }

  @Benchmark
  def parSeqMeasure(setState: SetState, blackhole: Blackhole): Unit = {
    val res: Array[Int] = setState.stream.par.filter(_ % 2 == 0).seq.map(_ * 3).toArray

    blackhole.consume(res)
  }

  @Benchmark
  def parSeq(setState: SetState, blackhole: Blackhole): Unit = {
    val res: Array[Int] = setState.stream.par.filter(_ % 2 == 0).map(_ * 3).toArray

    blackhole.consume(res)
  }

}

/**
  * @author <a href="mailto:1arxemond1@gmail.com">Glushenkov Yuri</a>
  */
object JMHExample {
  @State(Scope.Benchmark)
  class SetState {

    /**
      * Explicit predefined stream. This value will not involve to the next benchmark measure
      */
    final val stream: Stream[Int] = (1 to 1000000).toStream

    /**
      * This val will be predefined in the method {@link JMHExample#doSetup}
      * and cleared in the method  {@link JMHExample#tearDown}
      */
    var sum = 0

    /**
      * Refresh FOR EACH method. For example if there are 4-methods with the @Benchmark annotations, doSetup will be invoked 4 times
      *
      * @see <a href="http://tutorials.jenkov.com/java-performance/jmh.html#state-setup-and-teardown">Got from</a>
      *
      * You can annotate methods in your state class with the @Setup
      * and @TearDown annotations. The @Setup annotation tell JMH
      * that this method should be called to setup the state object
      * before it is passed to the benchmark method. The @TearDown
      * annotation tells JMH that this method should be called to
      * clean up ("tear down") the state object after the benchmark
      * has been executed.
      * */
    @Setup(Level.Trial)
    def doSetup(): Unit = {
      sum = scala.util.Random.nextInt( (10 - 1) + 1 )
      println(s"""setup  performed, sum: ${sum}""")
    }

    /**
      * Refresh FOR EACH method. For example if there are 4-methods with the @Benchmark annotations, doSetup will be invoked 4 times
      *
      * @see <a href="http://tutorials.jenkov.com/java-performance/jmh.html#state-setup-and-teardown">Got from</a>
      *
      * Level.Trial	The method is called once for each time for each full run of the benchmark.
      * A full run means a full "fork" including all warmup and benchmark iterations.
      * Level.Iteration	The method is called once for each iteration of the benchmark.
      * Level.Invocation	The method is called once for each call to the benchmark method.
      */
    @TearDown(Level.Trial)
    def tearDown(): Unit = {
      sum = 0
      println(s"""tearDown performed, sum: ${sum}""")
    }

  }
}