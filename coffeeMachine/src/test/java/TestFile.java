import io.dunzo.coffeeMachine.exceptions.CoffeeMachineException;
import io.dunzo.coffeeMachine.exceptions.IngredientNotSupportedException;
import io.dunzo.coffeeMachine.request.CreateCoffeeMachineRequest;
import io.dunzo.coffeeMachine.service.CoffeeService;
import io.dunzo.coffeeMachine.service.impl.CoffeeServiceImpl;
import io.dunzo.coffeeMachine.utils.FileUtil;
import io.dunzo.coffeeMachine.utils.Messages;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test Cases
 */
public class TestFile
{
	private final ByteArrayOutputStream	outContent	= new ByteArrayOutputStream();
	private static final String fileName = "src/main/resources/input1.txt";
	private static CreateCoffeeMachineRequest coffeeMachineRequest;

	@Before
	public void init() {
		System.setOut(new PrintStream(outContent));
	}

	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	@After
	public void cleanUp()
	{
		System.setOut(null);
	}

	@BeforeClass
	public static void setInputs() throws FileNotFoundException, MalformedURLException {
		coffeeMachineRequest = FileUtil.readFile(fileName, CreateCoffeeMachineRequest.class);
	}

	@Test
	public void createCoffeeMachine() throws CoffeeMachineException,IngredientNotSupportedException {
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		assertTrue(String.format(Messages.COFFEE_MACHINE_CREATED, 3).equalsIgnoreCase(outContent.toString().replace("\n", "")));
		instance.doCleanup();
	}

	@Test
	public void alreadyExistingCoffeeMachine() throws CoffeeMachineException,IngredientNotSupportedException {
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		thrown.expect(CoffeeMachineException.class);
		thrown.expectMessage(Messages.ALREADY_EXISTING_MACHINE);
		instance.createCoffeeMachine(coffeeMachineRequest);
	}

	@Test
	public void serveParallelRequestsNotMoreThanOutletSize() throws CoffeeMachineException, InterruptedException,IngredientNotSupportedException {
		List<String> requests = Arrays.asList("espresso", "hot_water", "espresso", "hot_milk", "espresso");
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		instance.serveRequests(requests);

		//Applied Sleep so that all the requests are executed
		Thread.sleep(5000);
		String[] output = outContent.toString().split("\n");

		//Since there are only 3 outlets, so only three beverages can be prepared at a time
		//It can be prepared in any order, so I have applied regex matching here
		assertTrue(output[1].matches("(.*)Preparing(.*)"));
		assertTrue(output[2].matches("(.*)Preparing(.*)"));
		assertTrue(output[3].matches("(.*)Preparing(.*)"));

		//Thus, No more requests are taken until any outlet is free, the execution of atleast one will finish until any other request gets picked up
		assertTrue(output[4].matches("(.*)prepared(.*)"));
		//Next, any request can be either picked up, or some previous requests have finished execution, so order can'nt be guaranteed...
	}


	@Test
	public void refillAndIndicator() throws CoffeeMachineException, InterruptedException,
			IngredientNotSupportedException {
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		List<String> requests1 = Arrays.asList("mocha", "mocha");
		instance.serveRequests(requests1);

		//wait for all the threads to get executed
		Thread.sleep(10000);

		//then refill chocolate_syrup to 1000 units
		instance.refillIngredient("chocolate_syrup", 1000);

		//now it can be fulfilled
		List<String> requests2 = Arrays.asList("mocha");
		instance.serveRequests(requests2);

		Thread.sleep(5000);
		String[] output = outContent.toString().split("\n");

		assertTrue(output[1].equalsIgnoreCase("Preparing Mocha"));
		assertTrue(output[2].equalsIgnoreCase("Preparing Mocha"));
		assertTrue(output[3].equalsIgnoreCase("Chocolate Syrup is running low"));
		assertTrue(output[4].equalsIgnoreCase("Mocha successfully prepared"));
		assertTrue(output[5].equalsIgnoreCase("Mocha can't be prepared because Chocolate Syrup is not sufficient"));
		assertTrue(output[6].equalsIgnoreCase("Chocolate Syrup  successfully refilled"));
		assertTrue(output[7].equalsIgnoreCase("Preparing Mocha"));
		assertTrue(output[8].equalsIgnoreCase("Mocha successfully prepared"));
	}


	@Test
	public void displayMenus()
			throws CoffeeMachineException, IngredientNotSupportedException, InterruptedException {
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		instance.displayMenu();
		Thread.sleep(100);
		String[] output = outContent.toString().split("\n");
		assertTrue(output[1].equalsIgnoreCase(Messages.DISPLAY_MENU));
		List<String> actualMenus = Arrays.asList(output[2], output[3], output[4], output[5], output[6]);
		List<String> expectedMenus = Arrays.asList("Hot Milk", "Espresso", "Mocha", "Hot Water", "Double Espresso");
		assertEquals(actualMenus, expectedMenus);
	}

	@Test
	public void invalidBeverage()
			throws CoffeeMachineException, IngredientNotSupportedException, InterruptedException {
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		instance.serveRequests(Arrays.asList("masala_chai"));
		Thread.sleep(100);
		String[] output = outContent.toString().split("\n");
		assertTrue(output[1].equalsIgnoreCase(String.format(Messages.NOT_SUPPORTED, "masala_chai")));
	}


	@Test
	public void refillValidIngredient()
			throws CoffeeMachineException, IngredientNotSupportedException, InterruptedException {
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		instance.refillIngredient("chocolate_syrup", 100);
		Thread.sleep(1000);
		String[] output = outContent.toString().split("\n");
		assertTrue(output[1].equalsIgnoreCase(String.format(Messages.REFILL_SUCCESS, "Chocolate Syrup")));
	}

	@Test
	public void refillInValidIngredient()
			throws CoffeeMachineException, IngredientNotSupportedException, InterruptedException {
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		Thread.sleep(1000);
		thrown.expect(IngredientNotSupportedException.class);
		thrown.expectMessage(String.format(Messages.NOT_SUPPORTED, "salt_syrup"));
		instance.refillIngredient("salt_syrup", 100);
	}

	@Test
	public void lowRunningIngredients()
			throws CoffeeMachineException, IngredientNotSupportedException, InterruptedException {
		CoffeeService instance = new CoffeeServiceImpl();
		instance.createCoffeeMachine(coffeeMachineRequest);
		List<String> requests = Arrays.asList("hot_milk", "hot_milk", "hot_milk");
		instance.serveRequests(requests);
		Thread.sleep(5000);
		List<String> actualOutput = instance.getLowIngredients();
		List<String> expectedOutput = Arrays.asList("Hot Milk");
		assertEquals(expectedOutput, actualOutput);
	}

}
