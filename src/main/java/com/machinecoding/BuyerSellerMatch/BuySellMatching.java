package com.machinecoding.BuyerSellerMatch;

import java.util.*;

public class BuySellMatching {

    static class Offer {
        int price;
        int quantity;

        public Offer(int price, int quantity) {
            this.price = price;
            this.quantity = quantity;
        }
    }

    public int getUnmatchedOrders(int[][] orders) {
        final int MOD = 1_000_000_007;

        // Max heap for buy orders (sorted by price descending)
        PriorityQueue<Offer> buyPQ = new PriorityQueue<>((a, b) -> b.price - a.price);

        // Min heap for sell orders (sorted by price ascending)
        PriorityQueue<Offer> sellPQ = new PriorityQueue<>(Comparator.comparingInt(a -> a.price));

        for (int[] order : orders) {
            int price = order[0];
            int quantity = order[1];
            int type = order[2]; // 0 = buy, 1 = sell

            if (type == 0) {
                // Buy order
                while (quantity > 0 && !sellPQ.isEmpty() && sellPQ.peek().price <= price) {
                    Offer sell = sellPQ.poll();
                    int matched = Math.min(quantity, sell.quantity);
                    quantity -= matched;
                    sell.quantity -= matched;
                    if (sell.quantity > 0) {
                        sellPQ.offer(sell); // put back the remaining quantity in case of a no match the whole order is put back
                    }
                }
                if (quantity > 0) {
                    buyPQ.offer(new Offer(price, quantity));
                }
            } else {
                // Sell order
                while (quantity > 0 && !buyPQ.isEmpty() && buyPQ.peek().price >= price) {
                    Offer buy = buyPQ.poll();
                    int matched = Math.min(quantity, buy.quantity);
                    quantity -= matched;
                    buy.quantity -= matched;
                    if (buy.quantity > 0) {
                        buyPQ.offer(buy);
                    }
                }
                if (quantity > 0) {
                    sellPQ.offer(new Offer(price, quantity));
                }
            }
        }

        // Count remaining unmatched orders
        long total = 0;
        for (Offer offer : buyPQ) total = (total + offer.quantity) % MOD;
        for (Offer offer : sellPQ) total = (total + offer.quantity) % MOD;

        return (int) total;
    }

    // Test the logic
    public static void main(String[] args) {
        BuySellMatching market = new BuySellMatching();

        int[][] orders = {
                {10, 5, 0}, // buy 5 at 10
                {15, 2, 1}, // sell 2 at 15 -> not matched
                {25, 1, 1}, // sell 1 at 25 -> not matched
                {30, 4, 0}, // buy 4 at 30 -> matches 1 at 25 and 2 at 15
        };

        int result = market.getUnmatchedOrders(orders);
        System.out.println("Unmatched orders: " + result);
    }
}
