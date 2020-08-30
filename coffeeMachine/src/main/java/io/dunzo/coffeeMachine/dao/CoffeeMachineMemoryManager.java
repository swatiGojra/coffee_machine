/**
 * 
 */
package io.dunzo.coffeeMachine.dao;

import io.dunzo.coffeeMachine.exceptions.BeverageNotSupportedException;
import io.dunzo.coffeeMachine.model.Ingredient;

import java.util.List;
import java.util.Map;

/** Interface for Memory Management System
 * @author swatigojra
 * @param <T>
 */
public interface CoffeeMachineMemoryManager<T extends Ingredient>
{
	Map<Ingredient, Integer> getRequirementsForABeverage(String beverage);

	Integer getInventoryForAIngredient(T ingredient);

	String getBeverageDisplayName(String beverage) throws BeverageNotSupportedException;

	void removeIngredientQuantity(T ingredient, Integer removedValue);

	void addIngredientQuantity(T ingredient, Integer removedValue);

	List<String> getBeveragesDisplayNameList();

	Integer getOutlets();

	List<String> getLowIngredients();

	void doCleanup();
}
