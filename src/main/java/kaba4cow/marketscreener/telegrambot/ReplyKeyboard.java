package kaba4cow.marketscreener.telegrambot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class ReplyKeyboard {

	private final List<KeyboardRow> keyboard;

	public ReplyKeyboard() {
		this.keyboard = new ArrayList<>();
	}

	public ReplyKeyboard row(String... options) {
		KeyboardRow row = new KeyboardRow();
		for (String option : options)
			row.add(new KeyboardButton(option));
		keyboard.add(row);
		return this;
	}

	public List<KeyboardRow> get() {
		return keyboard;
	}

}