package ai.clarity.pocs;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
    @MethodSource("argumentsProvider")
    void genericTest(int rows, int cols) {

        Pair<List<List<Double>>, Long> matrixPair = timeInMillis(() -> generateDoubleRandomMatrix(cols, rows));
        log.info("Random "+rows+" x "+cols+" generated in "+matrixPair.getSecond()+" ms");

        List<List<Double>> matrix = matrixPair.getFirst();

        Pair<List<Pair<String, Double>>, Long> result =
                timeInMillis(() -> IntStream.range(0, cols - 1).mapToObj(col -> {

                                    double[] col0Data = toDoubleArray(matrix.get(col));
                                    double[] col1Data = toDoubleArray(matrix.get(col + 1));
                                    double covariance = new Covariance().covariance(col0Data, col1Data);

                                    return new Pair<>("Columns [" + col + "," + col + 1 + "]", covariance);
                                }).collect(Collectors.toList())
                );

        log.info("Covariance calculation time "+result.getSecond()+" ms");
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


}
