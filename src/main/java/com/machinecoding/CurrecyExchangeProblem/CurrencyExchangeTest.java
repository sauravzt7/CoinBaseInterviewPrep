package com.machinecoding.CurrecyExchangeProblem;

import java.util.*;


// Edge class to represent currency exchange rate
class Edge{
    String from, to;
    double weight;

    Edge(String from, String to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = -Math.log(weight); // Store -log(rate) for Bellman-Ford as we want to minimize the product
    }
}


// Result class to store the best rate and path and if there's a negative cycle
class Result {
    double bestRate;
    List<String> path;
    boolean hasNegativeCycle;

    Result(double bestRate, List<String> path, boolean hasNegativeCycle) {
        this.bestRate = bestRate;
        this.path = path;
        this.hasNegativeCycle = hasNegativeCycle;
    }
}


interface ICurrencyExchange {
    Result bestExchangeResult(List<Edge> rates, String src, String dest);
}

class CurrencyExchangeBackTracking implements ICurrencyExchange {
    @Override
    public Result bestExchangeResult(List<Edge> rates, String src, String dest) {

        // Step 1: Create a graph from the rates
        Map<String, List<Edge>> graph = new HashMap<>();
        for (Edge rate : rates) {
            graph.computeIfAbsent(rate.from, _ -> new ArrayList<>()).add(rate);
        }

        // Step 2: Perform DFS to find all paths from src to dest
        List<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        double bestRate = dfs(graph, src, dest, visited, path, 1.0);

        // Step 3: If no path exists
        if (bestRate == -1) {
            return new Result(-1, new ArrayList<>(), false);
        }

        return new Result(bestRate, path, false);
    }

    private double dfs(Map<String, List<Edge>> graph, String src, String dest, Set<String> visited, List<String> path, double v) {

        visited.add(src);
        path.add(src);

        if (src.equals(dest)) {
            return v;
        }

        double bestRate = -1;

        for (Edge edge : graph.getOrDefault(src, new ArrayList<>())) {
            if (!visited.contains(edge.to)) {
                double rate = dfs(graph, edge.to, dest, visited, path, v * Math.exp(-edge.weight));
                if (rate > bestRate) {
                    bestRate = rate;
                }
            }
        }

        visited.remove(src);
        path.remove(path.size() - 1);

        return bestRate;
    }
}

class CurrencyExchangeBellmanFord implements ICurrencyExchange {

    @Override
    public Result bestExchangeResult(List<Edge> rates, String src, String dest) {
        Set<String> currencies = new HashSet<>(); // To store unique currencies
        for (Edge e : rates) {
            currencies.add(e.from);
            currencies.add(e.to);
        }

        // Initialize distances and parents
        Map<String, Double> dist = new HashMap<>(); // To store distances
        Map<String, String> parent = new HashMap<>(); // To reconstruct the path

        for (String c : currencies) {
            dist.put(c, Double.POSITIVE_INFINITY);
            parent.put(c, null);
        }
        dist.put(src, 0.0);

        int n = currencies.size();

        // Bellman-Ford algorithm: Relax edges n-1 times
        for (int i = 0; i < n - 1; i++) {
            for(Edge e: rates){
                if(dist.get(e.from) + e.weight < dist.get(e.to)){
                    dist.put(e.to, dist.get(e.from) + e.weight);
                    parent.put(e.from, e.to);
                }
            }
        }

        // Step 1: Check for negative cycle
        boolean hasNegativeCycle = false;
        for (Edge e : rates) {
            if (dist.get(e.from) + e.weight < dist.get(e.to)) {
                hasNegativeCycle = true;
                break;
            }
        }

        // Step 2: If no path exists
        if (dist.get(dest) == Double.POSITIVE_INFINITY) {
            return new Result(-1, new ArrayList<>(), hasNegativeCycle);
        }

        // Step 3: Reconstruct the path from dest -> src
        List<String> path = new LinkedList<>();
        String current = dest;
        while (current != null) {
            path.addFirst(current); // Insert at beginning
            current = parent.get(current);
        }

        // Step 4: Convert -log(rate) back to rate
        double rate = Math.exp(-dist.get(dest));

        return new Result(rate, path, hasNegativeCycle);
    }

}

public class CurrencyExchangeTest {

    public static void main(String[] args) {
        List<Edge> rates = new ArrayList<>();
        rates.add(new Edge("USD", "EUR", 0.9));
        rates.add(new Edge("EUR", "GBP", 0.8));
        rates.add(new Edge("USD", "GBP", 0.65));
        rates.add(new Edge("GBP", "JPY", 140));
        rates.add(new Edge("EUR", "JPY", -130));

        String src = "USD", dest = "JPY";

        ICurrencyExchange currencyExchange = new CurrencyExchangeBellmanFord();
        ICurrencyExchange currencyExchangeBackTracking =  new CurrencyExchangeBackTracking();

        Result result = currencyExchange.bestExchangeResult(rates, src, dest);
        Result resultFromBT = currencyExchangeBackTracking.bestExchangeResult(rates, src, dest);

        System.out.println("Using Bellman-Ford:");


        if (result.bestRate == -1) {
            System.out.println("No conversion path exists!");
        } else {
            System.out.println("Best rate from " + src + " to " + dest + " is: " + result.bestRate);
            System.out.println("Path taken: " + result.path);
        }

        if (result.hasNegativeCycle) {
            System.out.println("Warning: Arbitrage opportunity detected (negative cycle exists)!");
        }


        System.out.println("\nUsing Backtracking:");
        if (resultFromBT.bestRate == -1) {
            System.out.println("No conversion path exists!");
        } else {
            System.out.println("Best rate from " + src + " to " + dest + " is: " + resultFromBT.bestRate);
            System.out.println("Path taken: " + resultFromBT.path);
        }
    }


}
