package net.iponweb.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static net.iponweb.benchmarks.Utils.newFloatVector;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "--add-modules=jdk.incubator.vector"
})
public class PlainArrays {

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

    public float[] denseVector1;
    public float[] denseVector2;

    @Setup(Level.Trial)
    public void init() {
        denseVector1 = newFloatVector(size);
        denseVector2 = newFloatVector(size);
    }

    @Benchmark
    public float scalar() {
        float sum = 0f;
        for (int i = 0; i < size; ++i) {
            sum = Math.fma(denseVector1[i], denseVector2[i], sum);
        }
        return sum;
    }

    @Benchmark
    public float vector() {
        return (float) VectorizedDotProduct.dotDense(denseVector1, denseVector2);
    }
}
