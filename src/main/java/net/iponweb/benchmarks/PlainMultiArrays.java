package net.iponweb.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static net.iponweb.benchmarks.Utils.newFloatVector;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgsPrepend = {
        "--add-modules=jdk.incubator.vector",
        "-Xmx4g"
})
public class PlainMultiArrays {

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

    @Param({
            "100",
    })
    public int numberOfVectors;

    public float[] denseVector1;
    public float[][] denseVector2;

    @Setup(Level.Trial)
    public void init() {
        denseVector1 = newFloatVector(size);
        denseVector2 = new float[numberOfVectors][size];
        for (var i = 0; i < numberOfVectors; i++) denseVector2[i] = newFloatVector(size);
    }

    @Benchmark
    public float scalar() {
        float sum = 0f;
        for (var k = 0; k < numberOfVectors; k++) {
            for (int i = 0; i < size; ++i) {
                sum += denseVector1[i] * denseVector2[k][i];
            }
        }
        return sum;
    }

    @Benchmark
    public float vector() {
        float sum = 0f;
        for (var k = 0; k < numberOfVectors; k++) {
            sum += VectorizedDotProduct.dotDense(denseVector1, denseVector2[k]);
        }
        return sum;
    }
}
