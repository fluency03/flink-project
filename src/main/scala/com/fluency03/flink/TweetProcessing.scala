package com.fluency03.flink

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.twitter.TwitterSource

object TweetProcessing {
  def main(args: Array[String]): Unit = {

    // Checking input parameters
    val params = ParameterTool.fromArgs(args)
    println("Usage: TwitterExample [--output <path>] " +
      "[--twitter-source.consumerKey <key> " +
      "--twitter-source.consumerSecret <secret> " +
      "--twitter-source.token <token> " +
      "--twitter-source.tokenSecret <tokenSecret>]")

    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // make parameters available in the web interface
    env.getConfig.setGlobalJobParameters(params)

    env.setParallelism(params.getInt("parallelism", 1))

    // get input data
    val streamSource: DataStream[String] =
      if (params.has(TwitterSource.CONSUMER_KEY) &&
        params.has(TwitterSource.CONSUMER_SECRET) &&
        params.has(TwitterSource.TOKEN) &&
        params.has(TwitterSource.TOKEN_SECRET)
      ) {
        env.addSource(new TwitterSource(params.getProperties))
      } else {
        print("Executing TwitterStream example with default props.")
        print("Use --twitter-source.consumerKey <key> --twitter-source.consumerSecret <secret> " +
          "--twitter-source.token <token> " +
          "--twitter-source.tokenSecret <tokenSecret> specify the authentication info."
        )
        // get default test text data
        env.fromElements(TweetExamples.TEXTS: _*)
      }

    lazy val mapper = new ObjectMapper with ScalaObjectMapper
    val tweets: DataStream[JsonNode] = streamSource
      .map( t => mapper.readValue[JsonNode](t) )
      .map( _.get("user").get("id") )

    // emit result
    if (params.has("output")) {
      tweets.writeAsText(params.get("output"))
    } else {
      println("Printing result to stdout. Use --output to specify output path.")
      tweets.print()
    }

    // execute program
    env.execute("Twitter Streaming Example")
  }


}
