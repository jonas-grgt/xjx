package io.jonasg.xjx.serdes.deserialize;

import java.util.function.Supplier;

public class LazySupplier<T> implements Supplier<T> {
	private T instance;
	private Supplier<T> initializer;

	public LazySupplier(Supplier<T> initializer) {
		this.initializer = initializer;
	}

	@Override
	public T get() {
		if (instance == null) {
			instance = initializer.get();
		}
		return instance;
	}

	public void reset(Supplier<T> supplier) {
		this.instance = null;
		this.initializer = supplier;
	}
}

