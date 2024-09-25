package kaba4cow.marketscreener.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kaba4cow.marketscreener.entity.Signal;
import kaba4cow.marketscreener.entity.SignalType;
import kaba4cow.marketscreener.repository.SignalRepository;
import kaba4cow.marketscreener.utils.TimeUtils;

@Service
public class SignalService {

	@Autowired
	private SignalRepository signalRepository;

	public SignalService() {
	}

	public Signal saveSignal(SignalType type, Long timestamp, String baseAsset, String quoteAsset, Float value) {
		Signal signal = new Signal();
		signal.setType(type);
		signal.setTimestamp(timestamp);
		signal.setBaseAsset(baseAsset);
		signal.setQuoteAsset(quoteAsset);
		signal.setValue(value);
		return signalRepository.save(signal);
	}

	public String getSignalMessage(Signal signal, Float valueThreshold) {
		int signalCount = getLastSignals(signal.getType(), signal.getBaseAsset(), signal.getQuoteAsset(),
				valueThreshold).size();
		return signal.getType().getSignalMessage(signal, signalCount);
	}

	public List<Signal> getAllSignals() {
		return signalRepository.findAll();
	}

	public List<Signal> getLastSignals(SignalType type, String baseAsset, String quoteAsset, Float valueThreshold) {
		LocalDateTime timestamp = LocalDateTime.now().minus(Duration.ofHours(24));
		return signalRepository.findLastSignals(type, TimeUtils.getTimestamp(timestamp), baseAsset, quoteAsset,
				valueThreshold);
	}

}
