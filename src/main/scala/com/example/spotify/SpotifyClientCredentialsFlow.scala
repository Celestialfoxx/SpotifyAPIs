package com.example.spotify

import sttp.client3._
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object SpotifyClientCredentialsFlow extends App {
  // 创建异步HTTP请求客户端
  implicit val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()

  val clientId = "79651143cb6240cd84b9baa998fc1e7c"
  val clientSecret = "f31387af37e94e9ea42f0002f94162c2"

  // 将客户端ID和密钥编码为Base64
  val clientCredentials = java.util.Base64.getEncoder.encodeToString(s"$clientId:$clientSecret".getBytes("UTF-8"))

  // 准备请求
  val request = basicRequest
    .header("Authorization", s"Basic $clientCredentials")
    .header("Content-Type", "application/x-www-form-urlencoded")
    .post(uri"https://accounts.spotify.com/api/token")
    .body("grant_type=client_credentials")

  // 发送请求并处理响应
  val futureResponse = request.send().map { response =>
    response.body match {
      case Right(body) => println(s"Received token: $body")
      case Left(error) => println(s"Failed to obtain token: $error")
    }
  }

  // 确保应用程序不会在Future完成前退出
  futureResponse.onComplete {
    case Success(_) => println("Request completed successfully.")
    case Failure(exception) => println(s"Request failed: $exception")
  }
}

