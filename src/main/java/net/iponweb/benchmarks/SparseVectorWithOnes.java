package net.iponweb.benchmarks;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class SparseVectorWithOnes {

    public int[] onesIndices;
    public int[] indices;
    public float[] values;

    public SparseVectorWithOnes(Set<Integer> onesIndices, Map<Integer, Float> other) {
        this.onesIndices = new int[onesIndices.size()];
        int counter = 0;
        for (var i : onesIndices) {
            this.onesIndices[counter++] = i;
        }

        Arrays.sort(this.onesIndices);

        counter = 0;
        this.indices = new int[other.size()];
        this.values = new float[other.size()];

        List<Pair<Integer, Float>> l = other.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).map(e -> ImmutablePair.of(e.getKey(), e.getValue())).collect(Collectors.toList());
        for(var li : l) {
            this.indices[counter] = li.getKey();
            this.values[counter++] = li.getValue();
        }
    }


}
