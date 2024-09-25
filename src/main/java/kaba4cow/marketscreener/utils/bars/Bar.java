package kaba4cow.marketscreener.utils.bars;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bar {

	private final long openTime;
	private final float openPrice;
	private final float highPrice;
	private final float lowPrice;
	private final float closePrice;

	public Bar(JSONArray json) {
		this.openTime = json.getLong(0);
		this.openPrice = json.getFloat(1);
		this.highPrice = json.getFloat(2);
		this.lowPrice = json.getFloat(3);
		this.closePrice = json.getFloat(4);
	}

	public Bar(JSONObject json) {
		json = json.getJSONObject("k");
		this.openTime = json.getLong("t");
		this.openPrice = json.getFloat("o");
		this.highPrice = json.getFloat("h");
		this.lowPrice = json.getFloat("l");
		this.closePrice = json.getFloat("c");
	}

	public boolean isBullish() {
		return closePrice >= openPrice;
	}

	public boolean isBearish() {
		return !isBullish();
	}

	public long getOpenTime() {
		return openTime;
	}

	public float getOpenPrice() {
		return openPrice;
	}

	public float getHighPrice() {
		return highPrice;
	}

	public float getLowPrice() {
		return lowPrice;
	}

	public float getClosePrice() {
		return closePrice;
	}

	@Override
	public String toString() {
		return "Bar [openTime=" + openTime + ", openPrice=" + openPrice + ", highPrice=" + highPrice + ", lowPrice="
				+ lowPrice + ", closePrice=" + closePrice + "]";
	}

}