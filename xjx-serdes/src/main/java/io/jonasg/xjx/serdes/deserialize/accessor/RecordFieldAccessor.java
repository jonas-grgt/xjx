package io.jonasg.xjx.serdes.deserialize.accessor;

import io.jonasg.xjx.serdes.TypeMappers;
import io.jonasg.xjx.serdes.deserialize.RecordWrapper;
import io.jonasg.xjx.serdes.reflector.FieldReflector;

public class RecordFieldAccessor implements FieldAccessor {

	private final FieldReflector field;

	private final RecordWrapper recordWrapper;

	public RecordFieldAccessor(FieldReflector field, RecordWrapper recordWrapper) {
		this.field = field;
		this.recordWrapper = recordWrapper;
	}

	@Override
	public void set(Object value) {
		Object mappedValue = TypeMappers.forType(field.type()).apply(value);
		recordWrapper.set(field.rawField().getName(), mappedValue);
	}
}
