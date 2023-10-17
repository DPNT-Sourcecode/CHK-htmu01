package befaster.solutions.CHK;

import befaster.solutions.SUM.SumSolution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CheckoutSolutionTest {

    private CheckoutSolution checkoutSolution;

    @BeforeEach
    public void setUp() {
        checkoutSolution = new CheckoutSolution();
    }

    @Test
    public void compute_sum() {
        assertThat(checkoutSolution.checkout("ABC"), equalTo(0));
    }
}
