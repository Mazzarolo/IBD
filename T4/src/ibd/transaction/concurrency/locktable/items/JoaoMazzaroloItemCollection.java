package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;

import java.util.ArrayList;

public class JoaoMazzaroloItemCollection implements ItemCollection {
    private ArrayList<Item> items = new ArrayList<Item>();

    @Override
    public Item addItem(long lower, long higher) {
        Item item = getItem(lower, higher);
        if (item == null) {
            item = new Item(lower, higher);
            items.add(item);
        }

        return item;
    }

    @Override
    public Item getItem(long lower, long higher) {
        for (Item item : items) {
            if (item.getLower() == lower && item.getHigher() == higher) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Iterable<Item> getAllItems() {
        return items;
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        ArrayList<Item> list = new ArrayList<Item>();
        for (Item item : items) {
            if (item.getLower() <= higher && item.getHigher() >= lower) {
                list.add(item);
            }
        }
        return list;
    }
}
