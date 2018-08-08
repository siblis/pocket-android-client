package pocket_android_client.geekbrains.www.pocketmessenger

import android.app.Activity
import com.neovisionaries.ws.client.*


class PocketMessengerWebSocket(private val activity: Activity) {
    private val TIMEOUT: Int = 5000
    private val SERVER: String = "ws://echo.websocket.org"
    fun getWebSOcket(): WebSocket {
        var customWebSocketListener: ICustomWebSocketListener
        return WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(object : WebSocketAdapter() {
                    override fun onTextMessage(websocket: WebSocket?, message: String?) {
                        try {
                            customWebSocketListener = activity as ICustomWebSocketListener
                            customWebSocketListener.onTextMessageFromEcho(message)
                        } catch (e: ClassCastException) {
                            throw ClassCastException(activity.toString())
                        }
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect()
    }
}