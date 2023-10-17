package befaster.solutions.CHK;

import befaster.runner.SolutionNotImplementedException;

import java.util.*;
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

        if(skus == null){
            return -1;
        }
        if(skus.isBlank()){
            return 0;
        }

        try {
            final var countBySku = countBy(skus.trim());
            return items
                    .entrySet()
                    .stream()
                    .mapToInt(e -> calculatePriceBasedOn(e.getValue(),
                            countBySku.getOrDefault(e.getKey(), 0L).intValue()))
                    .sum();
        } catch (Exception e){
            System.out.println("Invalid Sku list");
            return -1;
        }
    }

    private static Integer calculatePriceBasedOn(final Item item, final Integer quantity){
        return item
                .offer()
                .map(offer -> {
                    final var priceWithDiscount = (quantity / offer.quantity()) * offer.price();
                    final var priceWithoutDiscount = (quantity % offer.quantity()) * item.price();
                    return priceWithDiscount + priceWithoutDiscount;
                })
                .orElseGet(() -> quantity * item.price());
    }

    private Map<String, Long> countBy(final String skus){
        return Stream.of(skus.split(""))
                .map(items::get)
                .map(item -> Optional.ofNullable(item).orElseThrow())
                .collect(Collectors.groupingBy(Item::sku, Collectors.counting()));
    }
}

