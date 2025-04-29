package com.machinecoding.NFTQuestion;

import java.util.*;

class NFTGenerator {

    private Random random = new Random();

    public List<Map<String, String>> generateRandomCharacters(Map<String, List<String>> properties, int n) {
        List<Map<String, String>> result = new ArrayList<>();

        for(int i = 0; i < n; i++){
            Map<String, String> map = new HashMap<>();
            for(Map.Entry<String, List<String>> entry: properties.entrySet()){
                String key = entry.getKey();
                List<String> values = entry.getValue();
                String randomValue = values.get(random.nextInt(values.size()));
                map.put(key, randomValue);
            }
            result.add(map);
        }
        return result;
    }

    public List<Map<String, String>> generateUniqueRandomCharacters(Map<String, List<String>> properties, int n) {
        Set<String> uniqueCharacters = new HashSet<>();
        List<Map<String, String>> result = new ArrayList<>();

        int totoalPossible = 1;

        for(Map.Entry<String, List<String>> entry: properties.entrySet()){
            int totalValues = entry.getValue().size();
            totoalPossible = totoalPossible * totalValues;
        }

        if(totoalPossible < n){
            System.out.println("Not enough characters");
            throw new IllegalArgumentException("Can not generate more that " + n + " characters");
        }


        while(uniqueCharacters.size() < n){
            Map<String, String> character = new HashMap<>();
            for(Map.Entry<String, List<String>> entrySet: properties.entrySet()){
                String key = entrySet.getKey();
                List<String> values = entrySet.getValue();
                String randomValue = values.get(random.nextInt(values.size()));
                character.put(key, randomValue);
            }
            if(!uniqueCharacters.contains(character.toString())){
                result.add(character);
                uniqueCharacters.add(character.toString());
            }
        }

        return result;
    }

    public List<Map<String, String>> generateUniqueCharactersWithRarity(Map<String, Map<String, String>> propertiesWithRarity, int n) {
        Set<String> uniqueCharacters = new HashSet<>();
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, List<String>> weightedProperties = new HashMap<>();

        for (Map.Entry<String, Map<String, String>> entry : propertiesWithRarity.entrySet()) {
            String property = entry.getKey();
            Map<String, String> valuesWithRarity = entry.getValue();
            List<String> weightedList = new ArrayList<>();

            for (Map.Entry<String, String> valueEntry : valuesWithRarity.entrySet()) {
                String value = valueEntry.getKey();
                String rarity = valueEntry.getValue();

                int weight = rarity.equalsIgnoreCase("common") ? 10 : 1;
                for (int i = 0; i < weight; i++) {
                    weightedList.add(value);
                }
            }
            weightedProperties.put(property, weightedList);
        }

        return generateUniqueRandomCharacters(weightedProperties, n);
    }
}


class NFTDemo {
    public static void main(String[] args) {
        NFTGenerator generator = new NFTGenerator();

        Map<String, List<String>> properties = new HashMap<>();
        properties.put("Color", Arrays.asList("Red", "Blue", "Green"));
        properties.put("Size", Arrays.asList("Small", "Medium", "Large"));
        properties.put("Pattern", Arrays.asList("Striped", "Plain", "PolkaDot"));


        Map<String, Map<String, String>> rareProps = new HashMap<>();

        Map<String, String> colorRarity = new HashMap<>();
        colorRarity.put("Red", "common");
        colorRarity.put("Blue", "rare");
        colorRarity.put("Green", "rare");
        rareProps.put("Color", colorRarity);

        Map<String, String> sizeRarity = new HashMap<>();
        sizeRarity.put("Small", "common");
        sizeRarity.put("Large", "rare");
        sizeRarity.put("Striped", "rare");
        rareProps.put("Size", sizeRarity);

        Map<String, String> patternRarity = new HashMap<>();
        patternRarity.put("Striped", "rare");
        patternRarity.put("Plain", "rare");
        patternRarity.put("PolkaDot", "rare");
        rareProps.put("Pattern", patternRarity);


        List<Map<String, String>> characters = generator.generateRandomCharacters(properties, 10);
        for (Map<String, String> character : characters) {
            System.out.println(character);
        }

        List<Map<String, String>> uniqueCharacters = generator.generateUniqueRandomCharacters(properties, 10);
        System.out.println(" Unique Characters: " + uniqueCharacters.size());
        uniqueCharacters.forEach(System.out::println);


        List<Map<String, String>> rareCharacters = generator.generateUniqueCharactersWithRarity(rareProps, 9);
        System.out.println("\nRarity Characters:");
        for (Map<String, String> character : rareCharacters) {
            System.out.println(character);
        }
    }

}



