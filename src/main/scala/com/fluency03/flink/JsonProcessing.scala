package com.fluency03.flink

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.apache.flink.streaming.api.scala._

case class Jsen(
                 @JsonProperty("a") a: String,
                 @JsonProperty("c") c: String,
                 @JsonProperty("e") e: String
               )

object JsonProcessing {

//  val mapper = new ObjectMapper

  def main(args: Array[String]) {

    // set up the execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // get input data
    val text = env.readTextFile("/Users/fluency03/Workplace/flink/flink-project/input/data")

    lazy val mapper = new ObjectMapper with ScalaObjectMapper
    val counts = text.map(l => mapper.readValue[Jsen](l) )

    // execute and print result
    counts.print()

    env.execute("JsonProcessing")
  }

}
