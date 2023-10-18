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
        assertThat(checkoutSolution.checkout("AAABBBBC"), equalTo(240));
        assertThat(checkoutSolution.checkout("AAAAAAAAA"), equalTo(200+130+50));
        assertThat(checkoutSolution.checkout(null), equalTo(-1));
        assertThat(checkoutSolution.checkout(""), equalTo(0));
        assertThat(checkoutSolution.checkout("null"), equalTo(-1));
        assertThat(checkoutSolution.checkout(" "), equalTo(0));
        assertThat(checkoutSolution.checkout("-"), equalTo(-1));
        assertThat(checkoutSolution.checkout("ABCa"), equalTo(-1));
        assertThat(checkoutSolution.checkout("AADD"), equalTo(100+30));
        assertThat(checkoutSolution.checkout("EEEEEBBB"), equalTo(230));
        assertThat(checkoutSolution.checkout("FFFFF"), equalTo(40));
        assertThat(checkoutSolution.checkout("FF"), equalTo(20));
        assertThat(checkoutSolution.checkout("FFFF"), equalTo(30));
        assertThat(checkoutSolution.checkout("FFFFFF"), equalTo(40));
        assertThat(checkoutSolution.checkout("ZZZZZZZYXXAA"), equalTo(152+100));
        assertThat(checkoutSolution.checkout("ZZZ"), equalTo(45));
        assertThat(checkoutSolution.checkout("XX"), equalTo(34));
        assertThat(checkoutSolution.checkout("XXYY"), equalTo(45+17));
        assertThat(checkoutSolution.checkout("ABCDEFGHIJKLMNOPQRSTUVW"), equalTo(795));
    }
}




