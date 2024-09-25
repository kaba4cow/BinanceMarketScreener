package kaba4cow.marketscreener.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kaba4cow.marketscreener.entity.SignalType;
import kaba4cow.marketscreener.entity.Subscriber;
import kaba4cow.marketscreener.repository.SubscriberRepository;

@Service
public class SubscriberService {

	@Autowired
	private SubscriberRepository subscriberRepository;

	@Value("${market_screener.subscriber.default.pump_threshold}")
	private Float defaultPumpThreshold;
	@Value("${market_screener.subscriber.default.dump_threshold}")
	private Float defaultDumpThreshold;
	@Value("${market_screener.subscriber.default.long_liquidation_threshold}")
	private Float defaultLongLiquidationThreshold;
	@Value("${market_screener.subscriber.default.short_liquidation_threshold}")
	private Float defaultShortLiquidationThreshold;

	public SubscriberService() {
	}

	public Subscriber createSubscriber(Long id) {
		Subscriber subscriber = new Subscriber();
		subscriber.setId(id);
		subscriber.setPumpThreshold(defaultPumpThreshold);
		subscriber.setDumpThreshold(defaultDumpThreshold);
		subscriber.setLongLiquidationThreshold(defaultLongLiquidationThreshold);
		subscriber.setShortLiquidationThreshold(defaultShortLiquidationThreshold);
		subscriber.setSubscribed(true);
		return subscriberRepository.save(subscriber);
	}

	public void saveSubscriber(Subscriber subscriber) {
		subscriberRepository.save(subscriber);
	}

	public void deleteSubscriber(Subscriber subscriber) {
		subscriberRepository.delete(subscriber);
	}

	public Subscriber getSubscriber(Long id) {
		return subscriberRepository.findById(id).get();
	}

	public boolean exists(Long id) {
		return subscriberRepository.findById(id).isPresent();
	}

	public boolean isSubscribed(Long id) {
		return subscriberRepository.findById(id).get().getSubscribed();
	}

	public List<Subscriber> getAllSubscribers() {
		return subscriberRepository.findAll();
	}

	public List<Subscriber> getSignalSubscribers(SignalType type, Float value) {
		switch (type) {
		case PUMP:
			return subscriberRepository.findSubscribersByPump(value);
		case DUMP:
			return subscriberRepository.findSubscribersByDump(value);
		case LONG_LIQUIDATION:
			return subscriberRepository.findSubscribersByLongLiquidation(value);
		case SHORT_LIQUIDATION:
			return subscriberRepository.findSubscribersByShortLiquidation(value);
		}
		return List.of();
	}

	public Float getValueThresholdByType(SignalType type, Subscriber subscriber) {
		switch (type) {
		case PUMP:
			return subscriber.getPumpThreshold();
		case DUMP:
			return subscriber.getDumpThreshold();
		case LONG_LIQUIDATION:
			return subscriber.getLongLiquidationThreshold();
		case SHORT_LIQUIDATION:
			return subscriber.getShortLiquidationThreshold();
		}
		return Float.NaN;
	}

}
