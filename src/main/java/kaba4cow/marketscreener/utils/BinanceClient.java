package kaba4cow.marketscreener.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.binance.connector.futures.client.FuturesClient;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.binance.connector.futures.client.impl.futures.Market;

import kaba4cow.marketscreener.utils.bars.Bar;
import kaba4cow.marketscreener.utils.bars.BarInterval;
import kaba4cow.marketscreener.utils.bars.BarSeries;

public class BinanceClient {

	private static final BinanceClient instance = new BinanceClient();

	private final FuturesClient client;
	private final Market market;

	private final Map<String, List<String>> assets;

	private BinanceClient() {
		client = new UMFuturesClientImpl();
		market = client.market();

		assets = new LinkedHashMap<>();

		JSONArray jsonSymbols = new JSONObject(market.exchangeInfo()).getJSONArray("symbols");
		for (int i = 0; i < jsonSymbols.length(); i++) {
			JSONObject jsonSymbol = jsonSymbols.getJSONObject(i);
			String baseAsset = jsonSymbol.getString("baseAsset");
			String quoteAsset = jsonSymbol.getString("quoteAsset");
			String status = jsonSymbol.getString("status");
			String contractType = jsonSymbol.getString("contractType");
			JSONArray jsonOrderTypes = jsonSymbol.getJSONArray("orderTypes");
			if (status.equals("TRADING") && //
					contractType.equals("PERPETUAL") && //
					JSONUtils.contains(jsonOrderTypes, "LIMIT") && //
					JSONUtils.contains(jsonOrderTypes, "MARKET")) {
				if (!assets.containsKey(baseAsset))
					assets.put(baseAsset, new ArrayList<>());
				assets.get(baseAsset).add(quoteAsset);
			}
		}
	}

	public static BarSeries getBarSeries(String symbol, BarInterval interval, int limit) {
		JSONArray json = new JSONArray(//
				instance.market.klines(new Parameters()//
						.put("symbol", symbol)//
						.put("interval", interval.toString())//
						.put("limit", limit)//
						.get()));
		BarSeries series = new BarSeries();
		for (int i = 0; i < json.length(); i++)
			series.addBar(new Bar(json.getJSONArray(i)));
		return series;
	}

	public static Set<String> getBaseAssets() {
		return instance.assets.keySet();
	}

	public static List<String> getQuoteAssets(String baseAsset) {
		if (baseAsset == null)
			return List.of();
		return instance.assets.get(baseAsset);
	}

}
