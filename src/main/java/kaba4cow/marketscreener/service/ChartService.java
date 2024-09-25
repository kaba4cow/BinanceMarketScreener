package kaba4cow.marketscreener.service;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kaba4cow.marketscreener.utils.BinanceClient;
import kaba4cow.marketscreener.utils.FormatUtils;
import kaba4cow.marketscreener.utils.MathUtils;
import kaba4cow.marketscreener.utils.bars.Bar;
import kaba4cow.marketscreener.utils.bars.BarInterval;
import kaba4cow.marketscreener.utils.bars.BarSeries;

@Service
public class ChartService {

	@Value("${market_screener.chart.bar_count}")
	private Integer barCountProperty;
	@Value("${market_screener.chart.bar_width}")
	private Integer barWidthProperty;
	@Value("${market_screener.chart.offset}")
	private Integer offsetProperty;
	@Value("${market_screener.chart.interval}")
	private String intervalProperty;
	@Value("${market_screener.chart.line_stroke}")
	private float[] lineStrokeProperty;

	@Value("${market_screener.chart.color.background}")
	private Integer[] colorBackgroundProperty;
	@Value("${market_screener.chart.color.text}")
	private Integer[] colorTextProperty;
	@Value("${market_screener.chart.color.line}")
	private Integer[] colorLineProperty;
	@Value("${market_screener.chart.color.bull}")
	private Integer[] colorBullProperty;
	@Value("${market_screener.chart.color.bear}")
	private Integer[] colorBearProperty;

	@Value("${market_screener.chart.font.name}")
	private String fontNameProperty;
	@Value("${market_screener.chart.font.size}")
	private Integer fontSizeProperty;

	public ChartService() {
	}

	public RenderedImage createChart(String baseAsset, String quoteAsset) {
		BarInterval interval = BarInterval.get(intervalProperty);
		BarSeries bars = BinanceClient.getBarSeries(baseAsset + quoteAsset, interval, barCountProperty);

		double currentClosePrice = bars.getLast().getClosePrice();
		double minLowPrice = Double.POSITIVE_INFINITY;
		double maxHighPrice = Double.NEGATIVE_INFINITY;
		int minLowIndex = 0;
		int maxHighIndex = 0;
		for (int i = 0; i <= bars.getLastIndex(); i++) {
			Bar bar = bars.getBar(i);
			double low = bar.getLowPrice();
			double high = bar.getHighPrice();
			if (low < minLowPrice) {
				minLowPrice = low;
				minLowIndex = i;
			}
			if (high > maxHighPrice) {
				maxHighPrice = high;
				maxHighIndex = i;
			}
		}

		int width = bars.getBarCount() * barWidthProperty + offsetProperty;
		int height = 2 * width / 3;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setFont(new Font(fontNameProperty, Font.PLAIN, fontSizeProperty));
		FontMetrics metrics = graphics.getFontMetrics();

		int minY = 5 * metrics.getHeight();
		int maxY = height - 2 * metrics.getHeight();

		Color backgroundColor = createColor(colorBackgroundProperty);
		Color textColor = createColor(colorTextProperty);
		Color lineColor = createColor(colorLineProperty);
		Color bullColor = createColor(colorBullProperty);
		Color bearColor = createColor(colorBearProperty);

		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, width, height);

		int barX = 0;
		for (int i = 0; i <= bars.getLastIndex(); i++) {
			Bar bar = bars.getBar(i);
			double open = bar.getOpenPrice();
			double close = bar.getClosePrice();
			double low = bar.getLowPrice();
			double high = bar.getHighPrice();

			double openY = MathUtils.map(open, minLowPrice, maxHighPrice, maxY, minY);
			double closeY = MathUtils.map(close, minLowPrice, maxHighPrice, maxY, minY);
			double bodyHeight = Math.abs(closeY - openY) + 1;

			double lowY = MathUtils.map(low, minLowPrice, maxHighPrice, maxY, minY);
			double highY = MathUtils.map(high, minLowPrice, maxHighPrice, maxY, minY);
			double tailHeight = Math.abs(highY - lowY) + 1;

			graphics.setColor(bar.isBullish() ? bullColor : bearColor);
			graphics.fillRect(barX + barWidthProperty / 2, (int) highY, 1, (int) tailHeight);
			graphics.fillRect(barX + 1, (int) Math.min(openY, closeY), barWidthProperty - 2, (int) bodyHeight);
			barX += barWidthProperty;
		}

		graphics.setStroke(new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1f, lineStrokeProperty,
				0.5f * barWidthProperty));
		{
			int lowX = minLowIndex * barWidthProperty;
			graphics.setColor(lineColor);
			graphics.drawLine(lowX, maxY, width, maxY);
			String lowText = FormatUtils.number(minLowPrice, 8);
			graphics.setColor(textColor);
			graphics.drawString(lowText, width - metrics.stringWidth(lowText), maxY - 1);
		}
		{
			int highX = maxHighIndex * barWidthProperty;
			graphics.setColor(lineColor);
			graphics.drawLine(highX, minY, width, minY);
			String highText = FormatUtils.number(maxHighPrice, 8);
			graphics.setColor(textColor);
			graphics.drawString(highText, width - metrics.stringWidth(highText), minY - 1);
		}
		{
			int closeX = bars.getLastIndex() * barWidthProperty;
			double closeY = MathUtils.map(currentClosePrice, minLowPrice, maxHighPrice, maxY, minY);
			graphics.setColor(lineColor);
			graphics.drawLine(closeX, (int) closeY, width, (int) closeY);
			String closeText = FormatUtils.number(currentClosePrice, 8);
			graphics.setColor(textColor);
			graphics.drawString(closeText, width - metrics.stringWidth(closeText), (int) closeY - 1);
		}

		graphics.setColor(textColor);
		graphics.translate(2, metrics.getAscent());
		graphics.drawString(String.format("Pair: %s / %s", baseAsset, quoteAsset), 0, 0);
		graphics.translate(0, metrics.getHeight());
		graphics.drawString(String.format("Interval: %s", interval.toString()), 0, 0);
		graphics.translate(0, metrics.getHeight());
		graphics.drawString(String.format("From: %s", FormatUtils.dateTime(bars.getFirst().getOpenTime())), 0, 0);
		graphics.translate(0, metrics.getHeight());
		graphics.drawString(String.format("To: %s", FormatUtils.dateTime(bars.getLast().getOpenTime())), 0, 0);

		return image;
	}

	private Color createColor(Integer[] color) {
		return new Color(color[0], color[1], color[2]);
	}

}
