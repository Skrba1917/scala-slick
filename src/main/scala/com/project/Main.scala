package com.project

import java.time.LocalDate
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util._

object PrivateExecutionContext {
  val executor = Executors.newFixedThreadPool(4)
  implicit val executionContext = ExecutionContext.fromExecutorService(executor)
}

object Main {
  import slick.jdbc.PostgresProfile.api._
  import PrivateExecutionContext._

  val shawshankRedemption = Movie(1L, "The Shawshank Redemption", LocalDate.of(1994, 9, 23), 162)
  val amazingSpiderman = Movie(2L, "The Amazing Spiderman", LocalDate.of(2012, 6, 3), 136)
  val allQuietOnWesternFront = Movie(3L, "All Quiet On Western Front", LocalDate.of(2022, 9, 29), 148)

  def demoInsertMovie(movie: Movie): Unit = {
    val queryDescription = SlickTables.movieTable += movie
    val futureId: Future[Int] = Connection.db.run(queryDescription)
    futureId.onComplete {
      case Success(newMovieId) => println(s"The query was successfull, new id is $newMovieId")
      case Failure(ex) => println(s"The query failed due to $ex")
    }
    Thread.sleep(5000)
  }

  def demoReadAllMovies(): Unit = {
    val resultFuture: Future[Seq[Movie]] = Connection.db.run(SlickTables.movieTable.result) // select * from table
    resultFuture.onComplete {
      case Success(movies) => println(s"Found: ${movies.mkString(",")}")
      case Failure(ex) => println(s"Couldn't find movies due to $ex")
    }
    Thread.sleep(5000)
  }

  def demoReadSomeMovies(): Unit = {
    val resultFuture: Future[Seq[Movie]] = Connection.db.run(SlickTables.movieTable.filter(_.name.like("%The Shawshank Redemption%")).result) // select * from table where name like The Amazing Spiderman
    resultFuture.onComplete {
      case Success(movies) => println(s"Found: ${movies.mkString(",")}")
      case Failure(ex) => println(s"Couldn't find movies due to $ex")
    }
    Thread.sleep(5000)
  }

  def demoUpdate(): Unit = {
    val queryDescription = SlickTables.movieTable.filter(_.id === 1L).update(shawshankRedemption.copy(lengthInMin = 150))
    val futureId: Future[Int] = Connection.db.run(queryDescription)
    futureId.onComplete {
      case Success(newMovieId) => println(s"The query was successfull, new id is $newMovieId")
      case Failure(ex) => println(s"The query failed due to $ex")
    }
    Thread.sleep(5000)
  }

  def demoDelete(): Unit = {
    Connection.db.run(SlickTables.movieTable.filter(_.name.like("%The Shawshank Redemption%")).delete)
    Thread.sleep(5000)
  }

  def main(args: Array[String]): Unit = {
    demoInsertMovie(shawshankRedemption)
    demoReadAllMovies()
    demoReadSomeMovies()
    demoUpdate()
    demoDelete()
  }
}
