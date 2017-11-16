import java.io.{FileOutputStream, PrintWriter}

import org.apache.hadoop.hive.ql.exec.spark.session.SparkSession
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._

object L2APDriverPipe {

  /** Makes sure only ERROR messages get logged to avoid log spam. */
  def setupLogging() = {
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
  }

  /** Our main function where the action happens */
  def main(args: Array[String]) {



    // Set up a Spark streaming context  that runs locally using
    // all CPU cores and one-second batches of data
    val sc = new SparkContext("local[*]", "tempPipe")

    // Get rid of log spam (should be called after the context is set up)
    setupLogging()


    val scriptPath = "/home/shalin/Downloads/l2ap/build/apss"

    val dataRDD = sc.textFile("/home/shalin/Downloads/l2ap/build/example.clu", 1)

    val originalRDD = dataRDD.cache()

    val header = dataRDD.first()

    val xyz = header.split(" ")

    val ogData = originalRDD.filter(row => row != header)

    println(xyz(0)+ xyz(1))

    val writer = new PrintWriter(new FileOutputStream("/home/shalin/Downloads/l2ap/build/cluHeaders.txt", false))

    val thresholdV =   0.6;

    writer.write(xyz(1)+" "+xyz(2)+"\n"+thresholdV)

    writer.close()

    val pipeRDD = ogData.pipe(scriptPath)

    val writer1 = new PrintWriter(new FileOutputStream("/home/shalin/Desktop/finalOutput.txt", false))



    pipeRDD.collect().foreach(x => writer1.write(x+"\n"))
    writer1.close()
    //dataRDD.collect().map(println(_))

  }
}