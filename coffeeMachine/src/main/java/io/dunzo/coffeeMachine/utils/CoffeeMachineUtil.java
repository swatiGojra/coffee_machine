package io.dunzo.coffeeMachine.utils;

import io.dunzo.coffeeMachine.exceptions.IngredientNotSupportedException;
import io.dunzo.coffeeMachine.model.Ingredient;
import io.dunzo.coffeeMachine.request.Beverage;
import io.dunzo.coffeeMachine.request.IngredientRequirement;
import io.dunzo.coffeeMachine.service.impl.IngredientFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author swatigojra
 *
 * Utility Class having functions like convert from request to dao objects, will be used by all the common service implemntations of the Coffee Service
 *
 */
public class CoffeeMachineUtil
{
  public static void populateDataMaps(List<Beverage> beverageList,
                                      Map<String, Map<Ingredient, Integer>> requirementSpecificationMap,
                                      Map<String, String> beverageNameVsDisplayNameMap) throws
      IngredientNotSupportedException {

    if(CollectionUtils.isEmpty(beverageList)){
      return;
    }

    for(Beverage beverage : beverageList){
      beverageNameVsDisplayNameMap.put(beverage.getName(), beverage.getDisplayName());

      List<IngredientRequirement> ingredientRequirements = beverage.getIngredientRequirementList();
      Map<Ingredient, Integer> ingredientQuantityMap = new HashMap<>();

      for(IngredientRequirement ingredientRequirement : ingredientRequirements){
        Ingredient ingredient = IngredientFactory.getIngredientType(ingredientRequirement.getType());
        ingredientQuantityMap.put(ingredient, ingredientRequirement.getQuantity());
      }

      requirementSpecificationMap.put(beverage.getName(), ingredientQuantityMap);
    }

  }

  public static void populateBeverageInventoryMap(
      List<IngredientRequirement> totalItemsQuantityList, Map<Ingredient, Integer> beverageInventoryMap) throws IngredientNotSupportedException{

    for(IngredientRequirement ingredientRequirement : totalItemsQuantityList){
      Ingredient ingredient = IngredientFactory.getIngredientType(ingredientRequirement.getType());
      beverageInventoryMap.put(ingredient, ingredientRequirement.getQuantity());
    }



  }
}
