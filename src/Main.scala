import java.io._
import java.net._
import java.util._
import org.jsoup.Jsoup
import org.jsoup.nodes._
import org.jsoup.select._
import org.json.JSONObject

object Main {

  @throws(classOf[IOException])
  def main(args: Array[String]): Unit = {

    val username = readLine("Enter the twitter username: ").toString.trim
    val page = readLine("Enter the number of pages: ").trim.toInt

    var tweetWordSrt = getTweet(username, page)

    tweetWordSrt._1.toSeq.sortWith(_._2 > _._2).foreach(println)

    println(s"${tweetWordSrt._2} processed tweet.")

  }

  @throws(classOf[IOException])
  def getTweet(usr: String, page: Int = 10) = {

    var (pos: String, tweetCount: Int) = Pair("", 1)

    val pattern = "[A-Za-z]+".r

    var tweetWords = Seq[String]()

    for(x <- 1 to page){

      var url = s"https://twitter.com/i/profiles/show/${usr}/timeline/tweets?max_position=${pos}"

      val json = Jsoup.connect(url).userAgent("Mozilla/5.0").ignoreContentType(true).execute.body

      val html = new JSONObject(json).getString("items_html")

      val parse = Jsoup.parse(html)

      val tweets = parse.select("li[data-item-type=tweet]").toArray

      tweets.foreach{tweet =>
        tweet.asInstanceOf[Element].select("div.js-tweet-text-container p").text.split(" ").foreach{t =>
          val tw = pattern.findFirstIn(t.toString.trim).mkString
          if(!tw.equals(""))
            tweetWords = tweetWords :+ tw
        }
        tweetCount += 1
        pos = tweet.asInstanceOf[Element].attr("data-item-id").toString
      }

    }

    (tweetWords.groupBy(w => w).mapValues(w => w.size), tweetCount)

  }


}
