package kaba4cow.marketscreener.service;

import java.awt.image.RenderedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import jakarta.annotation.PostConstruct;
import kaba4cow.marketscreener.entity.Signal;
import kaba4cow.marketscreener.entity.SignalType;
import kaba4cow.marketscreener.entity.Subscriber;
import kaba4cow.marketscreener.telegrambot.ReplyKeyboard;
import kaba4cow.marketscreener.telegrambot.TelegramBot;
import kaba4cow.marketscreener.telegrambot.TelegramBotListener;
import kaba4cow.marketscreener.utils.FormatUtils;

@Service
public class TelegramBotService implements TelegramBotListener {

	private static final String SUBSCRIBE = FormatUtils.EMOJI_BELL + " Subscribe";
	private static final String UNSUBSCRIBE = FormatUtils.EMOJI_BELL_SLASH + " Unsubscribe";
	private static final String SETTINGS = FormatUtils.EMOJI_WRENCH + " Settings";
	private static final String CANCEL = FormatUtils.EMOJI_CROSS + " Cancel";
	private static final String PUMP_THRESHOLD = FormatUtils.EMOJI_GREEN_CIRCLE + " Pump";
	private static final String DUMP_THRESHOLD = FormatUtils.EMOJI_RED_CIRCLE + " Dump";
	private static final String LONG_LIQUIDATION_THRESHOLD = FormatUtils.EMOJI_RED_SQUARE + " Long Liquidation";
	private static final String SHORT_LIQUIDATION_THRESHOLD = FormatUtils.EMOJI_GREEN_SQUARE + " Short Liquidation";

	@Autowired
	private SubscriberService subscriberService;
	@Autowired
	private SignalService signalService;
	@Autowired
	private ChartService chartService;

	@Value("${market_screener.bot.username}")
	private String usernameProperty;
	@Value("${market_screener.bot.token}")
	private String tokenProperty;

	@Value("${market_screener.bot.settings.pump_threshold.min}")
	private Float minPumpThreshold;
	@Value("${market_screener.bot.settings.pump_threshold.max}")
	private Float maxPumpThreshold;
	@Value("${market_screener.bot.settings.dump_threshold.min}")
	private Float minDumpThreshold;
	@Value("${market_screener.bot.settings.dump_threshold.max}")
	private Float maxDumpThreshold;
	@Value("${market_screener.bot.settings.long_liquidation_threshold.min}")
	private Float minLongLiquidationThreshold;
	@Value("${market_screener.bot.settings.long_liquidation_threshold.max}")
	private Float maxLongLiquidationThreshold;
	@Value("${market_screener.bot.settings.short_liquidation_threshold.min}")
	private Float minShortLiquidationThreshold;
	@Value("${market_screener.bot.settings.short_liquidation_threshold.max}")
	private Float maxShortLiquidationThreshold;

	private final Map<Long, Command> commands;

	private TelegramBot bot;

	public TelegramBotService() throws TelegramApiException {
		commands = new HashMap<>();
	}

	@PostConstruct
	public void initialize() throws TelegramApiException {
		bot = new TelegramBot(tokenProperty, usernameProperty, this);
	}

