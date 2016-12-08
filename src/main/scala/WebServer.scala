import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}
import spray.json.DefaultJsonProtocol._


/**
  * Created by User on 12/8/2016.
  */

object WebServer extends App {

  final case class ServerInfo(host: String, port: Int)

  implicit val serverInfoFormat = jsonFormat2(ServerInfo)

  // needed to run the route
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val env = args(0)
  val config = ConfigFactory.load()

  class ZookeeperWatcher extends Watcher {

    override def process(event: WatchedEvent): Unit = {

    }
  }

  val zkConfig = config.getConfig("zookeeper").getConfig(env)
  val connectString = zkConfig.getString("host-port")
  val zkPath = zkConfig.getString("path")
  val zk = new ZooKeeper(connectString, 3000, new ZookeeperWatcher)

  val route =
    path("hello") {
      get {
        val servers = zk.getChildren(zkPath, false)
        val r = scala.util.Random
        if (servers.isEmpty) {
          complete((StatusCodes.InternalServerError, "no server available"))
        } else {
          val server = servers.get(r.nextInt(servers.size()))
          val data = zk.getData(zkPath + "/" + server, false, null)
          val rawInfo = new String(data).split(":")
          complete(ServerInfo(rawInfo.head, rawInfo(1).toInt))
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8082)
}