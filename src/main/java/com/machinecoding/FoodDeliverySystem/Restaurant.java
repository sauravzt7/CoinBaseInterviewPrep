package com.machinecoding.FoodDeliverySystem;

import java.util.*;



// Core domain classes
class MenuItem {
    String name;
    double price;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

}

class Restaurant {
    String id;
    String name;
    List<MenuItem> menu;
    int estimatedDeliveryTime; // in minutes

    public Restaurant(String id, String name, List<MenuItem> menu, int estimatedDeliveryTime) {
        this.id = id;
        this.name = name;
        this.menu = menu;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public boolean hasItem(String itemName) {
        return menu.stream().anyMatch(item -> item.name.equalsIgnoreCase(itemName));
    }

    public double getPriceOfItem(String itemName) {
        return menu.stream().filter(item -> item.name.equalsIgnoreCase(itemName))
                .findFirst().get().price;
    }
}

// Enum for filter type
enum FilterType {
    PRICE,
    DELIVERY_TIME
}

interface FilterStrategy {
    Restaurant filter(List<Restaurant> restaurants, String itemName);
}


class PriceFilterStrategy implements FilterStrategy {

    double price;
    public PriceFilterStrategy(double price) {
        this.price = price;
    }
    @Override
    public Restaurant filter(List<Restaurant> restaurants, String itemName) {
        return restaurants.stream()
                .filter(restaurant -> restaurant.hasItem(itemName))
                .min(Comparator.comparingDouble(restaurant -> restaurant.getPriceOfItem(itemName)))
                .orElse(null);
    }
}

class DeliveryTimeFilterStrategy implements FilterStrategy {
    @Override
    public Restaurant filter(List<Restaurant> restaurants, String itemName) {
        return restaurants.stream()
                .filter(restaurant -> restaurant.hasItem(itemName))
                .min(Comparator.comparingInt(restaurant -> restaurant.estimatedDeliveryTime))
                .orElse(null);
    }
}

// Service layer
class RestaurantService {
    List<Restaurant> restaurants;


    public RestaurantService(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public Restaurant getOptimalRestaurantForItem(String itemName, FilterType filterType) {

        return switch(filterType) {

            case PRICE -> restaurants.stream()
                    .filter(restaurant -> restaurant.hasItem(itemName))
                    .min(Comparator.comparingDouble(restaurant -> restaurant.getPriceOfItem(itemName)))
                    .orElse(null);
            case DELIVERY_TIME -> restaurants.stream()
                    .filter(restaurant     -> restaurant.hasItem(itemName))
                    .min(Comparator.comparingInt(restaurant -> restaurant.estimatedDeliveryTime))
                    .orElse(null);
        };


    }

    public List<Restaurant> getAllRestaurants() {
        return restaurants;
    }
}

// Sample usage
class Main {
    public static void main(String[] args) {
        List<MenuItem> menu1 = List.of(new MenuItem("Burger", 5.99), new MenuItem("Pizza", 7.99));
        List<MenuItem> menu2 = List.of(new MenuItem("Burger", 4.99), new MenuItem("Fries", 2.99));
        List<MenuItem> menu3 = List.of(new MenuItem("Pizza", 6.49), new MenuItem("Fries", 3.49));

        Restaurant r1 = new Restaurant("r1", "Burger House", menu1, 30);
        Restaurant r2 = new Restaurant("r2", "Quick Bites", menu2, 20);
        Restaurant r3 = new Restaurant("r3", "Pizza Corner", menu3, 25);

        RestaurantService service = new RestaurantService(List.of(r1, r2, r3));

        Restaurant bestPrice = service.getOptimalRestaurantForItem("Burger", FilterType.PRICE);
        Restaurant fastest = service.getOptimalRestaurantForItem("Burger", FilterType.DELIVERY_TIME);

        System.out.println("Best Price for Burger: " + (bestPrice != null ? bestPrice.name : "None"));
        System.out.println("Fastest Delivery for Burger: " + (fastest != null ? fastest.name : "None"));
    }
}
