package io.dunzo.coffeeMachine.service.impl;

import io.dunzo.coffeeMachine.exceptions.IngredientNotSupportedException;
import io.dunzo.coffeeMachine.model.*;
import io.dunzo.coffeeMachine.utils.Messages;

/**
 * Factory Class to get a Ingredient Object from user defined type
 *
 * @author swatigojra
 */

public class IngredientFactory
{
	public static Ingredient getIngredientType(String type) throws IngredientNotSupportedException{

		switch (type) {
			case "hot_water":
				return new HotWater();
			case "hot_milk":
				return new HotMilk();
			case "chocolate_syrup":
				return new ChocolateSyrup();
			case "coffee_syrup":
				return new CoffeeSyrup();
			case "sugar_syrup":
				return new SugarSyrup();
		}

		throw new IngredientNotSupportedException(String.format(Messages.NOT_SUPPORTED, type));
	}
}
