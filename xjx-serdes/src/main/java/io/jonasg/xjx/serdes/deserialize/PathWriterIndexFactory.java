package io.jonasg.xjx.serdes.deserialize;

import java.lang.reflect.Field;
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import io.jonasg.xjx.serdes.Path;
import io.jonasg.xjx.serdes.Tag;
import io.jonasg.xjx.serdes.TypeMappers;
import io.jonasg.xjx.serdes.deserialize.accessor.FieldAccessor;
import io.jonasg.xjx.serdes.deserialize.config.XjxConfiguration;
import io.jonasg.xjx.serdes.reflector.FieldReflector;
import io.jonasg.xjx.serdes.reflector.TypeReflector;

public class PathWriterIndexFactory {

    public static final List<Class<?>> BASIC_TYPES = List.of(
            String.class, Integer.class, int.class, Boolean.class, boolean.class, Long.class, long.class, BigDecimal.class, Double.class,
            double.class, char.class, Character.class, LocalDate.class, LocalDateTime.class, ZonedDateTime.class);

	private final XjxConfiguration configuration;

	private final Map<Class<?>, Object> collectionCacheType = new HashMap<>();

	public PathWriterIndexFactory(XjxConfiguration xjxConfiguration) {
		this.configuration = xjxConfiguration;
	}

	public <T> Map<Path, PathWriter> createIndexForType(Class<T> type, String rootTag) {
        Path path = Path.of(rootTag);
        return buildIndex(type, path);
    }

    private <T> Map<Path, PathWriter> buildIndex(Class<T> type, Path path) {
        Map<Path, PathWriter> index = new HashMap<>();
		if (type.isRecord()) {
			RecordWrapper<T> recordWrapper = new RecordWrapper<>(type);
			index.put(path, PathWriter.rootInitializer(() -> recordWrapper));
			return doBuildIndex(type, path, index, () -> recordWrapper);
		} else {
			T root = TypeReflector.reflect(type).instanceReflector().instance();
			index.put(path, PathWriter.rootInitializer(() -> root));
			return doBuildIndex(type, path, index, () -> root);
		}
    }

