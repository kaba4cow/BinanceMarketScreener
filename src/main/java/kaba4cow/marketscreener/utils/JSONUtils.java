package kaba4cow.marketscreener.utils;

import org.json.JSONArray;

public final class JSONUtils {

	private JSONUtils() {
	}

	public static boolean contains(JSONArray array, String string) {
		for (int index = 0; index < array.length(); index++)
			if (array.getString(index).equals(string))
				return true;
		return false;
	}

}
