package pocket_android_client.geekbrains.www.pocketmessenger

interface ICustomWebSocketListener {
    fun onTextMessageFromEcho(echoMessage: String?)
}