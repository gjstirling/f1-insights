package runners

import services.MyLogger

object Runner {

  def main(args: Array[String]): Unit = {

    MyLogger.info("THIS IS MY JOB IM RUNNING")

  }
}