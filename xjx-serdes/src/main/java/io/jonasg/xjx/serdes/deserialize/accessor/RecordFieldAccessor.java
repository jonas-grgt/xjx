package io.jonasg.xjx.serdes.deserialize.accessor;

import io.jonasg.xjx.serdes.TypeMappers;
import io.jonasg.xjx.serdes.deserialize.RecordWrapper;
import io.jonasg.xjx.serdes.deserialize.config.XjxConfiguration;
import io.jonasg.xjx.serdes.reflector.FieldReflector;

public class RecordFieldAccessor implements FieldAccessor {

	private final FieldReflector field;

	private final RecordWrapper recordWrapper;

	private final XjxConfiguration configuration;

	public RecordFieldAccessor(FieldReflector field, RecordWrapper recordWrapper, XjxConfiguration configuration) {
		this.field = field;
		this.recordWrapper = recordWrapper;
		this.configuration = configuration;
	}

	@Override
	public void set(Object value) {
		Object mappedValue = TypeMappers.forType(field.type(), configuration).apply(value);
		recordWrapper.set(field.rawField().getName(), mappedValue);
	}
}
