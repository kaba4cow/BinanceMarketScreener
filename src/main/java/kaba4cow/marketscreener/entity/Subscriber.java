package kaba4cow.marketscreener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscriber")
public class Subscriber {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@Column(name = "threshold_pump", nullable = false, updatable = true, columnDefinition = "REAL")
	private Float pumpThreshold;

	@Column(name = "threshold_dump", nullable = false, updatable = true, columnDefinition = "REAL")
	private Float dumpThreshold;

	@Column(name = "threshold_long_liquidation", nullable = false, updatable = true, columnDefinition = "REAL")
	private Float longLiquidationThreshold;

	@Column(name = "threshold_short_liquidation", nullable = false, updatable = true, columnDefinition = "REAL")
	private Float shortLiquidationThreshold;

	@Column(name = "subscribed", nullable = false, updatable = true, columnDefinition = "BOOLEAN")
	private Boolean subscribed;

	public Subscriber() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Float getPumpThreshold() {
		return pumpThreshold;
	}

	public void setPumpThreshold(Float pumpThreshold) {
		this.pumpThreshold = pumpThreshold;
	}

	public Float getDumpThreshold() {
		return dumpThreshold;
	}

	public void setDumpThreshold(Float dumpThreshold) {
		this.dumpThreshold = dumpThreshold;
	}

	public Float getLongLiquidationThreshold() {
		return longLiquidationThreshold;
	}

	public void setLongLiquidationThreshold(Float longLiquidationThreshold) {
		this.longLiquidationThreshold = longLiquidationThreshold;
	}

	public Float getShortLiquidationThreshold() {
		return shortLiquidationThreshold;
	}

	public void setShortLiquidationThreshold(Float shortLiquidationThreshold) {
		this.shortLiquidationThreshold = shortLiquidationThreshold;
	}

	public Boolean getSubscribed() {
		return subscribed;
	}

	public void setSubscribed(Boolean subscribed) {
		this.subscribed = subscribed;
	}

	@Override
	public String toString() {
		return "Subscriber [id=" + id + ", pumpThreshold=" + pumpThreshold + ", dumpThreshold=" + dumpThreshold
				+ ", longLiquidationThreshold=" + longLiquidationThreshold + ", shortLiquidationThreshold="
				+ shortLiquidationThreshold + ", subscribed=" + subscribed + "]";
	}

}