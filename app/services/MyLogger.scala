package services

object MyLogger {
  def info(str: String): Unit = {
    println(Console.MAGENTA + str + Console.RESET)
  }

  def red(str: String): Unit = {
    println(Console.RED + str + Console.RESET)
  }

  def blue(str: String): Unit = {
    println(Console.BLUE + str + Console.RESET)
  }
}