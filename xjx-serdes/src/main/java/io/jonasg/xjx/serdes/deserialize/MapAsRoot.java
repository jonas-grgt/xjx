package io.jonasg.xjx.serdes.deserialize;

import java.util.Map;

public record MapAsRoot(Object root, Map<String, Object> map) {
}
