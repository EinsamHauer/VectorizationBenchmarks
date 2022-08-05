package net.iponweb.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static net.iponweb.benchmarks.Utils.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "--add-modules=jdk.incubator.vector",
        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"
})
public class SparseBenchmark {

    @Param({
            "4194304"
    })
    public int size;

    @Param({"0.5"})
    public float onesFraction;

    @Param({
            "0.01",
            "0.02",
            "0.04",
            "0.08",
            "0.16",
            "0.32",
            "0.64",
            "0.7",
            "0.8",
            "0.9"
    })
    public float sparsity;

    public SparseVector sparseVector;
    public float[] denseVector;

    @Setup(Level.Trial)
    public void init() {
        sparseVector = generateSparse(size, sparsity, onesFraction);
        denseVector = newFloatVector(size);
    }

    @Benchmark
    public float scalar() {
        float sum = 0f;

        for (var i = 0; i < sparseVector.indices.length; i++) sum += Math.fma(denseVector[i], sparseVector.values[i], sum);
        return sum;
    }

    @Benchmark
    public float vector() {
        return (float) VectorizedDotProduct.dotSparse(sparseVector.indices, sparseVector.values, denseVector);
    }
}