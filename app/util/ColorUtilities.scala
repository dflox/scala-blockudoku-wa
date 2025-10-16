package util;

import javax.inject.Singleton;

@Singleton
class ColorUtilities {
    private val ansiPattern = "\u001B\\[[;\\d]*m".r

    private val fgColorMap = Map(
      30 -> "black",
      31 -> "red",
      32 -> "green",
      33 -> "yellow",
      34 -> "blue",
      35 -> "magenta",
      36 -> "cyan",
      37 -> "white",
      90 -> "gray",
      91 -> "lightcoral",
      92 -> "lightgreen",
      93 -> "lightyellow",
      94 -> "lightblue",
      95 -> "violet",
      96 -> "lightcyan",
      97 -> "white"
    )

    private val bgColorMap = Map(
      40 -> "black",
      41 -> "red",
      42 -> "green",
      43 -> "yellow",
      44 -> "blue",
      45 -> "magenta",
      46 -> "cyan",
      47 -> "white",
      100 -> "gray",
      101 -> "lightcoral",
      102 -> "lightgreen",
      103 -> "lightyellow",
      104 -> "lightblue",
      105 -> "violet",
      106 -> "lightcyan",
      107 -> "white"
    )

    def convertConsoleToHTML(input: String): String = {
      val sb = new StringBuilder
      var currentFg: Option[String] = None
      var currentBg: Option[String] = None
      var openSpan = false

      def closeSpan(): Unit = {
        if (openSpan) {
          sb.append("</span>")
          openSpan = false
        }
      }

      def openSpanIfNeeded(): Unit = {
        if (currentFg.nonEmpty || currentBg.nonEmpty) {
          val style = Seq(
            currentFg.map(c => s"color: $c;"),
            currentBg.map(c => s"background-color: $c;")
          ).flatten.mkString(" ")
          sb.append(s"""<span style="$style">""")
          openSpan = true
        }
      }

      ansiPattern.split(input).zipAll(ansiPattern.findAllIn(input).toList, "", "").foreach {
        case (text, code) =>
          if (text.nonEmpty) sb.append(scala.xml.Utility.escape(text))
          if (code.nonEmpty) {
            val nums = code.stripPrefix("\u001b[").stripSuffix("m").split(";").flatMap(
              _.toIntOption)
            if (nums.isEmpty || nums.contains(0)) {
              // Reset all
              closeSpan()
              currentFg = None
              currentBg = None
            } else {
              var needsReopen = false
              closeSpan()
              nums.foreach {
                case n if fgColorMap.contains(n) =>
                  currentFg = Some(fgColorMap(n)); needsReopen = true
                case n if bgColorMap.contains(n) =>
                  currentBg = Some(bgColorMap(n)); needsReopen = true
                case 39 => currentFg = None; needsReopen = true // reset foreground
                case 49 => currentBg = None; needsReopen = true // reset background
                case _ => // ignore others
              }
              if (needsReopen) openSpanIfNeeded()
            }
          }
      }

      closeSpan()
      sb.toString()
    }

  //  def ConvertConsoleToHTML(input: String): String = {
//    if (input == null) {
//      return "";
//    }
//
//    input
//      .replace("\u001B[0m", "</span>")
//      .replace("\u001B[30m", "<span style=\"color:black\">")
//      .replace("\u001B[47m", "<span style=\"background-color:white\">");
//  }
}
