package kaba4cow.marketscreener.telegrambot;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramBot extends TelegramLongPollingBot {

	private final String username;
	private final TelegramBotListener listener;

	public TelegramBot(String token, String username, TelegramBotListener listener) throws TelegramApiException {
		super(token);
		this.username = username;
		this.listener = listener;
		new TelegramBotsApi(DefaultBotSession.class).registerBot(this);
		System.out.println("Telegram Bot \"" + username + "\" started");
	}

	@Override
	public void onUpdateReceived(Update update) {
		listener.onUpdate(update);
	}

	public boolean sendReplyKeyboard(Long id, String text, ReplyKeyboard keyboard) {
		SendMessage message = new SendMessage();
		message.setChatId(id);
		message.setText(text);
		message.setParseMode("Markdown");
		ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
		markup.setResizeKeyboard(true);
		markup.setKeyboard(keyboard.get());
		message.setReplyMarkup(markup);
		try {
			execute(message);
			return true;
		} catch (TelegramApiException e) {
			return false;
		}
	}

	public boolean sendMessage(Long id, String text) {
		SendMessage message = new SendMessage();
		message.setChatId(id);
		message.setText(text);
		message.setParseMode("Markdown");
		try {
			execute(message);
			return true;
		} catch (TelegramApiException e) {
			return false;
		}
	}

	public boolean sendMessageFormat(Long id, String format, Object... args) {
		return sendMessage(id, String.format(format, args));
	}

	public boolean sendPhoto(Long id, RenderedImage image, String caption) {
		SendPhoto message = new SendPhoto();
		message.setChatId(id);
		message.setCaption(caption);
		message.setParseMode("Markdown");
		try {
			message.setPhoto(createInputFile(id, image));
		} catch (IOException e) {
			return sendMessage(id, caption);
		}
		try {
			execute(message);
			return true;
		} catch (TelegramApiException e) {
			return false;
		}
	}

	public boolean sendPhotoFormat(Long id, RenderedImage image, String format, Object... args) {
		return sendPhoto(id, image, String.format(format, args));
	}

	private InputFile createInputFile(Long id, RenderedImage image) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "png", output);
		InputStream input = new ByteArrayInputStream(output.toByteArray());
		String filename = "image_" + UUID.randomUUID().toString();
		return new InputFile(input, filename);
	}

	@Override
	public final String getBotUsername() {
		return username;
	}

}
