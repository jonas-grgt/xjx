package io.jonasg.xjx.serdes.deserialize.accessor;

import io.jonasg.xjx.serdes.TypeMappers;
import io.jonasg.xjx.serdes.deserialize.RecordWrapper;
import io.jonasg.xjx.serdes.reflector.FieldReflector;

public interface FieldAccessor {

    static FieldAccessor of(FieldReflector field, Object instance) {
		if (instance instanceof RecordWrapper recordWrapper) {
			return new RecordFieldAccessor(field, recordWrapper);
		} else {
			var setterFieldAccessor = new SetterFieldAccessor(field, instance);
			if (setterFieldAccessor.hasSetterForField()) {
				return new SetterFieldAccessor(field, instance);
			}
			var mapper = TypeMappers.forType(field.type());
			return new ReflectiveFieldAccessor(field, instance, mapper);
		}
    }

    void set(Object value);

}