	@Override
	public void onUpdate(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String text = update.getMessage().getText();
			Long id = update.getMessage().getChatId();
			if (!subscriberService.exists(id))
				subscriberService.createSubscriber(id);
			if (text.equals("/start") || text.equals(SUBSCRIBE))
				subscribe(id);
			else if (text.equals(UNSUBSCRIBE))
				unsubscribe(id);
			else if (text.equals(SETTINGS))
				sendCurrentSettings(id);
			else if (text.equals(PUMP_THRESHOLD))
				setPumpThreshold(id);
			else if (text.equals(DUMP_THRESHOLD))
				setDumpThreshold(id);
			else if (text.equals(LONG_LIQUIDATION_THRESHOLD))
				setLongLiquidationThreshold(id);
			else if (text.equals(SHORT_LIQUIDATION_THRESHOLD))
				setShortLiquidationThreshold(id);
			else if (text.equals(CANCEL))
				cancelCommand(id);
			else {
				Command command = commands.getOrDefault(id, Command.NONE);
				switch (command) {
				case PUMP_THRESHOLD:
					handlePumpThreshold(id, text);
					break;
				case DUMP_THRESHOLD:
					handleDumpThreshold(id, text);
					break;
				case LONG_LIQUIDATION_THRESHOLD:
					handleLongLiquidationThreshold(id, text);
					break;
				case SHORT_LIQUIDATION_THRESHOLD:
					handleShortLiquidationThreshold(id, text);
					break;
				case NONE:
					break;
				}
			}
		}
	}

	private void setPumpThreshold(Long id) {
		Subscriber subscriber = subscriberService.getSubscriber(id);
		sendCommand(id, Command.PUMP_THRESHOLD, "Choose new pump limit between %s%% and %s%%\n\nCurrent value is %s%%",
				minPumpThreshold, maxPumpThreshold, subscriber.getPumpThreshold());
	}

	private void handlePumpThreshold(Long id, String text) {
		try {
			Float value = Float.parseFloat(text);
			if (value >= minPumpThreshold && value <= maxPumpThreshold) {
				Subscriber subscriber = subscriberService.getSubscriber(id);
				subscriber.setPumpThreshold(value);
				subscriberService.saveSubscriber(subscriber);
				sendMessage(id, "Pump limit set to %s", FormatUtils.percent(value / 100d));
				return;
			}
		} catch (NumberFormatException e) {
		}
		sendInvalidValue(id);
	}

	private void setDumpThreshold(Long id) {
		Subscriber subscriber = subscriberService.getSubscriber(id);
		sendCommand(id, Command.DUMP_THRESHOLD, "Choose new dump limit between %s%% and %s%%\n\nCurrent value is %s%%",
				minDumpThreshold, maxDumpThreshold, subscriber.getDumpThreshold());
	}

	private void handleDumpThreshold(Long id, String text) {
		try {
			Float value = Float.parseFloat(text);
			if (value >= minDumpThreshold && value <= maxDumpThreshold) {
				Subscriber subscriber = subscriberService.getSubscriber(id);
				subscriber.setDumpThreshold(value);
				subscriberService.saveSubscriber(subscriber);
				sendMessage(id, "Dump limit set to %s", FormatUtils.percent(value / 100d));
				return;
			}
		} catch (NumberFormatException e) {
		}
		sendInvalidValue(id);
	}

	private void setLongLiquidationThreshold(Long id) {
		Subscriber subscriber = subscriberService.getSubscriber(id);
		sendCommand(id, Command.LONG_LIQUIDATION_THRESHOLD,
				"Choose new long liquidation limit between $%s and $%s\n\nCurrent value is $%s",
				minLongLiquidationThreshold, maxLongLiquidationThreshold, subscriber.getLongLiquidationThreshold());
	}

	private void handleLongLiquidationThreshold(Long id, String text) {
		try {
			Float value = Float.parseFloat(text);
			if (value >= minLongLiquidationThreshold && value <= maxLongLiquidationThreshold) {
				Subscriber subscriber = subscriberService.getSubscriber(id);
				subscriber.setLongLiquidationThreshold(value);
				subscriberService.saveSubscriber(subscriber);
				sendMessage(id, "Long liquidation limit set to $%s", value);
				return;
			}
		} catch (NumberFormatException e) {
		}
		sendInvalidValue(id);
	}

	private void setShortLiquidationThreshold(Long id) {
		Subscriber subscriber = subscriberService.getSubscriber(id);
		sendCommand(id, Command.SHORT_LIQUIDATION_THRESHOLD,
				"Choose new short liquidation limit between $%s and $%s\n\nCurrent value is $%s",
				minShortLiquidationThreshold, maxShortLiquidationThreshold, subscriber.getShortLiquidationThreshold());
	}

	private void handleShortLiquidationThreshold(Long id, String text) {
		try {
			Float value = Float.parseFloat(text);
			if (value >= minShortLiquidationThreshold && value <= maxShortLiquidationThreshold) {
				Subscriber subscriber = subscriberService.getSubscriber(id);
				subscriber.setShortLiquidationThreshold(value);
				subscriberService.saveSubscriber(subscriber);
				sendMessage(id, "Short liquidation limit set to $%s", value);
				return;
			}
		} catch (NumberFormatException e) {
		}
		sendInvalidValue(id);
	}

	private void subscribe(Long id) {
		Subscriber subscriber = subscriberService.getSubscriber(id);
		subscriber.setSubscribed(true);
		subscriberService.saveSubscriber(subscriber);
		sendMessage(id, "Subscription successful");
	}

	private void unsubscribe(Long id) {
		Subscriber subscriber = subscriberService.getSubscriber(id);
		subscriber.setSubscribed(false);
		subscriberService.saveSubscriber(subscriber);
		sendMessage(id, "Subscription canceled");
	}

	private void sendCurrentSettings(Long id) {
		Subscriber subscriber = subscriberService.getSubscriber(id);
		String message = "%s Current Settings:\n\n" + //
				" - Status: %s\n" + //
				" - Pump limit: %s%%\n" + //
				" - Dump limit: %s%%\n" + //
				" - Long Liquidation limit: $%s\n" + //
				" - Short Liquidation limit: $%s";
		sendMessage(id, message, FormatUtils.EMOJI_WRENCH, subscriber.getSubscribed() ? "Subscribed" : "Unsubscribed",
				subscriber.getPumpThreshold(), subscriber.getDumpThreshold(), subscriber.getLongLiquidationThreshold(),
				subscriber.getShortLiquidationThreshold());
	}

	private void sendCommand(Long id, Command command, String format, Object... args) {
		bot.sendReplyKeyboard(id, String.format(format, args), new ReplyKeyboard()//
				.row(CANCEL));
		commands.put(id, command);
	}

	private void sendMessage(Long id, String format, Object... args) {
		Subscriber subscriber = subscriberService.getSubscriber(id);
		bot.sendReplyKeyboard(id, String.format(format, args), new ReplyKeyboard()//
				.row(PUMP_THRESHOLD, LONG_LIQUIDATION_THRESHOLD)//
				.row(DUMP_THRESHOLD, SHORT_LIQUIDATION_THRESHOLD)//
				.row(subscriber.getSubscribed() ? UNSUBSCRIBE : SUBSCRIBE)//
				.row(SETTINGS));
		commands.remove(id);
	}

	private void cancelCommand(Long id) {
		sendMessage(id, "Canceled");
	}

	private void sendInvalidValue(Long id) {
		sendMessage(id, "Invalid value");
	}

	public void sendSignal(SignalType type, Long timestamp, String baseAsset, String quoteAsset, Float value) {
		Signal signal = signalService.saveSignal(type, timestamp, baseAsset, quoteAsset, value);
		List<Subscriber> subscribers = subscriberService.getSignalSubscribers(type, value);
		if (subscribers.isEmpty())
			return;
		RenderedImage chart = type.isDrawChart()
				? chartService.createChart(signal.getBaseAsset(), signal.getQuoteAsset())
				: null;
		for (Subscriber subscriber : subscribers) {
			Float valueThreshold = subscriberService.getValueThresholdByType(type, subscriber);
			String message = signalService.getSignalMessage(signal, valueThreshold);
			if (type.isDrawChart())
				bot.sendPhoto(subscriber.getId(), chart, message);
			else
				bot.sendMessage(subscriber.getId(), message);
		}
	}

	private enum Command {

		NONE, PUMP_THRESHOLD, DUMP_THRESHOLD, LONG_LIQUIDATION_THRESHOLD, SHORT_LIQUIDATION_THRESHOLD;

	}

}
