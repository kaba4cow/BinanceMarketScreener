package kaba4cow.marketscreener.screener;

import org.json.JSONObject;

import kaba4cow.marketscreener.entity.SignalType;
import kaba4cow.marketscreener.service.TelegramBotService;
import kaba4cow.marketscreener.utils.BinanceClient;
import kaba4cow.marketscreener.utils.bars.Bar;
import kaba4cow.marketscreener.utils.bars.BarInterval;
import kaba4cow.marketscreener.utils.bars.BarSeries;

public class PumpDumpScreener implements Screener {

	private final TelegramBotService telegramBotService;

	private final String baseAsset;
	private final String quoteAsset;
	private final Float pumpThreshold;
	private final Float dumpThreshold;

	private final BarSeries series;

	public PumpDumpScreener(TelegramBotService telegramBotService, String baseAsset, String quoteAsset,
			Float pumpThreshold, Float dumpThreshold) {
		this.telegramBotService = telegramBotService;
		this.baseAsset = baseAsset;
		this.quoteAsset = quoteAsset;
		this.pumpThreshold = pumpThreshold;
		this.dumpThreshold = dumpThreshold;
		this.series = BinanceClient.getBarSeries(baseAsset + quoteAsset, BarInterval.MINUTE1, 2);
		this.series.setMaxBarCount(2);
	}

	@Override
	public void update(JSONObject json) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Bar firstBar = series.getFirst();
		Bar lastBar = series.getLast();
		Bar newBar = new Bar(json);
		if (newBar.getOpenTime() > lastBar.getOpenTime()) {
			float firstPrice = firstBar.getClosePrice();
			float lastPrice = lastBar.getClosePrice();
			float deltaPrice = calculateDelta(firstPrice, lastPrice);
			if (deltaPrice > 0f && deltaPrice >= pumpThreshold)
				telegramBotService.sendSignal(SignalType.PUMP, lastBar.getOpenTime(), baseAsset, quoteAsset,
						deltaPrice);
			else if (deltaPrice < 0f && deltaPrice <= -dumpThreshold)
				telegramBotService.sendSignal(SignalType.DUMP, lastBar.getOpenTime(), baseAsset, quoteAsset,
						-deltaPrice);
		}
		series.addBar(newBar);
	}

	private float calculateDelta(float oldValue, float newValue) {
		return 100f * (newValue - oldValue) / oldValue;
	}

}
