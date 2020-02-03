package util

object ColoredConsole {
    def error(msg: String) = {
        println(Console.BOLD + Console.RED_B + Console.WHITE + "[ERROR] " + msg + Console.RESET)
    }
    def info(msg: String) = {
        println(Console.YELLOW + "[INFO] " + msg + Console.RESET)
    }
    def color(msg: String, fg: String = Console.WHITE, bg: String = Console.BLACK_B) : String
        = bg + fg + msg + Console.RESET
    def log(msg: String, fg: String = Console.WHITE, bg: String = Console.BLACK_B) = {
        println(color(msg, fg, bg))
    }
}