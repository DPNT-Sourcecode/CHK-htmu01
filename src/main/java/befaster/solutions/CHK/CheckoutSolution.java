package befaster.solutions.CHK;

import befaster.runner.SolutionNotImplementedException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckoutSolution {

    record Offer(Integer quantity, Integer price){}
    record Item(String sku, Integer price, Optional<Offer> offer){}

    private final Map<String, Item> items = Map.of(
            "A",new Item("A", 50, Optional.of(new Offer(3, 130))),
            "B",new Item("B", 30, Optional.of(new Offer(2, 45))),
            "C",new Item("C", 20, Optional.empty()),
            "D",new Item("D", 15, Optional.empty())
    );
    public Integer checkout(String skus) {

        Stream.of(skus.split(""))
                .map(this::findItem)
                .collect(Collectors.groupingBy(Item::sku))
                .entrySet()
                .stream();
               // .reduce();

                return 0;
    }

    private Item findItem(final String sku){
        return items.get(sku);
    }
}

