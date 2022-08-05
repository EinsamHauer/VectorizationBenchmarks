package net.iponweb.benchmarks;

import java.util.*;

public class Utils {

    private static final Random random = new Random();

    public static SparseVectorWithOnes generateSparseWithOnes(int size, float fill, float onesFraction) {

        // generate ones
        int numberOfOnes = (int) (size * fill * onesFraction);
        Set<Integer> onesIndices = new HashSet<>();

        while (onesIndices.size() < numberOfOnes) {
            onesIndices.add(random.nextInt(size));
        }

        // generate other values
        Map<Integer, Float> map = new HashMap<>();

        while(map.size() < (size * fill - numberOfOnes)) {
            int i = random.nextInt(size);
            if (!onesIndices.contains(i)) {
                map.put(i, random.nextFloat());
            }
        }

        return new SparseVectorWithOnes(onesIndices, map);
    }

    public static SparseVector generateSparse(int size, float fill, float onesFraction) {

        // generate ones
        int numberOfOnes = (int) (size * fill * onesFraction);
        Set<Integer> onesIndices = new HashSet<>();

        while (onesIndices.size() < numberOfOnes) {
            onesIndices.add(random.nextInt(size));
        }

        // generate other values
        Map<Integer, Float> map = new HashMap<>();

        while(map.size() < (size * fill - numberOfOnes)) {
            int i = random.nextInt(size);
            if (!onesIndices.contains(i)) {
                map.put(i, random.nextFloat());
            }
        }

        return new SparseVector(onesIndices, map);
    }

    public static float[] newFloatVector(int size) {
        float[] vector = new float[size];
        for (int i = 0; i < vector.length; ++i) {
            vector[i] = random.nextFloat();
        }
        return vector;
    }


}
