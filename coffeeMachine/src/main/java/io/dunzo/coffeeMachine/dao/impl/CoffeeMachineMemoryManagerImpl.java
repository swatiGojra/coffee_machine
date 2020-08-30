/**
 * 
 */
package io.dunzo.coffeeMachine.dao.impl;

import io.dunzo.coffeeMachine.dao.CoffeeMachineMemoryManager;
import io.dunzo.coffeeMachine.exceptions.BeverageNotSupportedException;
import io.dunzo.coffeeMachine.model.Ingredient;
import io.dunzo.coffeeMachine.utils.Constants;
import io.dunzo.coffeeMachine.utils.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is a singleton class to manage the data of coffee machine system
 * 
 * @author swatigojra
 *
 */

public class CoffeeMachineMemoryManagerImpl<T extends Ingredient> implements
		CoffeeMachineMemoryManager<T>
{

	//Singleton class instance
	@SuppressWarnings("rawtypes")
	private static CoffeeMachineMemoryManagerImpl instance = null;

	//Contains beverage list vs requirements map (Quantity for each ingredient)
	private Map<String, Map<Ingredient, Integer>> requirementSpecificationMap;

	//Contains Quantity of each ingredient
	private Map<Ingredient, Integer> beverageInventoryMap;

	//Contains mapping of system name to user display name
	private Map<String, String> beverageNameVsDisplayNameMap;

	//The total no of outlets in the system
	private Integer outlets;


	/**
	 * Constructor
	 * @param outlets
	 * @param requirementSpecificationMap
	 * @param beverageNameVsDisplayNameMap
	 * @param beverageInventoryMap
	 * @param <T>
	 * @return
	 */
	public static <T extends Ingredient> CoffeeMachineMemoryManagerImpl<T> getInstance(Integer outlets, Map<String, Map<Ingredient, Integer>> requirementSpecificationMap,Map<String, String> beverageNameVsDisplayNameMap, Map<Ingredient, Integer> beverageInventoryMap) {

		if (instance == null)
		{
			synchronized (CoffeeMachineMemoryManagerImpl.class)
			{
				if (instance == null)
				{
					instance = new CoffeeMachineMemoryManagerImpl<T>(outlets, requirementSpecificationMap, beverageNameVsDisplayNameMap, beverageInventoryMap);
				}
			}
		}

		return instance;
	}

	/**
	 * Parameterized Constructor
	 * @param outlets
	 * @param requirementSpecificationMap
	 * @param beverageNameVsDisplayNameMap
	 * @param beverageInventoryMap
	 */
	private CoffeeMachineMemoryManagerImpl(Integer outlets, Map<String, Map<Ingredient, Integer>> requirementSpecificationMap,
																				 Map<String, String> beverageNameVsDisplayNameMap,  Map<Ingredient, Integer> beverageInventoryMap) {
			this.beverageNameVsDisplayNameMap = beverageNameVsDisplayNameMap;
			this.outlets = outlets;
			this.requirementSpecificationMap = requirementSpecificationMap;
			this.beverageInventoryMap = beverageInventoryMap;
	}

	/**
	 * List of requirement for a beverage
	 * @param beverage
	 * @return
	 */
	@Override
	public Map<Ingredient, Integer> getRequirementsForABeverage(String beverage){
		return requirementSpecificationMap.get(beverage);
	}

	/**
	 * Current Inventory for a Ingredient
	 * @param ingredient
	 * @return
	 */
	@Override
	public Integer getInventoryForAIngredient(Ingredient ingredient){
		return beverageInventoryMap.get(ingredient);
	}

	/**
	 * Get user display name from a system name
	 * @param beverage
	 * @return
	 */
	@Override
	public String getBeverageDisplayName(String beverage) throws BeverageNotSupportedException {
		if(!beverageNameVsDisplayNameMap.containsKey(beverage)){
			throw new BeverageNotSupportedException(String.format(Messages.NOT_SUPPORTED, beverage));
		}
		return beverageNameVsDisplayNameMap.get(beverage);
	}

	/**
	 * To Remove an Ingredient Quantity
	 * @param ingredient
	 * @param removedValue
	 */
	@Override
	public void removeIngredientQuantity(Ingredient ingredient, Integer removedValue){
		Integer newValue = getInventoryForAIngredient(ingredient) - removedValue;
		beverageInventoryMap.put(ingredient, newValue);
		if(newValue<Constants.CRITICAL_LIMIT){
			System.out.println(String.format(Messages.INGREDIENT_RUNNING_LOW, ingredient.getDisplayName()));
		}
	}

	/**
	 * To Add an Ingredient Quantity
	 * @param ingredient
	 * @param addedValue
	 */
	public void addIngredientQuantity(Ingredient ingredient, Integer addedValue){
		beverageInventoryMap.put(ingredient, getInventoryForAIngredient(ingredient) + addedValue);
	}

	/**
	 * Outlets of the system
	 * @return
	 */
	public Integer getOutlets(){
		return this.outlets;
	}

	/*
		return menu
	 */
	public List<String> getBeveragesDisplayNameList(){
		List<String> beveragesNamesList = new ArrayList<>();
		for(Map.Entry<String, String> entry : beverageNameVsDisplayNameMap.entrySet()){
			beveragesNamesList.add(entry.getValue());
		}
		return beveragesNamesList;
	}


	public List<String> getLowIngredients(){
		List<String> ingredientsNames = new ArrayList<>();
		for(Map.Entry<Ingredient, Integer> entry : beverageInventoryMap.entrySet()){
			if(entry.getValue()<Constants.CRITICAL_LIMIT) {
				ingredientsNames.add(entry.getKey().getDisplayName());
			}
		}
		return ingredientsNames;
	}

	/**
	 * Cleanup operation
	 */
	@Override
	public void doCleanup() {
			this.requirementSpecificationMap = null;
			this.beverageInventoryMap = null;
			this.beverageNameVsDisplayNameMap = null;
			this.outlets = null;
			instance = null;
	}

	/**
	 * To prevent singleton property
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
}
