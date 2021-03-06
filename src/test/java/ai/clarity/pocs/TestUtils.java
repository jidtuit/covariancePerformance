package ai.clarity.pocs;

import org.apache.commons.math3.util.Pair;

import java.security.SecureRandom;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class TestUtils {

    private static SecureRandom random = new SecureRandom();

    public static Double nextDouble() {
        return random.nextDouble();
    }

    public static double nextDoublePrimitive() {
        return random.nextDouble();
    }

    public static double[][] generateDoubleRandomMatrix(int cols, int rows) {
        double resp[][] = new double[cols][rows];

        IntStream.range(0, cols)
                .forEach(col ->
                    IntStream.range(0, rows)
                        .forEach(row -> resp[col][row] = nextDoublePrimitive())
                );

        return resp;
    }



    public static List<List<Double>> generateDoubleObjectRandomMatrix(int cols, int rows) {
        return generateRandomMatrix(cols, rows, TestUtils::nextDouble);
    }


    public static <T> List<List<T>> generateRandomMatrix(int cols, int rows, Supplier<T> itemGenerator) {

        return IntStream.range(0, cols)
                .boxed()
                .map(col -> IntStream.range(0, rows)
                        .mapToObj(i -> itemGenerator.get())
                        .collect(Collectors.toList())
                ).collect(Collectors.toList());

    }


    public static double[] toArray(Double array[]) {
        double[] resp = new double[array.length];

        IntStream.range(0, array.length)
                .forEach(i -> resp[i] = array[i]);

        return resp;
    }


    public static double[] toArray(List<Double> doubles) {

        double resp[] = new double[doubles.size()];

        IntStream.range(0, doubles.size())
                .forEach(i -> resp[i] = doubles.get(i));

        return resp;
    }


    public static <T> Pair<T, Long> timeInMillis(Supplier<T> supplier) {

        long init = System.nanoTime();
        T t = supplier.get();
        long end = System.nanoTime();

        return new Pair<>(t, (end-init)/1_000_000);
    }

}
