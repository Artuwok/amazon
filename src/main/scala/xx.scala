import argonaut.Parse
import com.ning.http.client
import dispatch._, Defaults._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object xx {

  def main(args: Array[String]): Unit = {

    makeReaquest("sdfsd", "sdfsdf", "qqq")

    def makeReaquest(inputLang: String, outputLang: String, text: String) = {
      val svc = url("https://www.google.com.ua").POST
      svc.setContentType("application/json", "UTF-8")
      svc.addParameter("input_lang", inputLang)
      svc.addParameter("output_lang", outputLang)
      svc.addParameter("text", text)


      val http = Http.configure(_.setRequestTimeout(1000))
      val response: Future[String] = http(svc OK as.String )
      response onComplete {
        case Success(content) => {
          println("Successful response" + content)
          Parse.parseWith(content, _.field("greeting").flatMap(_.string).getOrElse(null), msg => msg)
        }
        case Failure(t) => {
          println("An error has occurred: " + t.getMessage)
        }
      }
    }
  }



}
