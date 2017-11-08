package com.bamboo.bullyalert.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class HistoryDetail {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Item> ITEMS = new ArrayList<Item>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Item> ITEM_MAP = new HashMap<String, Item>();

    private static final int COUNT = 4;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createItem(i));
        }
    }

    private static void addItem(Item item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.person, item);
    }

    private static Item createItem(int position) {
        Item item;
        switch (position) {
            case 1:
                item = new Item("Person A", "Sample comment 1");
                break;
            case 2:
                item = new Item("Person B", "Sample comment 2");
                break;
            case 3:
                item = new Item("Person C", "Sample comment 3");
                break;
            case 4:
                item = new Item("Person D", "Sample comment 4");
                break;
            default:
                item = null;
                break;
        }
        return item;
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Item {
        public final String person;
        public final String comment;

        public Item(String person, String comment) {
            this.person = person;
            this.comment = comment;
        }

        @Override
        public String toString() {
            return person + ": " +comment;
        }
    }
}
