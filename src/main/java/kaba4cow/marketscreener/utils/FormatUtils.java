package kaba4cow.marketscreener.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FormatUtils {

	public static final String EMOJI_RED_CIRCLE = createEmoji(0x1F534);
	public static final String EMOJI_GREEN_CIRCLE = createEmoji(0x1F7E2);
	public static final String EMOJI_RED_SQUARE = createEmoji(0x1F7E5);
	public static final String EMOJI_GREEN_SQUARE = createEmoji(0x1F7E9);
	public static final String EMOJI_BELL = createEmoji(0x1F514);
	public static final String EMOJI_BELL_SLASH = createEmoji(0x1F515);
	public static final String EMOJI_WRENCH = createEmoji(0x1F527);
	public static final String EMOJI_CROSS = createEmoji(0x274C);

	private static final DateTimeFormatter dateTimeFormatter;
	private static final DateTimeFormatter dateFormatter;
	private static final DateTimeFormatter timeFormatter;

	private static final DecimalFormat decimalFormat;

	private FormatUtils() {
	}

	static {
		dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH:mm");
		dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormat = new DecimalFormat();
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		decimalFormat.setGroupingUsed(false);
	}

	private static String createEmoji(int code) {
		return new String(Character.toChars(code));
	}

	public static String dateTime(LocalDateTime dateTime) {
		return dateTime.format(dateTimeFormatter);
	}

	public static String dateTime(Long timestamp) {
		return dateTime(TimeUtils.getDateTime(timestamp));
	}

	public static String date(LocalDateTime date) {
		return date.format(dateFormatter);
	}

	public static String time(LocalDateTime time) {
		return time.format(timeFormatter);
	}

	public static String number(double number, int digits) {
		String suffix = "";
		if (number > 1e+12d) {
			number /= 1e+12d;
			suffix = "T";
		} else if (number > 1e+9d) {
			number /= 1e+9d;
			suffix = "B";
		} else if (number > 1e+6d) {
			number /= 1e+6d;
			suffix = "M";
		}
		decimalFormat.setMinimumFractionDigits(0);
		decimalFormat.setMaximumFractionDigits(digits);
		return String.format("%s%s", decimalFormat.format(number), suffix);
	}

	public static String number(double number) {
		return number(number, 3);
	}

	public static String percent(double percent) {
		decimalFormat.setMinimumFractionDigits(0);
		decimalFormat.setMaximumFractionDigits(2);
		return String.format("%s%%", decimalFormat.format(100d * percent));
	}

	public static String percentSigned(double percent) {
		if (percent == 0d)
			return percent(0d);
		String sign = percent >= 0d ? "+" : "-";
		return sign + percent(Math.abs(percent));
	}

	public static DecimalFormat getDecimalformat() {
		return decimalFormat;
	}

}
