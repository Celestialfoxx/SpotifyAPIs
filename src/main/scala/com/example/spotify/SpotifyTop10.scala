package com.example.spotify

import sttp.client3._
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object SpotifyTop10 extends App {
  // 创建异步HTTP请求客户端
  implicit val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

  val accessToken = "BQAotzaCab5mgBMPKpl2ke7ghUiPDUXYQ9hM8Y2o4U-nOyxS3kwACPoetuFg8v_TddVPh-YOqptSvsM01KfTbDxMpyLTYoBjQPCHdSR2lIt0N3fBRtw"

  val playlistId = "5Rrf7mqN8uus2AaQQQNdc1"
  val apiUrl = s"https://api.spotify.com/v1/playlists/$playlistId/tracks?market=US"

  // 构建请求
  val request = basicRequest
    .header("Authorization", s"Bearer $accessToken")
    .get(uri"$apiUrl")

  // 发送请求并处理响应
  val futureResponse = request.send()

  futureResponse.onComplete {
    case Success(response) => response.body match {
      case Right(body) =>
        val tracks = ujson.read(body)("items").arr
        val topTracks = tracks.sortBy(track => -track("track")("duration_ms").num).take(10)
        val artistNames = topTracks.map { track =>
          val artistName = track("track")("artists")(0)("name").str
          artistName
        }.toList

        topTracks.zipWithIndex.foreach { case (track, index) =>
          val trackName = track("track")("name").str
          val durationMs = track("track")("duration_ms").num.toLong
          val artistName = track("track")("artists")(0)("name").str
          println(s"Song${index + 1}: $trackName, $durationMs ms")
        }

        // artisit
        println("\nArtist Names of Top 10 Tracks:")
        artistNames.foreach(println)

      case Left(error) => println(s"An error occurred: $error")
    }
    case Failure(exception) => println(s"Request failed: $exception")
  }

}
