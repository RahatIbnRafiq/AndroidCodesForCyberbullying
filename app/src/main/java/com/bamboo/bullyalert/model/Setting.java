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
public class Setting {

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
        ITEM_MAP.put(item.name, item);
    }

    private static Item createItem(int position) {
        Item item;
        switch (position) {
            case 1:
                item = new Item("Alert me: ", "Bullying", false, "Comment", false, null);
                break;
            case 2:
                item = new Item("Send to: ", "Text", false, "Email", false, null);
                break;
            case 3:
                item = new Item("Email: ", null, false, null, false, "foo@example.com");
                break;
            case 4:
                item = new Item("Phone: ", null, false, null, false, "1234567890");
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
        public final String name;
        public final String optionOneTitle;
        public final boolean optionOne;
        public final String optionTwoTitle;
        public final boolean optionTwo;
        public final String info;

        public Item(String name, String optionOneTitle, boolean optionOne,
                    String optionTwoTitle, boolean optionTwo, String info) {
            this.name = name;
            this.optionOneTitle = optionOneTitle;
            this.optionOne = optionOne;
            this.optionTwoTitle = optionTwoTitle;
            this.optionTwo = optionTwo;
            this.info = info;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
