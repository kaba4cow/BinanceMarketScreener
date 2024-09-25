package kaba4cow.marketscreener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;

import jakarta.annotation.PostConstruct;
import kaba4cow.marketscreener.screener.LiquidationScreener;
import kaba4cow.marketscreener.screener.PumpDumpScreener;
import kaba4cow.marketscreener.screener.Screener;
import kaba4cow.marketscreener.service.TelegramBotService;
import kaba4cow.marketscreener.utils.BinanceClient;

@SpringBootApplication
@EnableScheduling
public class MarketScreener {

	@Autowired
	private TelegramBotService telegramBotService;

	@Value("${market_screener.screener.excluded}")
	private List<String> excludedProperty;
	@Value("${market_screener.screener.quote_asset}")
	private String quoteAssetProperty;
	@Value("${market_screener.screener.threshold.pump}")
	private Float thresholdPumpProperty;
	@Value("${market_screener.screener.threshold.dump}")
	private Float thresholdDumpProperty;
	@Value("${market_screener.screener.threshold.long_liquidation}")
	private Float thresholdLongLiquidationProperty;
	@Value("${market_screener.screener.threshold.short_liquidation}")
	private Float thresholdShortLiquidationProperty;

	public MarketScreener() {
	}

	@PostConstruct
	public void initialize() {
		UMWebsocketClientImpl client = new UMWebsocketClientImpl();

		ArrayList<String> streams = new ArrayList<>();
		Map<String, Screener> screeners = new LinkedHashMap<>();

		Set<String> baseAssets = BinanceClient.getBaseAssets();
		for (String baseAsset : baseAssets) {
			if (baseAsset.startsWith("1000") || excludedProperty.contains(baseAsset)
					|| !BinanceClient.getQuoteAssets(baseAsset).contains(quoteAssetProperty))
				continue;
			System.out.println("Initializing " + baseAsset + " Screeners");
			String symbol = (baseAsset + quoteAssetProperty).toLowerCase();

			String liquidationStream = symbol + "@forceOrder";
			Screener liquidationScreener = new LiquidationScreener(telegramBotService, baseAsset, quoteAssetProperty,
					thresholdLongLiquidationProperty, thresholdShortLiquidationProperty);
			screeners.put(liquidationStream, liquidationScreener);
			streams.add(liquidationStream);

			String pumpDumpStream = symbol + "@kline_1m";
			Screener pumpScreener = new PumpDumpScreener(telegramBotService, baseAsset, quoteAssetProperty,
					thresholdPumpProperty, thresholdDumpProperty);
			screeners.put(pumpDumpStream, pumpScreener);
			streams.add(pumpDumpStream);
		}
		System.out.println("Total " + screeners.size() / 2 + " Assets");

		client.combineStreams(streams, data -> {
			JSONObject json = new JSONObject(data);
			screeners.get(json.getString("stream")).update(json.getJSONObject("data"));
		});
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(MarketScreener.class).run(args);
	}

}
