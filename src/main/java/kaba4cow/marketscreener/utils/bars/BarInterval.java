package kaba4cow.marketscreener.utils.bars;

import java.time.Duration;

public enum BarInterval {

	MINUTE1("1m", Duration.ofMinutes(1)), //
	MINUTE3("3m", Duration.ofMinutes(3)), //
	MINUTE5("5m", Duration.ofMinutes(5)), //
	MINUTE15("15m", Duration.ofMinutes(15)), //
	MINUTE30("30m", Duration.ofMinutes(30)), //
	HOUR1("1h", Duration.ofHours(1)), //
	HOUR2("2h", Duration.ofHours(2)), //
	HOUR4("4h", Duration.ofHours(4)), //
	HOUR6("6h", Duration.ofHours(6)), //
	HOUR8("8h", Duration.ofHours(8)), //
	HOUR12("12h", Duration.ofHours(12)), //
	DAY1("1d", Duration.ofDays(1));

	private final String string;
	private final Duration duration;

	private BarInterval(String code, Duration duration) {
		this.string = code;
		this.duration = duration;
	}

	public static BarInterval get(String string) {
		for (BarInterval interval : values())
			if (interval.string.equals(string))
				return interval;
		return null;
	}

	@Override
	public String toString() {
		return string;
	}

	public Duration getDuration() {
		return duration;
	}

}
