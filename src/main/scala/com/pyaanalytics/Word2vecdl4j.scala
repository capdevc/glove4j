package com.pyaanalytics

import org.deeplearning4j.bagofwords.vectorizer.{TextVectorizer, TfidfVectorizer}
import org.deeplearning.berkeley.Pair
import org.springframework.core.io.ClassPathResource;

import java.io.File
import scopt.OptionParser
import scala.io.Source

object Glove4j {
  case class Glove4jConfig(abstractsFile: String = "",
                           modelFile: String = "",
                           sparkMaster: String = "local[64]")

  def main(args: Array[String]): Unit = {

    val parser = new OptionParser[Glove4jConfig]("Glove4j") {

      arg[String]("abstractsFile") valueName("abstractsFile") action {
        (x, c) => c.copy(abstractsFile = x)
      }


      arg[String]("modelFile") valueName("modelFile") action {
        (x, c) => c.copy(modelFile = x)
      }

      arg[String]("sparkMaster") valueName("sparkMaster") action {
        (x, c) => c.copy(sparkMaster = x)
      }
    }

    parser.parse(args, Glove4jConfig()) match {
      case Some(config) => {
        run(config)
      } case None => {
        System.exit(1)
      }
    }
  }
  def run(config: Glove4jConfig): Unit = {
    println("Model saved")
  }
}
