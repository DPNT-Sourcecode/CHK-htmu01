package befaster.solutions.CHK;

import befaster.runner.SolutionNotImplementedException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CheckoutSolution {

    record Offer(Integer quantity, Integer price){}
    record Item(String sku, Integer price, Optional<Offer> offer){}

    private final List<Item> items = List.of(
            new Item("A", 50, Optional.of(new Offer(3, 130))),
            new Item("B", 30, Optional.of(new Offer(2, 45))),
            new Item("C", 20, Optional.empty()),
            new Item("D", 15, Optional.empty())
    );
    public Integer checkout(String skus) {

        System.out.println(skus.toCharArray());
       // var x = List.of(skus.toCharArray()).stream()
         //       .collect(Collectors.groupingBy(Item::sku))

                return 0;
    }

    public static void main(String[] args) {
        var x = new CheckoutSolution();

        var y = x.checkout("AB");

        System.out.println(y);


    }
}
