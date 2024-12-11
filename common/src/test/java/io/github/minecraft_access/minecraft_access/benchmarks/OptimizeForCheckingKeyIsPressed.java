package io.github.minecraft_access.minecraft_access.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Should I imperatively avoid using streams when the input has only one element for checking key is pressed?
 * <p>
 * result:
 * Benchmark                                                  Mode  Cnt   Score   Error  Units
 * OptimizeForCheckingKeyIsPressed.notUseStreamForOneElement  avgt   25   0.471 ± 0.004  ns/op
 * OptimizeForCheckingKeyIsPressed.useStreamForOneElement     avgt   25  18.463 ± 0.146  ns/op
 * <p>
 * 18 nanos diff, quite a small difference,
 * there are about 60 invokes on this method,
 * 18 * 60 = 1080 nanos = 0.001 millis
 * One tick is 50 millis, so the diff takes a very small part of one tick.
 * Conclusion: I should not avoid using streams.
 * <p>
 * ref:
 * There aren't big general speed differences between streams and loops: <a href="https://stackoverflow.com/a/34632749/11397457">...</a>
 * <a href="https://minecraft.fandom.com/wiki/Tick">...</a>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class OptimizeForCheckingKeyIsPressed {
    private static final int[] keyCode = new int[]{0};

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public boolean useStreamForOneElement() {
        int handle = 1;
        return IntStream.of(keyCode).anyMatch(c -> mockIsKeyPressed(handle, c));
    }

    @Benchmark
    public boolean notUseStreamForOneElement() {
        int handle = 1;
        return mockIsKeyPressed(handle, keyCode[0]);
    }

    private static boolean mockIsKeyPressed(int handle, int code) {
        return handle > code;
    }
}
