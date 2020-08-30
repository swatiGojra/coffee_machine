package io.dunzo.coffeeMachine.service;

import io.dunzo.coffeeMachine.exceptions.CoffeeMachineException;
import io.dunzo.coffeeMachine.exceptions.IngredientNotSupportedException;
import io.dunzo.coffeeMachine.request.CreateCoffeeMachineRequest;

import java.util.List;

/**
 * Interface defining all the supported actions by Coffee System
 *
 *  @author swatigojra
 *
 */
public interface CoffeeService
{
	/* ---- Actions ----- */
	void createCoffeeMachine(CreateCoffeeMachineRequest createCoffeeMachineRequest)
      throws CoffeeMachineException, IngredientNotSupportedException;

	void serveRequests(List<String> beverages);

	void refillIngredient(String ingredientName, Integer refillValue) throws IngredientNotSupportedException;

	void displayMenu();

	List<String> getLowIngredients();

	void doCleanup();

}
