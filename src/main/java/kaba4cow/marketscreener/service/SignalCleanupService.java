package kaba4cow.marketscreener.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import kaba4cow.marketscreener.repository.SignalRepository;
import kaba4cow.marketscreener.utils.TimeUtils;

@Service
public class SignalCleanupService {

	@Autowired
	private SignalRepository signalRepository;

	public SignalCleanupService() {
	}

	@Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
	public void deleteExpiredSignals() {
		LocalDateTime timestamp = LocalDateTime.now().minus(Duration.ofHours(24));
		signalRepository.deleteOldSignals(TimeUtils.getTimestamp(timestamp));
	}

}
