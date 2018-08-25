package wiki.content

import models.animation.Animation
import wiki.MarkdownFile

object ContentHelper {
  private[this] val baseUrl = "https://github.com/KyleU/hawkthorne"

  def img(path: String, alt: String) = s"""[[$baseUrl/blob/master/public/game/images/$path|alt=$alt]]"""

  val keyValHeaders = Seq(('l', 40, "Key"), ('l', 40, "Value"))
  private[this] val animationHeaders = Seq(('l', 40, "ID"), ('l', 40, "Frames"), ('r', 8, "Delay"), ('l', 6, "Loop"))

  def link(title: String, url: String = "") = s"[$title](${if (url.isEmpty) { title } else { url }})"

  def table(md: MarkdownFile, cols: Seq[(Char, Int, String)], content: Seq[Seq[String]]) = {
    md.add("| " + cols.map(c => " " + c._3.padTo(c._2 - 1, " ").mkString).mkString("|").trim)
    md.add("|" + cols.map(c => (c._1 match {
      case 'l' => (0 until c._2).map(_ => "-")
      case 'r' => (0 until (c._2 - 1)).map(_ => "-") :+ ':'
      case 'c' => ':' +: (0 until c._2 - 2).map(_ => "-") :+ ':'
      case _ => throw new IllegalStateException("Please pass one of [l, r, c] for alignment.")
    }).mkString).mkString("|"))
    content.foreach { row =>
      md.add("| " + cols.zip(row).map(c => " " + c._2.padTo(c._1._2 - 1, " ").mkString).mkString("|").trim)
    }
  }

  def attributes(md: MarkdownFile, attrs: (String, String)*) = {
    md.add()
    md.add("## Attributes")
    ContentHelper.table(md = md, cols = keyValHeaders, content = attrs.map(attr => Seq(attr._1, attr._2)))
  }

  def animations(md: MarkdownFile, animations: Seq[Animation]) = {
    md.add()
    md.add("## Animations")
    ContentHelper.table(md = md, cols = animationHeaders, content = animations.map { anim =>
      Seq(anim.id, anim.frames.mkString(", "), anim.delay.toString, anim.loop.toString)
    })
  }
}
