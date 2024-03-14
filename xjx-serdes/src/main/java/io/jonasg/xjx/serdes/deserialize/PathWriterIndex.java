package io.jonasg.xjx.serdes.deserialize;

import io.jonasg.xjx.serdes.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathWriterIndex {

	private final Map<Path, List<PathWriter>> index = new HashMap<>();

	public void put(Path path, PathWriter pathWriter) {
		index.compute(path, (p, w) -> {
			if (w == null) {
				List<PathWriter> pathWriters = new ArrayList<>();
				pathWriters.add(pathWriter);
				return pathWriters;
			}
			w.add(pathWriter);
			return w;
		});
	}

	public void putAll(PathWriterIndex pathWriterIndex) {
		index.putAll(pathWriterIndex.index);
	}

	public List<PathWriter> get(Path path) {
		return index.get(path);
	}
}
