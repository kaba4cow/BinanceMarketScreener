package kaba4cow.marketscreener.telegrambot;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramBotListener {

	public void onUpdate(Update update);

}
