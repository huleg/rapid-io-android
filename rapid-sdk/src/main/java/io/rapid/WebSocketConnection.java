package io.rapid;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;

import java.net.URI;
import java.util.Map;

import io.rapid.utility.Logcat;


/**
 * Created by Leos on 15.03.2017.
 */

public class WebSocketConnection extends WebSocketClient
{
	private WebSocketConnectionListener mListener;
	private boolean mConnected = false;


	enum CloseReasonEnum
	{
		UNKNOWN;
	}


	interface WebSocketConnectionListener
	{
		void onOpen();
		void onMessage(MessageBase message);
		void onClose(CloseReasonEnum reason);
		void onError(Exception ex);
	}


	public WebSocketConnection(URI serverURI, WebSocketConnectionListener listener)
	{
		super(serverURI);
		mListener = listener;
	}


	public WebSocketConnection(URI serverUri, Draft draft, WebSocketConnectionListener listener)
	{
		super(serverUri, draft);
		mListener = listener;
	}


	public WebSocketConnection(URI serverUri, Draft draft, Map<String, String> headers, int connectTimeout, WebSocketConnectionListener listener)
	{
		super(serverUri, draft, headers, connectTimeout);
		mListener = listener;
	}


	public boolean isConnected()
	{
		return mConnected;
	}


	public void sendMessage(MessageBase message)
	{
		try
		{
			send(message.toJson().toString());
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public void onOpen(ServerHandshake handshakeData)
	{
		Logcat.d("Status message: " + handshakeData.getHttpStatusMessage() + "; HTTP status: " + handshakeData.getHttpStatus());

		mConnected = true;
		if(mListener != null) mListener.onOpen();
	}


	@Override
	public void onMessage(String message)
	{
		Logcat.d(message);

		try
		{
			if(mListener != null) mListener.onMessage(MessageParser.parse(message));
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public void onClose(int code, String reason, boolean remote)
	{
		Logcat.d("Code: " + code + "; reason: " + reason + "; remote:" + Boolean.toString(remote));

		mConnected = false;

		//TODO translate String reason to enum
		CloseReasonEnum reasonEnum = CloseReasonEnum.UNKNOWN;
		if(mListener != null) mListener.onClose(reasonEnum);
	}


	@Override
	public void onError(Exception ex)
	{
		Logcat.d(ex.getMessage());

		mConnected = false;
		if(mListener != null) mListener.onError(ex);
	}
}
