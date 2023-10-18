package befaster.solutions.CHK;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CheckoutSolution {

    enum OfferType{
        PRICE_DISCOUNT, FREE_ITEM, GROUP_DISCOUNT
    }

    record Offer(Integer triggerQuantity, Integer unit, String sku, OfferType offerType){}
    record Item(String sku, Integer price, List<Offer> offers){}

    private final Offer groupOfferOne = new Offer(3, 45, "GROUP_DISCOUNT_1", OfferType.GROUP_DISCOUNT);

    private final Map<String, Item> items = Map.ofEntries(
            Map.entry("A",new Item("A", 50, List.of(
                    new Offer(3, 130, "A", OfferType.PRICE_DISCOUNT),
                    new Offer(5, 200, "A", OfferType.PRICE_DISCOUNT)))),
            Map.entry("B",new Item("B", 30, List.of(new Offer(2, 45, "B", OfferType.PRICE_DISCOUNT)))),
            Map.entry( "C",new Item("C", 20, List.of())),
            Map.entry("D",new Item("D", 15, List.of())),
            Map.entry("E",new Item("E", 40, List.of(new Offer(2, 1, "B", OfferType.FREE_ITEM)))),
            Map.entry( "F",new Item("F", 10, List.of(new Offer(3, 1, "F", OfferType.FREE_ITEM)))),
            Map.entry( "G",new Item("G", 20, List.of())),
            Map.entry("H",new Item("H", 10, List.of(
                    new Offer(5, 45, "H", OfferType.PRICE_DISCOUNT),
                    new Offer(10, 80, "H", OfferType.PRICE_DISCOUNT)))),
            Map.entry("I",new Item("I", 35, List.of())),
            Map.entry("J",new Item("J", 60, List.of())),
            Map.entry("K",new Item("K", 70, List.of(new Offer(2, 120, "K", OfferType.PRICE_DISCOUNT)))),
            Map.entry("L",new Item("L", 90, List.of())),
            Map.entry("M",new Item("M", 15, List.of())),
            Map.entry("N",new Item("N", 40, List.of(new Offer(3, 1, "M", OfferType.FREE_ITEM)))),
            Map.entry("O",new Item("O", 10, List.of())),
            Map.entry("P",new Item("P", 50, List.of(new Offer(5, 200, "P", OfferType.PRICE_DISCOUNT)))),
            Map.entry("Q",new Item("Q", 30, List.of(new Offer(3, 80, "Q", OfferType.PRICE_DISCOUNT)))),
            Map.entry("R",new Item("R", 50, List.of(new Offer(3, 1, "Q", OfferType.FREE_ITEM)))),
            Map.entry("S",new Item("S", 20, List.of(groupOfferOne))),
            Map.entry("T",new Item("T", 20, List.of(groupOfferOne))),
            Map.entry("U",new Item("U", 40, List.of(new Offer(4, 1, "U", OfferType.FREE_ITEM)))),
            Map.entry("V",new Item("V", 50, List.of(
                    new Offer(2, 90, "V", OfferType.PRICE_DISCOUNT),
                    new Offer(3, 130, "V", OfferType.PRICE_DISCOUNT)))),
            Map.entry( "W",new Item("W", 20, List.of())),
            Map.entry("X",new Item("X", 17, List.of(groupOfferOne))),
            Map.entry("Y",new Item("Y", 20, List.of(groupOfferOne))),
            Map.entry("Z",new Item("Z", 21, List.of(groupOfferOne)))
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

            final var groupDiscountsSkus = items.values()
                    .stream()
                    .filter(i->i.offers().stream().anyMatch(o->o.offerType() == OfferType.GROUP_DISCOUNT))
                    .map(Item::sku)
                    .toList();

            return calculateNonGroupPrice(countAfterFreeItems, groupDiscountsSkus)
                    + calculateGroupPrice(countAfterFreeItems, groupDiscountsSkus);
        } catch (Exception e){
            System.out.println("Invalid Sku list");
            return -1;
        }
    }

    private Integer calculateGroupPrice(final Map<String, Long> countAfterFreeItems, final List<String> groupDiscountsSkus ) {
        record GroupedAmount(Integer counter, Integer amountProcessed, Item item){

        }

        return countAfterFreeItems.entrySet()
                .stream()
                .filter(item-> groupDiscountsSkus.contains(item.getKey()))
                .map(e->Map.entry(items.get(e.getKey()), e.getValue()))
                .sorted(Comparator.comparing(e->e.getKey().price(), Comparator.reverseOrder()))
                .flatMap(e-> IntStream
                        .range(0, e.getValue().intValue())
                        .mapToObj(value -> new GroupedAmount(1, e.getKey().price(), e.getKey())))
                .reduce((i, g)->{
                    final var counter = i.counter() + 1;
                    final var amountProcessed = counter % groupOfferOne.triggerQuantity() == 0 ?
                            groupOfferOne.unit() * (counter / groupOfferOne.triggerQuantity()) :
                            i.amountProcessed() + g.item().price();
                    return new GroupedAmount(counter, amountProcessed, g.item());
                })
                .stream()
                .map(GroupedAmount::amountProcessed)
                .findAny()
                .orElse(0);
    }

    private int calculateNonGroupPrice(Map<String, Long> countBySku, final List<String> groupDiscountsSkus) {
        return items
                .entrySet()
                .stream()
                .filter(item-> !groupDiscountsSkus.contains(item.getKey()))
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




