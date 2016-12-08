name := "jp-mahjong-lb"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-http_2.11" % "10.0.0",
  "com.typesafe.akka" % "akka-http-spray-json_2.11" % "10.0.0",
  "org.apache.zookeeper" % "zookeeper" % "3.4.9"
)

    