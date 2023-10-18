package befaster.solutions.CHK;

import befaster.runner.SolutionNotImplementedException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckoutSolution {

    record Offer(Integer quantity, Integer price, Integer free){}
    record Item(String sku, Integer price, List<Offer> offers){}

    private final Map<String, Item> items = Map.of(
            "A",new Item("A", 50, List.of(new Offer(3, 130, 0))),
            "B",new Item("B", 30, List.of(new Offer(2, 45, 0))),
            "C",new Item("C", 20, List.of()),
            "D",new Item("D", 15, List.of()),
            "E",new Item("E", 40, List.of(new Offer(2, 40, 1)))
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
        if(item.offers.isEmpty()){
            return quantity * item.price();
        }
        return item
                .offers()
                .stream()
                .mapToInt(offer -> {
                    final var priceWithDiscount = (quantity / offer.quantity()) * offer.price();
                    final var priceWithoutDiscount = (quantity % offer.quantity()) * item.price();
                    return priceWithDiscount + priceWithoutDiscount;
                }).sum();
    }

    private Map<String, Long> countBy(final String skus){
        return Stream.of(skus.split(""))
                .map(items::get)
                .map(item -> Optional.ofNullable(item).orElseThrow())
                .collect(Collectors.groupingBy(Item::sku, Collectors.counting()));
    }
}
