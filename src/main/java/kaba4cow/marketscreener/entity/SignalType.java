package kaba4cow.marketscreener.entity;

import kaba4cow.marketscreener.utils.FormatUtils;

public enum SignalType {

	PUMP(true) {
		@Override
		public String getSignalMessage(Signal signal, int signalCount) {
			return title(FormatUtils.EMOJI_GREEN_CIRCLE, signal, signalCount) + //
					String.format("Pump: +%s", FormatUtils.percent(signal.getValue() / 100d));
		}
	}, //
	DUMP(true) {
		@Override
		public String getSignalMessage(Signal signal, int signalCount) {
			return title(FormatUtils.EMOJI_RED_CIRCLE, signal, signalCount) + //
					String.format("Dump: -%s", FormatUtils.percent(signal.getValue() / 100d));
		}
	}, //
	LONG_LIQUIDATION(false) {
		@Override
		public String getSignalMessage(Signal signal, int signalCount) {
			return title(FormatUtils.EMOJI_RED_SQUARE, signal, signalCount) + //
					String.format("Long Liquidation: *$%s", signal.getValue().intValue());
		}
	}, //
	SHORT_LIQUIDATION(false) {
		@Override
		public String getSignalMessage(Signal signal, int signalCount) {
			return title(FormatUtils.EMOJI_GREEN_SQUARE, signal, signalCount) + //
					String.format("Short Liquidation: $%s", signal.getValue().intValue());
		}
	};

	private final boolean drawChart;

	private SignalType(boolean drawChart) {
		this.drawChart = drawChart;
	}

	public abstract String getSignalMessage(Signal signal, int signalCount);

	private static String title(String emoji, Signal signal, int signalCount) {
		return String.format("%s [%s](https://www.coinglass.com/tv/Binance_%s%s) %s %d\n\n", emoji,
				signal.getBaseAsset(), signal.getBaseAsset(), signal.getQuoteAsset(), FormatUtils.EMOJI_BELL,
				signalCount);
	}

	public boolean isDrawChart() {
		return drawChart;
	}

}
