package ai.clarity.pocs;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ai.clarity.pocs.TestUtils.*;

class CovarianceMatrixTest {

    private Logger log = Logger.getLogger(CovarianceMatrixTest.class.getName());

    private static final int onceAWeek5Years = 4 * 12 * 5;
    private static final int onceAWeek10Years = 4 * 12 * 10;
    private static final int onceADay5Years = 365 * 5;
    private static final int onceADay10Years = 365 * 10;

    private static final int securities = 135_000;


    @ParameterizedTest
    @MethodSource("argumentsProvider2")
    void genericTestDoubleObjetsApacheCommonsMath(int rows, int cols) {

        Pair<List<List<Double>>, Long> matrixPair = timeInMillis(() -> generateDoubleObjectRandomMatrix(cols, rows));
        log.info("Random "+rows+" x "+cols+" generated in "+matrixPair.getSecond()+" ms");

        List<List<Double>> matrix = matrixPair.getFirst();

        Covariance covarianceLib = new Covariance();

        Pair<String, Long> result = timeInMillis(() -> {

                IntStream.range(0, cols - 1).forEach(col -> {

                    double[] col0Data = toArray(matrix.get(col));
                    double[] col1Data = toArray(matrix.get(col + 1));
                    covarianceLib.covariance(col0Data, col1Data);

                });
                return "";
        });

        log.info("Covariance calculation time "+result.getSecond()+" ns");
    }


    @ParameterizedTest
    @MethodSource("argumentsProvider2")
    void genericTestDoublePrimitivesApacheCommonsMath(int rows, int cols) {

        Pair<double[][], Long> matrixPair = timeInMillis(() -> generateDoubleRandomMatrix(cols, rows));
        log.info("Random "+rows+" x "+cols+" generated in "+matrixPair.getSecond()+" ms");

        double[][] matrix = matrixPair.getFirst();

        Covariance covarianceLib = new Covariance();

        Pair<double[][], Long> result = timeInMillis(() -> {

            double covariances[][] = new double[cols][cols];

            IntStream.range(0, cols - 1).forEach(col ->
                    covarianceLib.covariance(matrix[col], matrix[col + 1])
            );

            return covariances;
        });

        log.info("Covariance calculation time "+result.getSecond()+" ns");
    }


    private static Stream<Arguments> argumentsProvider() {
        return Stream.of(
                Arguments.of(100, 2),                       // GenerateDoubles: 6 ms -- Covariance: 9ms
                Arguments.of(100_000, 2),                   // GenerateDoubles: 223 ms -- Covariance: 11ms
                Arguments.of(onceAWeek5Years, securities),  // GenerateDoubles: 32573 ms -- Covariance: 652ms
                Arguments.of(onceAWeek10Years, securities)  // GenerateDoubles: 50883 ms -- Covariance: 1291ms
                //Arguments.of(onceADay5Years, securities), // OurOfMemoryException
                //Arguments.of(onceADay10Years, securities) // OurOfMemoryException
        );
    }


    private static Stream<Arguments> argumentsProvider2() {
        return Stream.of(
                Arguments.of(onceAWeek5Years, 2),  // GenerateDoubles: 9 ms -- Covariance: 9ms
                Arguments.of(onceAWeek10Years, 2), // GenerateDoubles: 5 ms -- Covariance: 0ms
                Arguments.of(onceADay5Years, 2),   // GenerateDoubles: 10 ms -- Covariance: 0ms
                Arguments.of(onceADay10Years, 2)   // GenerateDoubles: 18 ms -- Covariance: 0ms
        );
    }


}
