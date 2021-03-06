package be.nextlab.sbt.neo4j

import sbt._
import Keys._

import org.neo4j.kernel.GraphDatabaseAPI
import org.neo4j.server.ServerTestUtils
import org.neo4j.server.Bootstrapper
import org.neo4j.server.WrappingNeoServerBootstrapper
import org.neo4j.server.configuration.Configurator
import org.neo4j.server.configuration.ServerConfigurator

//import scalax.file._

import scala.collection.JavaConversions._

object Neo4JPlugin extends Plugin{

  val neo4jTestSettings:Seq[Setting[_]] = Seq(
    testOptions in Test += Tests.Setup( () => start()),
    testOptions in Test += Tests.Cleanup( () => stop())
  )

  var serverAndGraphDB:Option[(Bootstrapper, GraphDatabaseAPI)] = None

  def start() {
    val props:java.util.Map[String, String] = new java.util.HashMap[String, String]
    val graphdb = ServerTestUtils.EPHEMERAL_GRAPH_DATABASE_FACTORY.createDatabase(null, props)

    println("Graph DB created")

    val config = new ServerConfigurator(graphdb)
    config.configuration().setProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, 17474 )
    val neo4JServer = new WrappingNeoServerBootstrapper(graphdb, config)
    println("Neo4j Server created")

    neo4JServer.start()
    println("Neo4j Server started")
    graphdb.index();
    println("Graph indexer created")
    serverAndGraphDB = Some((neo4JServer, graphdb))
  }

  def stop() {
    serverAndGraphDB.foreach { case (neo4JServer, graphdb) =>
      graphdb.shutdown()
      neo4JServer.stop()

      Thread.sleep(100)
    }
  }

}