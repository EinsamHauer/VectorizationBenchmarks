package net.iponweb.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static net.iponweb.benchmarks.Utils.generate;
import static net.iponweb.benchmarks.Utils.newFloatVector;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "--add-modules=jdk.incubator.vector"
})
public class WithOnesBenchmark {

    @Param({
            "32768",
            "65536",
            "131072",
            "262144",
            "524228",
            "1048576",
            "2097152",
            "4194304"
    })
    public int size;

    @Param({"0.5"})
    public float onesFraction;

    @Param({
/*            "0.01",
            "0.02",
            "0.03",
            "0.04",*/
            "0.05"
    })
    public float sparsity;

    public SparseVectorWithOnes sparseVector;
    public float[] denseVector;

    @Setup(Level.Trial)
    public void init() {
        sparseVector = generate(size, sparsity, onesFraction);
        denseVector = newFloatVector(size);
    }

    @Benchmark
    public float scalar() {
        float sum = 0f;

        for (var i : sparseVector.onesIndices) sum += denseVector[i];
        for (var i = 0; i < sparseVector.indices.length; i++) sum += Math.fma(denseVector[i], sparseVector.values[i], sum);
        return sum;
    }

    @Benchmark
    public float vector() {
        return (float) VectorizedDotProduct.dotWithOnes(sparseVector.onesIndices, sparseVector.indices, sparseVector.values, denseVector);
    }
}