    private Map<Path, PathWriter> doBuildIndex(Class<?> type,
                                               Path path,
                                               Map<Path, PathWriter> index,
                                               Supplier<Object> root) {
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
            indexMapType(field, index, path, parent);
        } else if (field.type().isEnum()) {
			indexEnumType(field, index, path, parent);
		} else if (field.isRecord()) {
			indexRecordType(field, index, path, parent);
        } else {
            indexComplexType(field, index, path, parent);
        }
    }

	private void indexRecordType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
		RecordWrapper<?> recordWrapper = new RecordWrapper<>(field.type());
		index.put(getPathForField(field, path), PathWriter.objectInitializer(() -> {
			return recordWrapper;
		}).setValueInitializer((value) -> {
			if (value instanceof RecordWrapper<?> recordWrapperValue) {
				FieldAccessor.of(field, parent.get(), configuration).set(recordWrapperValue.record());
			}
		}));
		doBuildIndex(field.type(), getPathForField(field, path), index, () -> recordWrapper);
	}

	private void indexMapType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        Path pathForField = getPathForField(field, path);
        if (pathForField.isRoot()) {
            indexMapAsRootType(field, index, parent, pathForField);
        } else {
            doIndexMapType(field, index, parent, pathForField);
        }
    }

    private void doIndexMapType(FieldReflector field,
                                       Map<Path, PathWriter> index,
                                       Supplier<Object> parent,
                                       Path pathForField) {
        index.put(pathForField, PathWriter.objectInitializer(() -> {
            Map<String, Object> map = new HashMap<>();
            Class<?> valueType = (Class<?>) ((ParameterizedType) field.genericType()).getActualTypeArguments()[1];
            FieldAccessor.of(field, parent.get(), configuration).set(map);
            if (valueType.equals(Object.class)) {
                return map;
            } else {
                return new MapWithTypeInfo(map, valueType);
            }
        }));
    }

    private void indexMapAsRootType(FieldReflector field,
                                           Map<Path, PathWriter> index,
                                           Supplier<Object> parent,
                                           Path pathForField) {
        index.put(pathForField, PathWriter.rootInitializer(() -> {
            Map<String, Object> map = new HashMap<>();
            FieldAccessor.of(field, parent.get(), configuration).set(map);
            return new MapAsRoot(parent.get(), map);
        }));
    }

    private void indexComplexType(FieldReflector field,
                                  Map<Path, PathWriter> index,
                                  Path path,
                                  Supplier<Object> parent) {
        if (field.hasAnnotation(Tag.class)) {
            doIndexComplexType(field, index, path, parent);
        } else {
            searchFieldsRecursivelyForTag(field)
                    .ifPresent(tagPath -> {
                        if (tagPath.isAbsolute()) {
                            doIndexComplexType(field, index, path, parent);
                        } else {
                            throw new XjxDeserializationException("Field " + tagPath.field().name() +
                                                                  " is annotated with @Tag but one of it's parent " +
                                                                  "is missing a @Tag.");
                        }
                    });
        }
    }

    private void doIndexComplexType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        if (field.hasAnnotation(ValueDeserialization.class)) {
            index.put(getPathForField(field, path), PathWriter.valueInitializer((value) -> {
                value = ValueDeserializationHandler.getInstance().handle(field.rawField(), (String) value)
                        .orElse(value);
                FieldAccessor.of(field, parent.get(), configuration).set(value);
            }));
        } else {
            Supplier<Object> complexTypeSupplier = () -> {
                if (collectionCacheType.containsKey(field.type())) {
                    return collectionCacheType.get(field.type());
                }
                Object complexType = TypeReflector.reflect(field.type()).instanceReflector().instance();
                collectionCacheType.put(field.type(), complexType);
                FieldAccessor.of(field, parent.get(), configuration).set(complexType);
                return complexType;
            };
            index.putAll(doBuildIndex(field.type(), getPathForField(field, path), index, complexTypeSupplier));
        }
    }

    private Optional<TagPath> searchFieldsRecursivelyForTag(FieldReflector field) {
        if (field.hasAnnotation(Tag.class)) {
            return Optional.of(new TagPath(field.getAnnotation(Tag.class), field));
        }

        Class<?> fieldType = field.type();
        Field[] fields = fieldType.getDeclaredFields();

        for (Field subField : fields) {
            FieldReflector subFieldReflector = new FieldReflector(subField);
            if (BASIC_TYPES.contains(subField.getType())) {
                if (subFieldReflector.hasAnnotation(Tag.class)) {
                    return Optional.of(new TagPath(subFieldReflector.getAnnotation(Tag.class), subFieldReflector));
                }
                return Optional.empty();
            }
            return searchFieldsRecursivelyForTag(subFieldReflector);
        }
        return Optional.empty();
    }

    private void indexEnumType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        index.put(getPathForField(field, path), PathWriter.valueInitializer((value) -> {
            if (field.hasAnnotation(ValueDeserialization.class)) {
                value = ValueDeserializationHandler.getInstance().handle(field.rawField(), (String) value)
                        .orElse(value);
            }
            FieldAccessor.of(field, parent.get(), configuration).set(value);
        }));
    }

    private void indexSimpleType(FieldReflector field, Map<Path, PathWriter> index, Path path, Supplier<Object> parent) {
        if (field.hasAnnotation(Tag.class)) {
            index.put(getPathForField(field, path), PathWriter.valueInitializer((value) -> {
                if (value instanceof String) {
                    value = ValueDeserializationHandler.getInstance().handle(field.rawField(), (String) value)
                            .orElse(value);
                }
                FieldAccessor.of(field, parent.get(), configuration).set(value);
            }));
        }
    }

    private void indexSetType(FieldReflector field, Map<Path, PathWriter> index, Path parentPath, Supplier<Object> parent) {
        Collection<Object> set = new HashSet<>();
        Path path = getPathForField(field, parentPath);
        var pathWriter = PathWriter.objectInitializer(() -> {
            FieldAccessor.of(field, parent.get(), configuration).set(set);
            return set;
        });
        if (path.isRoot()) {
            pathWriter.setRootInitializer(() -> {
                FieldAccessor.of(field, parent.get(), configuration).set(set);
                return parent.get();
            });
        }
        index.put(path, pathWriter);

        indexListTypeArgument(path, field, index, set);
    }

    private Supplier<Object> collectionSupplierForType(Class<?> typeArgument) {
        return () -> {
            if (collectionCacheType.get(typeArgument) != null) {
                return collectionCacheType.get(typeArgument);
            }
			if (typeArgument.isRecord()) {
				var recordWrapper = new RecordWrapper<>(typeArgument);
				collectionCacheType.put(typeArgument, recordWrapper);
				return recordWrapper;
			} else {
				Object listTypeInstance = TypeReflector.reflect(typeArgument).instanceReflector().instance();
				collectionCacheType.put(typeArgument, listTypeInstance);
				return listTypeInstance;
			}
        };
    }

    private void indexListType(FieldReflector field, Map<Path, PathWriter> index, Path parentPath, Supplier<Object> parent) {
        List<Object> list = new ArrayList<>();
        Path path = getPathForField(field, parentPath);
        var pathWriter = PathWriter.objectInitializer(() -> {
            FieldAccessor.of(field, parent.get(), configuration).set(list);
            return list;
        });
        if (path.isRoot()) {
            pathWriter.setRootInitializer(() -> {
                FieldAccessor.of(field, parent.get(), configuration).set(list);
                return parent.get();
            });
        }
        index.put(path, pathWriter);

        indexListTypeArgument(path, field, index, list);
    }

    private void indexListTypeArgument(Path path, FieldReflector field, Map<Path, PathWriter> index, Collection<Object> list) {
        Type actualTypeArgument = ((ParameterizedType) field.genericType()).getActualTypeArguments()[0];
        Class<?> typeArgument = (Class<?>) actualTypeArgument;
        if (TypeMappers.TYPES.contains(typeArgument)) {
            indexSimpleTypeListTypeArgument(path, index, list, field, typeArgument);
        } else {
            indexComplexListTypeArgument(index, list, typeArgument, field);
        }
    }

    private void indexSimpleTypeListTypeArgument(Path path, Map<Path, PathWriter> index, Collection<Object> list, FieldReflector field, Class<?> typeArgument) {
        Tag tag = field.getAnnotation(Tag.class);
        index.put(path.append(Path.parse(tag.items())),
                PathWriter.valueInitializer((o) -> list.add(TypeMappers.forType(typeArgument, configuration).apply(o))));
    }

    private void indexComplexListTypeArgument(Map<Path, PathWriter> index, Collection<Object> list, Class<?> typeArgument, FieldReflector field) {
        Supplier<Object> listTypeInstanceSupplier = collectionSupplierForType(typeArgument);
        Tag tag = field.getAnnotation(Tag.class);
        if (tag.items().isBlank()) {
            throw new XjxDeserializationException(
                  """
                  Field (%s) requires @Tag to have items parameter describing\
                   the tag name of a single repeated tag""".formatted(typeArgument.getSimpleName() ));
        }
        Path path = Path.parse(tag.path()).append(Path.parse(tag.items()));
        index.put(path, PathWriter.objectInitializer(() -> {
            collectionCacheType.clear();
            Object listTypeInstance = listTypeInstanceSupplier.get();
            list.add(listTypeInstance);
            return listTypeInstance;
        }).setValueInitializer((value) -> {
			if (value instanceof RecordWrapper<?> recordWrapperValue) {
				list.remove(recordWrapperValue);
				list.add(recordWrapperValue.record());
			}
		}));
        doBuildIndex(typeArgument, path, index, listTypeInstanceSupplier);
    }

    private Path getPathForField(FieldReflector field, Path path) {
        Tag tag = field.getAnnotation(Tag.class);
        if (tag != null) {
            TagPath tagPath = new TagPath(tag, field);
            Path activePath;
            if (tagPath.isAbsolute()) {
                activePath = Path.parse(tagPath.path());
            } else {
                activePath = path.append(Path.parse(tagPath.path()));
            }
            if (tag.attribute().isEmpty()) {
                return activePath;
            }
            return activePath.appendAttribute(tagPath.attribute());
        }
        return path.append(field.name());
    }
}
