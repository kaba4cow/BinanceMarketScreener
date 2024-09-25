package kaba4cow.marketscreener.utils.bars;

import java.util.LinkedList;

public class BarSeries {

	private final LinkedList<Bar> bars;
	private int maxBarCount;

	public BarSeries() {
		this.bars = new LinkedList<>();
		this.maxBarCount = 1000;
	}

	public BarSeries addBar(Bar newBar) {
		if (isEmpty())
			bars.add(newBar);
		else {
			Bar lastBar = getLast();
			if (newBar.getOpenTime() == lastBar.getOpenTime()) {
				bars.removeLast();
				bars.addLast(newBar);
			} else if (newBar.getOpenTime() > lastBar.getOpenTime()) {
				bars.addLast(newBar);
				if (getBarCount() > maxBarCount)
					bars.removeFirst();
			}
		}
		return this;
	}

	public Bar getBar(int index) {
		return bars.get(index);
	}

	public BarSeries setMaxBarCount(int maxBarCount) {
		this.maxBarCount = maxBarCount;
		while (getBarCount() > maxBarCount)
			bars.removeFirst();
		return this;
	}

	public Bar getLast() {
		return bars.getLast();
	}

	public Bar getFirst() {
		return bars.getFirst();
	}

	public int getLastIndex() {
		return getBarCount() - 1;
	}

	public int getBarCount() {
		return bars.size();
	}

	public boolean isEmpty() {
		return bars.isEmpty();
	}

}
