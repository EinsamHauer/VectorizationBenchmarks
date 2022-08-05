package net.iponweb.benchmarks;

import jdk.incubator.vector.*;

public class VectorizedDotProduct {
    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    public static double dotDense(float[] vd1, float[] vd2) {
        var product = 0f;

        var upperBound = SPECIES.loopBound(vd1.length);
        var i = 0;
        for (; i < upperBound; i += SPECIES.length()) {
            var v1 = FloatVector.fromArray(SPECIES, vd1, i);
            var v2 = FloatVector.fromArray(SPECIES, vd2, i);

            product += v1.mul(v2).reduceLanes(VectorOperators.ADD);
        }

        for (; i < vd1.length; i++) {
            product += vd1[i] * vd2[i];
        }

        return product;
    }

    public static double dotSparse(int[] indices, float[] values, float[] dense) {
        return multiplySimpleVectors(indices, values, dense, FloatVector.zero(SPECIES));
    }

    public static double dotScalar(int[] onesIndices, int[] indices, float[] values, float[] dense) {
        float sum = 0f;

        for (var i : onesIndices) sum += dense[i];
        for (var i = 0; i < indices.length; i++) sum += dense[i] * values[i];
        return sum;
    }

    public static double dotWithOnes(int[] onesIndices, int[] indices, float[] values, float[] dense) {
        var sum = FloatVector.zero(SPECIES);
        var product = 0f;

        // process ones first
        int upperBound = SPECIES.loopBound(onesIndices.length);
        var i = 0;
        for (; i < upperBound; i += SPECIES.length()) {
            var v = FloatVector.fromArray(SPECIES, dense, 0, onesIndices, i);

            sum = v.add(sum);
        }

        for (; i < onesIndices.length; i++) {
            product += dense[onesIndices[i]];
        }

        return product + multiplySimpleVectors(indices, values, dense, sum);
    }

    public static double dotWithFreqs(float[] freqValues, int[][] freqIndices, int[] indices, float[] values, float[] dense) {
        var sum = FloatVector.zero(SPECIES);
        var product = 0f;

        // process frequent values first
        for (var i = 0; i < freqIndices.length; i++) {
            var upperBound = SPECIES.loopBound(freqIndices[i].length);
            var j = 0;

            for (; j < upperBound; j += SPECIES.length()) {
                var v = FloatVector.fromArray(SPECIES, dense, 0, freqIndices[i], j);
                sum = sum.add(v.mul(freqValues[i]));
            }

            for (; j < freqIndices[i].length; j++) {
                product += freqValues[i] * dense[freqIndices[i][j]];
            }
        }

        return product + multiplySimpleVectors(indices, values, dense, sum);
    }

    private static float multiplySimpleVectors(int[] indices, float[] values, float[] dense, FloatVector sum) {
        var product = 0f;

        var upperBound = SPECIES.loopBound(indices.length);
        var i = 0;
        for (; i < upperBound; i += SPECIES.length()) {
            var v1 = FloatVector.fromArray(SPECIES, values, i);
            var v2 = FloatVector.fromArray(SPECIES, dense, 0, indices, i);

            sum = v1.fma(v2, sum);
        }

        product += sum.reduceLanes(VectorOperators.ADD);

        for (; i < indices.length; i++) {
            product += values[i] * dense[indices[i]];
        }

        return product;
    }
}
