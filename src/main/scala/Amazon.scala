import argonaut.Parse
import com.ning.http.client
import dispatch.{Http, url}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.json4s.JsonAST._
import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/*
1) Finding 1000 most active users (profile names)

2) Finding 1000 most commented food items (item ids).

3) Finding 1000 most used words in the reviews*/

// https://www.cloudera.com/documentation/enterprise/5-5-x/topics/spark_develop_run.html


// TODO fix parsing to Int
case class Review(Id: String, ProductId: String, UserId: String, ProfileName: String, text: String)

object Amazon {

  val rest = "https://api.google.com/translate"
  def makeRequest(r: Review, num: Int): Review = {
    val  inputLang = "en"
    val outputLang = "fr"
    val svc = url(rest).POST
    svc.setContentType("application/json", "UTF-8")
    svc.addParameter("input_lang", inputLang)
    svc.addParameter("output_lang", outputLang)
    svc.addParameter("text", r.text)

    val response: Future[client.Response] = Http(svc)
    response.onComplete {
      case Success(content) => {
        println("Successful response" + content)
      }
      case Failure(t) => {
        println("An error has occurred: " + t.getMessage)
      }
    }
    Review(r.Id, r.ProductId, r.UserId, r.ProfileName, " ")
  }

  def main(args: Array[String]): Unit = {

    def mapper(line: String): Review = {
      val fields = line.split(',')
      val review: Review = Review(fields(0), fields(1), fields(2), fields(3), fields(9))
      review
    }

    val spark = SparkSession
      .builder
      .appName("Amazon")
      .master("local[4]")
      .getOrCreate()

    spark.conf.set("spark.executor.memory", "0.5g")


    import spark.implicits._
    val lines: RDD[String] = spark.sparkContext.textFile("/mnt/01D0AD212457E350/projects/amazon/src/resources/Reviews.csv")
    val review: Dataset[Review] = lines.map(mapper).toDS().cache()


    println("Here is our inferred schema:")
    review.printSchema()


    println("Let's select 1000 most active users:")
    review.groupBy("ProfileName").count().sort(desc("count")).show(1000)

    println("Let's find 1000 most commented food items:")
    review.groupBy("ProductId").count().sort(desc("count")).show(1000)

    println(" Finding 1000 most used words in the reviews:")

    val result: Unit = review
      .select("text").flatMap(_.getAs[String]("text")
      .toLowerCase
      .split("\\W+"))
      .toDF() // Convert to DataFrame to perform aggregation / sorting
      .groupBy($"value") // Count number of occurences of each word
      .agg(count("*") as "numOccurances")
      .orderBy($"numOccurances" desc).show() // Show most common words first


    val z: RDD[Review] = review.rdd
      z.map((r: Review) => makeRequest(r, z.getNumPartitions)).count()
    print(z.getNumPartitions)
    spark.stop()

  }
}


