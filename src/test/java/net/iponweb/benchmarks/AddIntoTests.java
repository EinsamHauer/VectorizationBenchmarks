package net.iponweb.benchmarks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static net.iponweb.benchmarks.Utils.generateSparseWithOnes;
import static net.iponweb.benchmarks.Utils.newFloatVector;
import static org.junit.jupiter.api.Assertions.*;

public class AddIntoTests {

    @Test
    @DisplayName("Test addInto for vector with ones")
    public void testAddIntoWithOnes() {
        SparseVectorWithOnes sparseVector = generateSparseWithOnes(4194304, 0.05f, 0.5f);
        float[] target = newFloatVector(4194304);
        float[] scalarTarget = target.clone();

        VectorizedAddInto.addInto(1.23f, target, sparseVector.onesIndices, sparseVector.indices, sparseVector.values);
        scalarAddInto(1.23f, scalarTarget, sparseVector.onesIndices, sparseVector.indices, sparseVector.values);

        assertArrayEquals(target, scalarTarget, 0.01f);
    }

    private void scalarAddInto(float multiplier, float[] target, int[] onesIndices, int[] indices, float[] values) {
        for (var i = 0; i < indices.length; i++) {
            target[indices[i]] += values[i] * multiplier;
        }

        for (var i = 0; i < onesIndices.length; i++) {
            target[onesIndices[i]] += multiplier;
        }
    }
}
