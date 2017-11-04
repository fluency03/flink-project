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

    val result = edits.keyBy( _.getUser )
      .timeWindow(Time.seconds(5))
      .fold(("", 0L)) {
        (acc: (String, Long), event: WikipediaEditEvent) => {
          (event.getUser, acc._2 + event.getByteDiff)
        }
      }

    result.print

    // execute program
    env.execute("Wikipedia Edit Monitoring")
  }
}
