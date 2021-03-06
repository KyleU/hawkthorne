package services.socket

import models.RequestMessage
import services.event.EventHandler
import util.{BinarySerializers, JsonSerializers, Logging}

class SocketConnection(key: String, val handler: EventHandler, val binary: Boolean) {
  protected[this] val socket = new NetworkSocket(handler)

  connect(s"/connect?binary=$binary")

  def connect(path: String) = {
    NetworkMessage.register(sendMessage)
    val url = websocketUrl(path)
    Logging.debug(s"Socket [$key] starting with url [$url].")
    socket.open(url)
  }

  private[this] def websocketUrl(path: String) = {
    val loc = org.scalajs.dom.document.location
    val wsProtocol = if (loc.protocol == "https:") { "wss" } else { "ws" }
    s"$wsProtocol://${loc.host}$path"
  }

  private[this] def sendMessage(rm: RequestMessage): Unit = {
    if (socket.isConnected) {
      if (binary) {
        socket.sendBinary(BinarySerializers.writeRequestMessage(rm))
      } else {
        socket.sendString(JsonSerializers.serialize(rm).spaces2)
      }
    } else {
      util.Logging.warn(s"Attempted send of message [${rm.getClass.getSimpleName}] when not connected.")
    }
  }
}
