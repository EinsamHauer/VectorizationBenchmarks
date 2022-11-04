package net.iponweb.benchmarks;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class VectorizedAddInto {
    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    public static void addInto(float multiplier, float[] target, int[] onesIndices, int[] indices, float[] values) {
        var multiplierVector = FloatVector.broadcast(SPECIES, multiplier);

        // values
        var upperBound = SPECIES.loopBound(indices.length);
        var i = 0;
        for (; i < upperBound; i += SPECIES.length()) {
            var v1 = FloatVector.fromArray(SPECIES, values, i);
            var v2 = FloatVector.fromArray(SPECIES, target, 0, indices, i);

            v1.fma(multiplierVector, v2).intoArray(target, 0, indices, i);
        }

        for (; i < indices.length; i++) {
            target[indices[i]] += values[i] * multiplier;
        }

        // ones
        upperBound = SPECIES.loopBound(onesIndices.length);
        i = 0;
        for (; i < upperBound; i += SPECIES.length()) {
            var v = FloatVector.fromArray(SPECIES, target, 0, onesIndices, i);

            v.add(multiplierVector).intoArray(target, 0, onesIndices, i);
        }

        for (; i < onesIndices.length; i++) {
            target[onesIndices[i]] += multiplier;
        }
    }

}
