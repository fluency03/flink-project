package org.example

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditsSource
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent

/**
 * Wikipedia Edit Monitoring
 */
object WikipediaEditMonitoring {
  def main(args: Array[String]) {
    // set up the execution environment
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    val edits: DataStream[WikipediaEditEvent] = env.addSource(new WikipediaEditsSource)

    // fold is deprecated, use [[aggregate()]] instead
//    val result = edits
//      .keyBy( _.getUser )
//      .timeWindow(Time.seconds(5))
//      .fold(("", 0L)) {
//        (acc: (String, Long), event: WikipediaEditEvent) => {
//          (event.getUser, acc._2 + event.getByteDiff)
//        }
//      }

    val result = edits
      .map( e => UserWithEdits(e.getUser, e.getByteDiff) )
      .keyBy( "user" )
      .timeWindow(Time.seconds(5))
      .sum("edits")

    result.print

    // execute program
    env.execute("Wikipedia Edit Monitoring")
  }

  /** Data type for words with count */
  case class UserWithEdits(user: String, edits: Long)
}
