package net.iponweb.benchmarks;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class SparseVector {

    public int[] indices;
    public float[] values;

    public SparseVector(Set<Integer> onesIndices, Map<Integer, Float> other) {
        for (var i : onesIndices) {
            other.put(i, 1.0f);
        }
        int counter = 0;

        this.indices = new int[other.size()];
        this.values = new float[other.size()];

        List<Pair<Integer, Float>> l = other.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).map(e -> ImmutablePair.of(e.getKey(), e.getValue())).collect(Collectors.toList());
        for(var li : l) {
            this.indices[counter] = li.getKey();
            this.values[counter++] = li.getValue();
        }
    }


}
