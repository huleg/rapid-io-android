package io.rapid;


import com.google.gson.Gson;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.rapid.converter.RapidGsonConverter;
import io.rapid.converter.RapidJsonConverter;
import io.rapid.utility.Logcat;


public class Rapid implements WebSocketConnection.WebSocketConnectionListener{
	private static Map<String, Rapid> sInstances = new HashMap<>();
	private final String mApiKey;
	private RapidJsonConverter mJsonConverter;
	private WebSocketConnection mWebSocketConnection;

	private Map<String, RapidCollection> mCollections = new HashMap<>();
	private RapidCollectionProvider mCollectionProvider;


	private Rapid(String apiKey) {
		mApiKey = apiKey;
		mJsonConverter = new RapidGsonConverter(new Gson());
		mWebSocketConnection = new WebSocketConnection(URI.create(RapidConfig.URI), this);
		mWebSocketConnection.connect();

		mCollectionProvider = new MockRapidCollectionProvider();
	}


	public static Rapid getInstance(String apiKey) {
		if(!sInstances.containsKey(apiKey))
			throw new IllegalStateException("Rapid SDK not initialized. Please call Rapid.initialize(apiKey) first.");
		return sInstances.get(apiKey);
	}


	public static Rapid getInstance() {
		if(sInstances.isEmpty())
			throw new IllegalStateException("Rapid SDK not initialized. Please call Rapid.initialize(apiKey) first.");
		else if(sInstances.size() > 1) {
			throw new IllegalStateException("Multiple Rapid instances initialized. Please use Rapid.getInstance(apiKey) to select the one you need.");
		} else {
			return getInstance(sInstances.keySet().iterator().next());
		}
	}


	public static void initialize(String apiKey) {
		sInstances.put(apiKey, new Rapid(apiKey));
	}


	public <T> RapidCollection<T> collection(String collectionName, Class<T> itemClass) {
		return mCollectionProvider.provideCollection(this, collectionName, itemClass);
	}


	public RapidJsonConverter getJsonConverter() {
		return mJsonConverter;
	}


	public void setJsonConverter(RapidJsonConverter jsonConverter) {
		mJsonConverter = jsonConverter;
	}


	public String getApiKey() {
		return mApiKey;
	}


	@Override
	public void onOpen()
	{

	}


	@Override
	public void onMessage(MessageBase message)
	{
		Logcat.d(message.toString());
	}


	@Override
	public void onClose(WebSocketConnection.CloseReasonEnum reason)
	{

	}


	@Override
	public void onError(Exception ex)
	{

	}
}
