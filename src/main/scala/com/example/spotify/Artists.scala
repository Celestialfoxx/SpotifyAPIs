package com.example.spotify

import com.example.spotify.SpotifyTop10.request
import sttp.client3._
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Artists extends App {
    // 创建异步HTTP请求客户端
    implicit val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

    val accessToken = "BQAotzaCab5mgBMPKpl2ke7ghUiPDUXYQ9hM8Y2o4U-nOyxS3kwACPoetuFg8v_TddVPh-YOqptSvsM01KfTbDxMpyLTYoBjQPCHdSR2lIt0N3fBRtw"
    val playlistId = "5Rrf7mqN8uus2AaQQQNdc1"
    val apiUrl = s"https://api.spotify.com/v1/playlists/$playlistId/tracks?market=US"

    // 发送请求并处理响应
    val request = basicRequest
      .header("Authorization", s"Bearer $accessToken")
      .get(uri"$apiUrl")
    val futureResponse = request.send()

  futureResponse.onComplete {
    case Success(response) => response.body match {
      case Right(body) =>
        val tracks = ujson.read(body)("items").arr
        val topTracks = tracks.sortBy(track => -track("track")("duration_ms").num).take(10)
        val artistIds = topTracks.flatMap { track =>
          track("track")("artists").arr.map(_.obj("id").str)
        }.distinct

        // 获取每个艺术家的详细信息
        val artistDetailsFutures = artistIds.map(fetchArtistDetails)
        Future.sequence(artistDetailsFutures).onComplete {
          case Success(artistDetails) =>
            val sortedArtists = artistDetails.sortBy(-_._2)
            sortedArtists.foreach { case (name, followers) =>
              println(s"$name : $followers")
            }

          case Failure(exception) => println(s"Failed to fetch artist details: $exception")
        }

      case Left(error) => println(s"An error occurred: $error")
    }
    case Failure(exception) => println(s"Request failed: $exception")
  }

  def fetchArtistDetails(artistId: String): Future[(String, Int)] = {
    basicRequest
      .header("Authorization", s"Bearer $accessToken")
      .get(uri"https://api.spotify.com/v1/artists/$artistId")
      .send()
      .map { response =>
        response.body match {
          case Right(body) =>
            val json = ujson.read(body)
            val name = json("name").str
            val followers = json("followers")("total").num.toInt
            (name, followers)
          case Left(error) =>
            println(s"An error occurred while fetching artist details: $error")
            ("", 0)
        }
      }
  }
}
