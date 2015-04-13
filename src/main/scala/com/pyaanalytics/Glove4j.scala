package com.pyaanalytics

import org.deeplearning4j.bagofwords.vectorizer.{TextVectorizer, TfidfVectorizer}
import org.deeplearning4j.text.sentenceiterator.{LineSentenceIterator, SentenceIterator, SentencePreProcessor}
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache
import org.deeplearning4j.text.tokenization.tokenizerfactory.{DefaultTokenizerFactory, TokenizerFactory}
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.EndingPreProcessor
import org.nd4j.linalg.factory.Nd4j
import org.deeplearning4j.models.glove.CoOccurrences
import org.deeplearning4j.models.glove.{Glove, GloveWeightLookupTable}
import org.deeplearning4j.berkeley.Pair
import org.deeplearning4j.util.SerializationUtils
import org.springframework.core.io.ClassPathResource;

import java.io.{File, FileOutputStream, PrintWriter}
import scopt.OptionParser
import scala.collection.JavaConversions._
import scala.io.Source

object Glove4j {
  case class Glove4jConfig(abstractsFile: String = "",
                           vocabFile: String = "",
                           weightFile: String = "",
                           sparkMaster: String = "local[64]")

  def main(args: Array[String]): Unit = {

    val parser = new OptionParser[Glove4jConfig]("Glove4j") {

      arg[String]("abstractsFile") valueName("abstractsFile") action {
        (x, c) => c.copy(abstractsFile = x)
      }

      arg[String]("vocabFile") valueName("vocabFile") action {
        (x, c) => c.copy(vocabFile = x)
      }

      arg[String]("weightFile") valueName("weightFile") action {
        (x, c) => c.copy(vocabFile = x)
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

    class SentPP() extends SentencePreProcessor {
      override def preProcess(sentence: String) = {
        sentence.toLowerCase()
      }
    }

    val inFile = Source.fromFile(config.abstractsFile)
    val iter = new LineSentenceIterator(new File(config.abstractsFile))
    var cache = new InMemoryLookupCache()
    var t = new DefaultTokenizerFactory()
    var tfidf = new TfidfVectorizer.Builder().cache(cache)
      .iterate(iter)
      .minWords(1)
      .tokenize(t)
      .build()

    tfidf.fit()
    val preProcessor = new EndingPreProcessor()
    t.setTokenPreProcessor(preProcessor)

    val layerSize = 1000
    Nd4j.ENFORCE_NUMERICAL_STABILITY = true

    var c = new CoOccurrences.Builder()
      .cache(cache)
      .iterate(iter)
      .tokenizer(t)
      .build()
    c.fit()

    var table = new GloveWeightLookupTable.Builder()
      .cache(cache)
      .lr(0.005)
      .build()

    var vec = new Glove.Builder()
      .learningRate(0.005)
      .batchSize(1000)
      .cache(cache)
      .coOccurrences(c)
      .cache(cache)
      .iterations(30)
      .vectorizer(tfidf)
      .weights(table)
      .layerSize(layerSize)
      .iterate(iter)
      .tokenizer(t)
      .minWordFrequency(30)
      .symmetric(true)
      .windowSize(15)
      .build()
    vec.fit()


    println(vec.wordsNearest("heart", 20))

    // SerializationUtils.writeObject(vec.vocab(), new FileOutputStream(config.vocabFile))
    // SerializationUtils.writeObject(vec.lookupTable(), new FileOutputStream(config.vocabFile))

    val vocab = vec.vocab()
    val p = new PrintWriter(new File(config.vocabFile))
    vocab.words.toList foreach { word =>
      p.println(word ++ vec.getWordVector(word).toVector.mkString(" "))
    }
    p.close()


    println("Model saved")
  }
}
