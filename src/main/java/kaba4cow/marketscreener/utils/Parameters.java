package kaba4cow.marketscreener.utils;

import java.util.LinkedHashMap;

public class Parameters {

	private final LinkedHashMap<String, Object> parameters;

	public Parameters() {
		parameters = new LinkedHashMap<>();
	}

	public Parameters put(String parameter, Object value) {
		parameters.put(parameter, value.toString());
		return this;
	}

	public LinkedHashMap<String, Object> get() {
		return parameters;
	}

}
