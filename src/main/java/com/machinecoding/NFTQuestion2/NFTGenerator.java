package com.machinecoding.NFTQuestion2;

import java.util.*;

public class NFTGenerator {

    // Stage 1: All possible combinations (no constraint)
    public static List<Map<String, String>> generateAllCombinations(Map<String, List<String>> attributes) {
        List<Map<String, String>> result = new ArrayList<>();
        backtrackAll(attributes, new ArrayList<>(attributes.keySet()), 0, new HashMap<>(), result);
        return result;
    }

    private static void backtrackAll(Map<String, List<String>> attributes, List<String> keys,
                                     int index, Map<String, String> current, List<Map<String, String>> result) {
        if (index == keys.size()) {
            result.add(new HashMap<>(current));
            return;
        }

        String key = keys.get(index);
        for (String val : attributes.get(key)) {
            current.put(key, val);
            backtrackAll(attributes, keys, index + 1, current, result);
            current.remove(key);
        }
    }

    // Stage 2: Constraint-aware with correct usage limit enforcement
    public static List<Map<String, String>> generateConstrainedCombinations(Map<String, Map<String, Integer>> attributeLimits) {
        // Create tracking map for each attribute value's usage
        Map<String, Integer> attributeUsage = new HashMap<>();

        // Create a copy of the available limits that we'll decrease as we go
        Map<String, Map<String, Integer>> remainingLimits = new HashMap<>();
        for(Map.Entry<String, Map<String, Integer>> entry : attributeLimits.entrySet()) {
            String key = entry.getKey();
            Map<String, Integer> limits = new HashMap<>(entry.getValue());
            remainingLimits.put(key, limits);
        }
        List<Map<String, String>> result = new ArrayList<>();

        // Find all possible attribute combinations
        Map<String, List<String>> allAttributes = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : attributeLimits.entrySet()) {
            allAttributes.put(entry.getKey(), new ArrayList<>(entry.getValue().keySet()));
        }

        // Generate valid NFTs
        List<Map<String, String>> allPossible = generateAllCombinations(allAttributes);

        // Apply constraints
        for (Map<String, String> nft : allPossible) {
            boolean valid = true;

            // Check if this NFT would exceed any attribute limits
            for (Map.Entry<String, String> attribute : nft.entrySet()) {
                String key = attribute.getKey();
                String value = attribute.getValue();

                int remaining = remainingLimits.get(key).getOrDefault(value, 0);
                if (remaining <= 0) {
                    valid = false;
                    break;
                }
            }

            // If valid, add NFT and decrease available limits
            if (valid) {
                result.add(nft);

                // Update remaining limits
                for (Map.Entry<String, String> attribute : nft.entrySet()) {
                    String key = attribute.getKey();
                    String value = attribute.getValue();

                    int current = remainingLimits.get(key).get(value);
                    remainingLimits.get(key).put(value, current - 1);
                }
            }
        }

        return result;
    }

    // Optional: Limit number of generated NFTs
    public static List<Map<String, String>> generateConstrainedCombinations(Map<String, Map<String, Integer>> limits, int maxNFTs) {
        List<Map<String, String>> allConstrained = generateConstrainedCombinations(limits);

        // Return only up to maxNFTs
        if (allConstrained.size() <= maxNFTs) {
            return allConstrained;
        } else {
            return allConstrained.subList(0, maxNFTs);
        }
    }
}

class Main {
    public static void main(String[] args) {
        // Stage 1 - Full combinations
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("Background", List.of("Yellow", "Blue"));
        attributes.put("Eyes", List.of("Red", "Green"));
        attributes.put("Hat", List.of("Beanie", "Cap"));

        System.out.println("🔁 All Combinations:");
        NFTGenerator.generateAllCombinations(attributes).forEach(System.out::println);

        // Stage 2 - With constraints
        Map<String, Map<String, Integer>> limits = new HashMap<>();
        limits.put("Background", Map.of("Yellow", 2, "Blue", 1));
        limits.put("Eyes", Map.of("Red", 1, "Green", 2));
        limits.put("Hat", Map.of("Beanie", 1, "Cap", 2));

        System.out.println("\n🛑 Constrained Combinations:");
        NFTGenerator.generateConstrainedCombinations(limits).forEach(System.out::println);

        // Stage 3 - With limits on total NFTs
        System.out.println("\n🎯 Bounded Constrained (max 3 NFTs):");
        NFTGenerator.generateConstrainedCombinations(limits, 3).forEach(System.out::println);
    }
}