package io.dunzo.coffeeMachine.service.impl;

import io.dunzo.coffeeMachine.dao.CoffeeMachineMemoryManager;
import io.dunzo.coffeeMachine.dao.impl.CoffeeMachineMemoryManagerImpl;
import io.dunzo.coffeeMachine.exceptions.BeverageNotSupportedException;
import io.dunzo.coffeeMachine.exceptions.CoffeeMachineException;
import io.dunzo.coffeeMachine.exceptions.IngredientNotSupportedException;
import io.dunzo.coffeeMachine.model.Ingredient;
import io.dunzo.coffeeMachine.service.CoffeeService;
import io.dunzo.coffeeMachine.request.CreateCoffeeMachine;
import io.dunzo.coffeeMachine.request.CreateCoffeeMachineRequest;
import io.dunzo.coffeeMachine.response.Response;
import io.dunzo.coffeeMachine.utils.CoffeeMachineUtil;
import io.dunzo.coffeeMachine.utils.Messages;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author swatigojra
 *
 * Implemetaion of Coffee Service, contains the business logic
 *
 */

public class CoffeeServiceImpl implements CoffeeService
{
	//The dataManager instance to access the data
	private CoffeeMachineMemoryManager<Ingredient> dataManager = null;

	//To take a write lock so that no two threads can concurrently change the inventory
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

	//Thread pool manager
	private ExecutorService executorService;

	/**
	 * Create a Coffee Machine
	 * @param createCoffeeMachineRequest
	 * @throws CoffeeMachineException
	 * @throws IngredientNotSupportedException
	 */
	@Override
	public void createCoffeeMachine(CreateCoffeeMachineRequest createCoffeeMachineRequest)
			throws CoffeeMachineException, IngredientNotSupportedException {

		if(dataManager!=null){
			throw new CoffeeMachineException(Messages.ALREADY_EXISTING_MACHINE);
		}

		CreateCoffeeMachine createCoffeeMachine = createCoffeeMachineRequest.getCreateCoffeeMachine();

		Map<String, String> beverageNameVsDisplayNameMap = new HashMap<>();
		Map<String, Map<Ingredient, Integer>> requirementSpecificationMap = new HashMap<>();
		Map<Ingredient, Integer> beverageInventoryMap = new HashMap<>();
		Integer outlets = createCoffeeMachine.getOutlets().getNoOfMachines();

		CoffeeMachineUtil
				.populateDataMaps(createCoffeeMachine.getBeverageList(), requirementSpecificationMap, beverageNameVsDisplayNameMap);

		CoffeeMachineUtil.populateBeverageInventoryMap(createCoffeeMachine.getTotalItemsQuantityList(), beverageInventoryMap);

		this.dataManager = CoffeeMachineMemoryManagerImpl
				.getInstance(outlets, requirementSpecificationMap, beverageNameVsDisplayNameMap, beverageInventoryMap);

		//create thread pool with given outlets
		executorService = Executors.newFixedThreadPool(outlets);

		System.out.println(String.format(Messages.COFFEE_MACHINE_CREATED, outlets));
	}

	/**
	 * Public method to get request list
	 * @param beverages
	 */
	@Override
	public void serveRequests(List<String> beverages){

		for(String beverage : beverages) {
			executorService.execute(()-> {
				try {
					serveSingleRequest(beverage);
				} catch (BeverageNotSupportedException e) {
					//catch block since due to one request other requests should not get failed
						System.out.println(e.getMessage());
				}
			});
		}

	}

	/**
	 * Processing Single Request, Getting Requirements etc..
	 * @param beverage
	 * @throws BeverageNotSupportedException
	 */
	private void serveSingleRequest(String beverage) throws BeverageNotSupportedException {

		String beverageDisplayName = dataManager.getBeverageDisplayName(beverage);

		System.out.println(String.format(Messages.PREPARING, beverageDisplayName));

		// To Simulate Real time scenario
		try {
			Thread.sleep(2000);
		}catch (Exception e){
			String message = String.format(Messages.INTERNAL_ERROR, beverageDisplayName);
			System.out.println(message);
			return;
		}

		Map<Ingredient, Integer> requirements = dataManager.getRequirementsForABeverage(beverage);

		Response response = prepareBeverage(beverageDisplayName, requirements);
		System.out.println(response.getMessage());
	}

	/**
	 * This gets write lock on the database and then updates the inventory after checking the current inventory
	 * @param beverageDisplayName
	 * @param requirements
	 * @return
	 */
	private Response prepareBeverage(String beverageDisplayName, Map<Ingredient, Integer> requirements) {

		lock.writeLock().lock();

		try {
			for (Map.Entry<Ingredient, Integer> entry : requirements.entrySet()) {
				Ingredient ingredient = entry.getKey();
				Integer currentQuantity = dataManager.getInventoryForAIngredient(ingredient);
				Integer requiredQuantity = entry.getValue();
				if (currentQuantity < requiredQuantity) {
					String message = String.format(Messages.INVENTORY_FULL, beverageDisplayName, ingredient.getDisplayName());
					return new Response(false, message);
				}
			}

			for (Map.Entry<Ingredient, Integer> entry : requirements.entrySet()) {
				dataManager.removeIngredientQuantity(entry.getKey(), entry.getValue());
			}

		}catch (Exception e){
			String message = String.format(Messages.INTERNAL_ERROR, beverageDisplayName);
			return new Response(false, message);
		}

		//will be called before return and throwing exception
		finally
		{
			lock.writeLock().unlock();
		}

		String message = String.format(Messages.DRINK_PREPARED, beverageDisplayName);
		return new Response(true, message);
	}


	/**
	 * To Refill a particular Ingredient
	 * @param ingredientName
	 * @param refillValue
	 * @throws IngredientNotSupportedException
	 */
	@Override
	public void refillIngredient(String ingredientName, Integer refillValue) throws IngredientNotSupportedException{

		lock.writeLock().lock();

		Ingredient ingredient = IngredientFactory.getIngredientType(ingredientName);
		try {
			dataManager.addIngredientQuantity(ingredient, refillValue);
		}catch (Exception e){
			String message = String.format(Messages.REFILL_ERROR, ingredient.getDisplayName());
			System.out.println(message);
			return;
		}
		finally
		{
			lock.writeLock().unlock();
		}
		String message = String.format(Messages.REFILL_SUCCESS, ingredient.getDisplayName());
		System.out.println(message);
	}

	@Override
	public void displayMenu(){
		List<String> menus = dataManager.getBeveragesDisplayNameList();
		System.out.println(Messages.DISPLAY_MENU);
		menus.forEach(System.out::println);
	}

	@Override
	public List<String> getLowIngredients(){
		lock.writeLock().lock();
		try{
			return dataManager.getLowIngredients();
		}
		finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Clean Up Method
	 */
	@Override
	public void doCleanup()
	{
		if (dataManager != null) {
			dataManager.doCleanup();
		}
		dataManager=null;
	}
}
