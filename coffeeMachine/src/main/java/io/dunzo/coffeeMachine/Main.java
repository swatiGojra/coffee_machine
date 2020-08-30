package io.dunzo.coffeeMachine;

import io.dunzo.coffeeMachine.request.CreateCoffeeMachineRequest;
import io.dunzo.coffeeMachine.service.CoffeeService;
import io.dunzo.coffeeMachine.service.impl.CoffeeServiceImpl;
import io.dunzo.coffeeMachine.utils.FileUtil;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

public class Main
{
	public static void main(String[] args) throws Exception {


		String fileName = "src/main/resources/input1.txt";
		CoffeeService instance = new CoffeeServiceImpl();
		CreateCoffeeMachineRequest coffeeMachineRequest = FileUtil
				.readFile(fileName, CreateCoffeeMachineRequest.class);
		instance.createCoffeeMachine(coffeeMachineRequest);

		List<String> requests = Arrays.asList("espresso", "hot_water", "espresso", "hot_milk", "espresso");
		instance.serveRequests(requests);

		//wait for all the threads to get executed
		Thread.sleep(10000);

		//then refill to 1000 units
		//instance.refillIngredient("chocolate_syrup", 1000);

		//now it can be fulfilled
		//List<String> requests2 = Arrays.asList("mocha");
		//instance.serveRequests(requests2);


	}
}
