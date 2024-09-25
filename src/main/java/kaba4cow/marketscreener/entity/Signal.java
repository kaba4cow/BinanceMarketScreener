package kaba4cow.marketscreener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "signal")
public class Signal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, updatable = false)
	private SignalType type;

	@Column(name = "timestamp", nullable = false, updatable = false, columnDefinition = "BIGINT")
	private Long timestamp;

	@Column(name = "base_asset", nullable = false, updatable = false, columnDefinition = "VARCHAR(16)")
	private String baseAsset;

	@Column(name = "quote_asset", nullable = false, updatable = false, columnDefinition = "VARCHAR(16)")
	private String quoteAsset;

	@Column(name = "value", nullable = false, updatable = false, columnDefinition = "REAL")
	private Float value;

	public Signal() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SignalType getType() {
		return type;
	}

	public void setType(SignalType type) {
		this.type = type;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getBaseAsset() {
		return baseAsset;
	}

	public void setBaseAsset(String baseAsset) {
		this.baseAsset = baseAsset;
	}

	public String getQuoteAsset() {
		return quoteAsset;
	}

	public void setQuoteAsset(String quoteAsset) {
		this.quoteAsset = quoteAsset;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Signal [id=" + id + ", type=" + type + ", timestamp=" + timestamp + ", baseAsset=" + baseAsset
				+ ", quoteAsset=" + quoteAsset + ", value=" + value + "]";
	}

}
