package kaba4cow.marketscreener.utils;

public final class MathUtils {

	private MathUtils() {
	}

	public static double difference(double a, double b) {
		return Math.abs(a - b);
	}

	public static double clamp(double x, double min, double max) {
		if (x < min)
			return min;
		if (x > max)
			return max;
		return x;
	}

	public static double map(double x, double start1, double stop1, double start2, double stop2) {
		return start2 + (stop2 - start2) * (x - start1) / (stop1 - start1);
	}

	public static double interpolate(double a, double b, double position) {
		return position * b + (1d - position) * a;
	}

}
