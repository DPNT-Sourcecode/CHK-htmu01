package befaster.solutions.CHK;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckoutSolution {

    enum OfferType{
        PRICE_DISCOUNT, FREE_ITEM
    }

    record Offer(Integer triggerQuantity, Integer unit, String sku, OfferType offerType){}
    record Item(String sku, Integer price, List<Offer> offers){}

    private final Map<String, Item> items = Map.of(
            "A",new Item("A", 50, List.of(
                    new Offer(3, 130, "A", OfferType.PRICE_DISCOUNT),
                    new Offer(5, 200, "A", OfferType.PRICE_DISCOUNT))),
            "B",new Item("B", 30, List.of(new Offer(2, 45, "B", OfferType.PRICE_DISCOUNT))),
            "C",new Item("C", 20, List.of()),
            "D",new Item("D", 15, List.of()),
            "E",new Item("E", 40, List.of(new Offer(2, 1, "B", OfferType.FREE_ITEM))),
            "F",new Item("F", 10, List.of(new Offer(3, 1, "F", OfferType.FREE_ITEM))),
            "G",new Item("G", 20, List.of()),
            "H",new Item("H", 10, List.of(
                    new Offer(5, 45, "H", OfferType.PRICE_DISCOUNT),
                    new Offer(10, 80, "H", OfferType.PRICE_DISCOUNT))),
            "I",new Item("I", 35, List.of()),
            "J",new Item("J", 60, List.of()),
            "K",new Item("K", 80, List.of(new Offer(2, 150, "K", OfferType.PRICE_DISCOUNT))),
            "K",new Item("K", 80, List.of(new Offer(2, 150, "K", OfferType.PRICE_DISCOUNT))),
            "L",new Item("L", 90, List.of()),
            "M",new Item("M", 15, List.of()),
            "N",new Item("N", 40, List.of(new Offer(3, 1, "M", OfferType.FREE_ITEM))),
            "M",new Item("O", 10, List.of()),

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
            final var countAfterFreeItems = applyFreeItemsQuantities(countBySku);
            return calculatePrice(countAfterFreeItems);
        } catch (Exception e){
            System.out.println("Invalid Sku list");
            return -1;
        }
    }

    private int calculatePrice(Map<String, Long> countBySku) {
        return items
                .entrySet()
                .stream()
                .mapToInt(e -> calculatePriceBasedOn(e.getValue(),
                        countBySku.getOrDefault(e.getKey(), 0L).intValue()))
                .sum();
    }

    private Map<String, Long> applyFreeItemsQuantities(final Map<String, Long> countBySku){
       return countBySku
                .entrySet()
                .stream()
                .map(e->{
                    final var quantityToDecrement = findQuantityToDecrement(e.getKey(), countBySku);
                    return Map.entry(e.getKey(), e.getValue() -quantityToDecrement);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Integer findQuantityToDecrement(final String key, final Map<String, Long> countBySku) {
        return items.entrySet()
                .stream()
                .mapToInt(m->m.getValue()
                        .offers()
                        .stream()
                        .filter(offer -> offer.offerType() == OfferType.FREE_ITEM)
                        .filter(offer -> offer.sku().equals(key))
                        .mapToInt(offer-> (countBySku.getOrDefault(m.getKey(), 0L).intValue() / offer.triggerQuantity()) * offer.unit())
                        .sum())
                .sum();
    }

    private static Integer calculatePriceBasedOn(final Item item, final Integer quantity){
        if(item.offers.isEmpty()){
            return quantity * item.price();
        }

        final var leftQuantity = new AtomicInteger(quantity);
        final var priceWithDiscounts = item
                .offers()
                .stream()
                .filter(offer -> OfferType.PRICE_DISCOUNT == offer.offerType())
                .sorted(Comparator.comparing(Offer::triggerQuantity, Comparator.reverseOrder()))
                .mapToInt(offer -> {
                    if(leftQuantity.get() / offer.triggerQuantity() >=1){
                        final var remainingQuantity = leftQuantity.get() % offer.triggerQuantity();
                        return (leftQuantity.getAndSet(remainingQuantity) / offer.triggerQuantity()) * offer.unit();
                    }
                    return 0;
                })
                .sum();
        final var priceWithoutDiscounts = leftQuantity.get() * item.price();
        return priceWithDiscounts + priceWithoutDiscounts;
    }

    private Map<String, Long> countBy(final String skus){
        return Stream.of(skus.split(""))
                .map(items::get)
                .map(item -> Optional.ofNullable(item).orElseThrow())
                .collect(Collectors.groupingBy(Item::sku, Collectors.counting()));
    }
}
