package io.rapid;

import android.content.Context;

import org.json.JSONException;


abstract class WebSocketConnection
{
	String mServerURI;
	WebSocketConnectionListener mListener;


	interface WebSocketConnectionListener
	{
		void onOpen();
		void onMessage(Message message);
		void onClose(CloseReasonEnum reason);
//		void onError(Exception ex);
	}


	public WebSocketConnection(String serverURI, WebSocketConnectionListener listener)
	{
		mServerURI = serverURI;
		mListener = listener;
	}


	abstract void connectToServer(Context context);
	abstract void sendMessage(String message);


	void disconnectFromServer(boolean sendDisconnectMessage)
	{
		if(sendDisconnectMessage)
		{
			try
			{
				sendMessage(new Message.Dis().toJson().toString());
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
	}


	void handleNewMessage(Message parsedMessage) {
		if(mListener != null) mListener.onMessage(parsedMessage);
	}
}
