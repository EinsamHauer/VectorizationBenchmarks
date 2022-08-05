package net.iponweb.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static net.iponweb.benchmarks.Utils.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2)
@Fork(value = 1, jvmArgsPrepend = {
        "--add-modules=jdk.incubator.vector",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
})
public class WithOnesBenchmark {

    @Param({
/*            "32768",
            "65536",
            "131072",
            "262144",
            "524228",
            "1048576",
            "2097152",*/
            "4194304"
    })
    public int size;

    @Param({"0.5"})
    public float onesFraction;

    @Param({
            "0.05"/*,
            "0.01",
            "0.02",
            "0.04",
            "0.08",
            "0.16",
            "0.32",
            "0.64",
            "0.7",
            "0.8",
            "0.9"*/
    })
    public float sparsity;

    public SparseVectorWithOnes sparseVector;
    public float[] denseVector;

    @Setup(Level.Trial)
    public void init() {
        sparseVector = generateSparseWithOnes(size, sparsity, onesFraction);
        denseVector = newFloatVector(size);
    }

    @Benchmark
    public float scalar() {
        float sum = 0f;

        for (var i : sparseVector.onesIndices) sum += denseVector[i];
        for (var i = 0; i < sparseVector.indices.length; i++) sum += denseVector[sparseVector.indices[i]] * sparseVector.values[i];
        return sum;
    }

    @Benchmark
    public float vector() {
        return (float) VectorizedDotProduct.dotWithOnes(sparseVector.onesIndices, sparseVector.indices, sparseVector.values, denseVector);
    }
}