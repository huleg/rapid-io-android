package io.rapid;


public class RapidError extends Error {

	private ErrorType mType;


	public enum ErrorType {
		TIMEOUT("timeout", "Connection timed out"),
		SUBSCRIPTION_CANCELED("subscription-cancelled", "Subscription cancelled"),
		INTERNAL_ERROR("internal-error", "Internal server error"),
		PERMISSION_DENIED("permission-denied", "Permission denied"),
		CONNECTION_TERMINATED("connection-terminated", "Connection terminated"),
		INVALID_AUTH_TOKEN("invalid-auth-token", "Invalid Auth Token"),
		UNKNOWN_ERROR("unknown", "Unknown Error"),
		ETAG_CONFLICT("etag-conflict", "ETAG Confilct");

		private String mName;
		private String mMessage;


		ErrorType(String name, String defaultMessage) {
			mName = name;
			mMessage = defaultMessage;
		}


		public static ErrorType fromServerError(Message.Err serverError) {
			ErrorType result = UNKNOWN_ERROR; // default error type
			Message.Err.Type errorType = serverError.getType();
			for(ErrorType type : values()) {
				if(type.getName().equals(errorType.getKey())) {
					result = type;
					break;
				}
			}

			// if there is a error message provided by server - use it
			if(serverError.getErrorMessage() != null)
				result.setMessage(serverError.getErrorMessage());

			return result;
		}


		public String getName() {
			return mName;
		}


		public String getMessage() {
			return mMessage;
		}


		public void setMessage(String message) {
			mMessage = message;
		}
	}


	public RapidError(ErrorType type) {
		super("Rapid Error: " + type.getMessage());
		mType = type;
	}


	public ErrorType getType() {
		return mType;
	}
}
