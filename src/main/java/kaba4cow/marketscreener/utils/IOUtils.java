package kaba4cow.marketscreener.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public final class IOUtils {

	private static final String DIRECTORY;

	private IOUtils() {
	}

	static {
		DIRECTORY = System.getProperty("user.home") + File.separator + ".traderclient" + File.separator;
		File directoryFile = new File(DIRECTORY);
		if (!directoryFile.exists())
			directoryFile.mkdir();
	}

	public static JSONObject readJSONObject(String filename) {
		try {
			return new JSONObject(new String(Files.readAllBytes(Paths.get(DIRECTORY + filename))));
		} catch (IOException e) {
			return new JSONObject();
		}
	}

	public static JSONArray readJSONArray(String filename) {
		try {
			return new JSONArray(new String(Files.readAllBytes(Paths.get(DIRECTORY + filename))));
		} catch (Exception e) {
			return new JSONArray();
		}
	}

	public static void writeJSONObject(JSONObject json, String filename) {
		try (FileWriter fileWriter = new FileWriter(DIRECTORY + filename)) {
			fileWriter.write(json.toString(1));
		} catch (Exception e) {
		}
	}

	public static void writeJSONArray(JSONArray json, String filename) {
		try (FileWriter fileWriter = new FileWriter(DIRECTORY + filename)) {
			fileWriter.write(json.toString(1));
		} catch (IOException e) {
		}
	}

}
