package kaba4cow.marketscreener.screener;

import java.time.LocalDateTime;

import org.json.JSONObject;

import kaba4cow.marketscreener.entity.SignalType;
import kaba4cow.marketscreener.service.TelegramBotService;
import kaba4cow.marketscreener.utils.TimeUtils;

public class LiquidationScreener implements Screener {

	private final TelegramBotService telegramBotService;

	private final String baseAsset;
	private final String quoteAsset;
	private final Float longLiquidationThreshold;
	private final Float shortLiquidationThreshold;

	public LiquidationScreener(TelegramBotService telegramBotService, String baseAsset, String quoteAsset,
			Float longLiquidationThreshold, Float shortLiquidationThreshold) {
		this.telegramBotService = telegramBotService;
		this.baseAsset = baseAsset;
		this.quoteAsset = quoteAsset;
		this.longLiquidationThreshold = longLiquidationThreshold;
		this.shortLiquidationThreshold = shortLiquidationThreshold;
	}

	@Override
	public void update(JSONObject json) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		json = json.getJSONObject("o");
		String side = json.getString("S");
		float price = json.getFloat("p");
		float quantity = json.getFloat("q");
		float liquidation = price * quantity;
		if (side.equals("SELL") && liquidation >= longLiquidationThreshold)
			telegramBotService.sendSignal(SignalType.LONG_LIQUIDATION, TimeUtils.getTimestamp(LocalDateTime.now()),
					baseAsset, quoteAsset, liquidation);
		else if (side.equals("BUY") && liquidation >= shortLiquidationThreshold)
			telegramBotService.sendSignal(SignalType.SHORT_LIQUIDATION, TimeUtils.getTimestamp(LocalDateTime.now()),
					baseAsset, quoteAsset, liquidation);
	}

}
