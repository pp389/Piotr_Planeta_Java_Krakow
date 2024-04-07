package com.ocado.basket;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasketSplitter {

    //this map contains data about all products delivery methods from config.json file
    private LinkedHashMap<String, List<String>> configMap;

    /**
     * Reads data from config.json file and converts it to a map (keys - product names, values - methods of delivery)
     * @param absolutePathToConfigFile - path to config.json file
     * @return map which contains data from config.json file
     * */
    private Map<String, List<String>> convertConfigToMap(String absolutePathToConfigFile) throws IOException {
        var mapper = new ObjectMapper();
        return mapper.readValue(new File(absolutePathToConfigFile), new TypeReference<Map<String, List<String>>>() {});
    }

    /**
     * Calculates how many products from basket can be transported by each delivery method.
     * @param productsWithTheirDeliveryMethods - linked hash map which contains products with their delivery methods (keys - products, values - delivery methods)
     * @return linked hash map which contains number of products which can be transported by each delivery method,
     * key - delivery method, value - number of products in basket that can be transported by specific delivery method (map is sorted descending by values)
     * */
    private LinkedHashMap<String, Integer> countPossibleProductDeliveryMethods(LinkedHashMap<String, List<String>> productsWithTheirDeliveryMethods) {
        //will contain delivery methods with number of their occurrences
        var deliveryMethodsOccurrences = new HashMap<String, Integer>();

        //for each products' delivery method list
        for (var deliveryMethodsCollection : productsWithTheirDeliveryMethods.values()) {
            //for each delivery method in the list
            for(var deliveryMethod : deliveryMethodsCollection) {
                int value = deliveryMethodsOccurrences.get(deliveryMethod) == null ? 0 : deliveryMethodsOccurrences.get(deliveryMethod);
                deliveryMethodsOccurrences.put(deliveryMethod, value + 1);
            }
        }

        //sort descending by values (by number of occurrences of each delivery method)
        LinkedHashMap<String, Integer> sortedMap = deliveryMethodsOccurrences.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        //System.out.println(sortedMap);
        return sortedMap;
    }

    public BasketSplitter(String absolutePathToConfigFile) {
        try {
            configMap = new LinkedHashMap<>(convertConfigToMap(absolutePathToConfigFile));
            //System.out.println(map);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds to delivery group all products that can be transported by it.
     * @param groups linked hash map which contains delivery groups with associated products.
     * @param group name of the delivery group to which products will be added
     * @param products linked hash map which contains products from basket and their delivery methods
     * */
    private void addProductsToSpecifiedDeliveryGroup(LinkedHashMap<String, List<String>> groups, String group, LinkedHashMap<String, List<String>> products) {
        var iterator = products.entrySet().iterator();
        while(iterator.hasNext()) {
            var product = iterator.next();
            //if product can be transported by specified delivery method
            if(product.getValue().contains(group)) {
                //add product to group
                groups.computeIfAbsent(group, k -> new ArrayList<>()).add(product.getKey());
                //remove product from basket
                iterator.remove();
            }
        }
    }

    /**
     * Splits products from basket to delivery groups.
     * @param items list which contains all items from basket
     * */
    public Map<String, List<String>> split(List<String> items) {
        //will contain products from basket associated with all their delivery methods
        var prodFromBasketWithDeliveryMethods = new LinkedHashMap<String, List<String>>();

        for(String item : items) {
            var itemInConfig = configMap.get(item);
            prodFromBasketWithDeliveryMethods.put(item, itemInConfig);
        }

        //remove products which were not present in config.json file
        prodFromBasketWithDeliveryMethods.values().removeIf(Objects::isNull);

        var groups = new LinkedHashMap<String, List<String>>();

        //while there are still products in the basket to be added to groups
        while(!prodFromBasketWithDeliveryMethods.isEmpty()) {
            //calculate how many products from basket can be transported by each delivery method (map is sorted descending by each method's number of occurrences)
            var deliveryMethodsOccurencesCountMap = countPossibleProductDeliveryMethods(prodFromBasketWithDeliveryMethods);
            //add to delivery group with the highest number of occurrences all products that can be transported by this method
            //(since deliveryMethodsOccurencesCountMap is sorted descending, first entry contains group with highest number of occurrences)
            addProductsToSpecifiedDeliveryGroup(groups, deliveryMethodsOccurencesCountMap.entrySet().stream().findFirst().get().getKey(), prodFromBasketWithDeliveryMethods);
            //remove group with the highest number of occurrences from map, because all products which could be transported by this method were added to group
            deliveryMethodsOccurencesCountMap.remove(deliveryMethodsOccurencesCountMap.entrySet().stream().findFirst().get().getKey());
        }

        return groups;
    }
}