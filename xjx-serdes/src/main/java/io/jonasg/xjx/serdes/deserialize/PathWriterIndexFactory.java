package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.deserialize.accessor.FieldAccessor;
import io.jonasg.xjx.serdes.reflector.FieldReflector;
import io.jonasg.xjx.serdes.reflector.Reflector;
import io.jonasg.xjx.serdes.reflector.TypeReflector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PathWriterIndexFactory {

    private static final List<Class<?>> BASIC_TYPES = List.of(
            String.class, Integer.class, Long.class, long.class, BigDecimal.class, Double.class,
            double.class, char.class, Character.class, LocalDate.class, LocalDateTime.class, ZonedDateTime.class);
    private final Map<Class<?>, Object> listTypeCache = new HashMap<>();

    public <T> Map<Path, PathWriter> createIndexForType(Class<T> type, String rootTag) {
        Path path = Path.of(rootTag);
        return buildIndex(type, path);
    }

    private <T> Map<Path, PathWriter> buildIndex(Class<T> type, Path path) {
        Map<Path, PathWriter> index = new HashMap<>();
        T root = TypeReflector.reflect(type).instanceReflector().instance();
        index.put(path, PathWriter.initializer(() -> root));
        return doBuildIndex(type, path, index, () -> root);
    }

    private Map<Path, PathWriter> doBuildIndex(Class<?> type, Path path, Map<Path, PathWriter> index, Supplier<Object> root) {
        TypeReflector.reflect(type).fields()
                .forEach(field -> indexField(field, index, path, root));
        return index;
    }

    private void indexField(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        if (BASIC_TYPES.contains(field.type())) {
            indexSimpleType(field, index, path, parent);
        } else if (List.class.equals(field.type())) {
            indexListType(field, index, path, parent);
        } else if (Set.class.equals(field.type())) {
            indexSetType(field, index, path, parent);
        } else if (Map.class.equals(field.type())) {
            index.put(getPathForField(field, path), PathWriter.objectInitializer(() -> {
                HashMap<Object, Object> map = new HashMap<>();
                FieldAccessor.of(field, parent.get()).set(map);
                return map;
            }));
        } else if (field.type().isEnum()) {
            indexEnumType(field, index, path, parent);
        } else {
            indexComplexType(field, index, path, parent);
        }
    }

    private void indexComplexType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        if (field.isAnnotatedWith(ValueDeserialization.class)) {
            index.put(getPathForField(field, path), PathWriter.valueInitializer((value) -> {
                value = ValueDeserializationHandler.getInstance().handle(field.rawField(), (String)value)
                        .orElse(value);
                FieldAccessor.of(field, parent.get()).set(value);
            }));
        } else {
            Supplier<Object> complexTypeSupplier = () -> {
                if (listTypeCache.containsKey(field.type())) {
                    return listTypeCache.get(field.type());
                }
                Object complexType = TypeReflector.reflect(field.type()).instanceReflector().instance();
                listTypeCache.put(field.type(), complexType);
                FieldAccessor.of(field, parent.get()).set(complexType);
                return complexType;
            };
            index.putAll(doBuildIndex(field.type(), getPathForField(field, path), index, complexTypeSupplier));
        }
    }

    private void indexEnumType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        index.put(getPathForField(field, path), PathWriter.valueInitializer((value) -> {
            if (field.isAnnotatedWith(ValueDeserialization.class)) {
                value = ValueDeserializationHandler.getInstance().handle(field.rawField(), (String)value)
                        .orElse(value);
            }
            FieldAccessor.of(field, parent.get()).set(value);
        }));
    }

    private void indexSimpleType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        if (field.isAnnotatedWith(Tag.class)) {
            index.put(getPathForField(field, path), PathWriter.valueInitializer((value) -> {
                if (value instanceof String) {
                    value = ValueDeserializationHandler.getInstance().handle(field.rawField(), (String) value)
                            .orElse(value);
                }
                FieldAccessor.of(field, parent.get()).set(value);
            }));
        }
    }

    private void indexSetType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        Collection<Object> set = new HashSet<>();
        index.put(getPathForField(field, path), PathWriter.objectInitializer(() -> {
            FieldAccessor.of(field, parent.get()).set(set);
            return set;
        }));
        Type actualTypeArgument = ((ParameterizedType) field.genericType()).getActualTypeArguments()[0];
        Class<?> typeArgument = (Class<?>) actualTypeArgument;
        var tag = Reflector.reflect(typeArgument).annotation(Tag.class);
        if (tag != null) {
            Supplier<Object> listTypeInstanceSupplier = () -> {
                if (listTypeCache.get(typeArgument) != null) {
                    return listTypeCache.get(typeArgument);
                }
                Object listTypeInstance = TypeReflector.reflect(typeArgument).instanceReflector().instance();
                listTypeCache.put(typeArgument, listTypeInstance);
                return listTypeInstance;
            };
            index.put(Path.parse(tag.path()), PathWriter.objectInitializer(() -> {
                listTypeCache.clear();
                Object listTypeInstance = listTypeInstanceSupplier.get();
                set.add(listTypeInstance);
                return listTypeInstance;
            }));
            doBuildIndex(typeArgument, path, index, listTypeInstanceSupplier);
        }
    }

    private void indexListType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        List<Object> list = new ArrayList<>();
        index.put(getPathForField(field, path), PathWriter.objectInitializer(() -> {
            FieldAccessor.of(field, parent.get()).set(list);
            return list;
        }));
        Type actualTypeArgument = ((ParameterizedType) field.genericType()).getActualTypeArguments()[0];
        Class<?> typeArgument = (Class<?>) actualTypeArgument;
        var tag = Reflector.reflect(typeArgument).annotation(Tag.class);
        if (tag != null) {
            Supplier<Object> listTypeInstanceSupplier = () -> {
                if (listTypeCache.get(typeArgument) != null) {
                    return listTypeCache.get(typeArgument);
                }
                Object listTypeInstance = TypeReflector.reflect(typeArgument).instanceReflector().instance();
                listTypeCache.put(typeArgument, listTypeInstance);
                return listTypeInstance;
            };
            index.put(Path.parse(tag.path()), PathWriter.objectInitializer(() -> {
                listTypeCache.clear();
                Object listTypeInstance = listTypeInstanceSupplier.get();
                list.add(listTypeInstance);
                return listTypeInstance;
            }));
            doBuildIndex(typeArgument, path, index, listTypeInstanceSupplier);
        }
    }

    private Path getPathForField(FieldReflector field, Path path) {
        Tag tag = field.getAnnotation(Tag.class);
        if (tag != null) {
            Path activePath = Path.parse(tag.path());
            if (tag.attribute().isEmpty()) {
                return activePath;
            }
            return activePath.appendAttribute(tag.attribute());
        }
        return path.append(field.name());
    }

}
