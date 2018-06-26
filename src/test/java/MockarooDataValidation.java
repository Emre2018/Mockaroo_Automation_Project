import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MockarooDataValidation {

	WebDriver driver;

	@BeforeClass
	public void setUp() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();

	}

	@BeforeMethod
	public void navigateToHomePage() {
		driver.get("https://mockaroo.com/");

	}

	@Test
	public void openApplication() throws InterruptedException, IOException {

		String actualTitle = driver.getTitle();
		assertTrue(actualTitle.contains("Mockaroo"));

		String mocka = driver.findElement(By.xpath("//div[@class='brand']")).getText();
		String realistic = driver.findElement(By.xpath("//div[@class='tagline']")).getText();
		assertTrue(mocka.contains("mockaroo") && realistic.contains("realistic data generator"));

		// 5. Remote all existing fields by clicking on x icon link
		List<WebElement> clickX = driver
				.findElements(By.xpath("//div[@id='fields']//a[@class='close remove-field remove_nested_fields']"));
		for (WebElement closeEach : clickX) {
			closeEach.click();
		}

		// 6. Assert that ‘Field Name’ , ‘Type’, ‘Options’ labels are displayed
		String fieldName = driver.findElement(By.xpath("//div[@class='column column-header column-name']")).getText();
		String type = driver.findElement(By.xpath("//div[@class='column column-header column-type']")).getText();
		String options = driver.findElement(By.xpath("//div[@class='column column-header column-options']")).getText();

		assertEquals(fieldName, "Field Name");
		assertEquals(type, "Type");
		assertEquals(options, "Options");

		// 7. Assert that ‘Add another field’ button is enabled.
		// Find using xpath with tagname and text. isEnabled() method in selenium
		assertTrue(driver.findElement(By.xpath("//a[.='Add another field']")).isEnabled());

		// 8. Assert that default number of rows is 1000.
		String defaultNumber = driver.findElement(By.xpath("//input[@value='1000']")).getAttribute("value");
		assertEquals(Integer.parseInt(defaultNumber), 1000);

		// 9. Assert that default format selection is CSV
		Select defaultSelection = new Select(driver.findElement(By.xpath("//select[@id='schema_file_format']")));
		WebElement option = defaultSelection.getFirstSelectedOption();
		String optionDefault = option.getText();
		assertEquals(optionDefault, "CSV");

		// 10. Assert that Line Ending is Unix(LF)
		Select lineEnding = new Select(driver.findElement(By.xpath("//select[@id='schema_line_ending']")));
		WebElement lineOption = lineEnding.getFirstSelectedOption();
		String lineDefault = lineOption.getText();
		assertEquals(lineDefault, "Unix (LF)");

		// 11. Assert that header check box is checked and BOM is unchecked
		assertTrue(driver.findElement(By.xpath("//input[@id='schema_include_header']")).isSelected());
		assertFalse(driver.findElement(By.xpath("//input[@id='schema_bom']")).isSelected());

		// 12. Click on 'Add another field' and enter name “City”
		driver.findElement(By.xpath("//a[.='Add another field']")).click();
		driver.findElement(By.xpath(
				"(//div[@id='fields']//input[starts-with(@id, 'schema_columns_attributes_')][contains(@id,'name')])[last()]"))
				.sendKeys("City");

		// 13. Click on Choose type and assert that Choose a Type dialog box is
		// displayed.
		driver.findElement(By.xpath("(//input[@placeholder='choose type...'])[last()]")).click();
		WebElement dialogBox = driver.findElement(By.xpath("//div[@id='type_dialog']//h3[.='Choose a Type']"));
		Thread.sleep(1000);
		assertTrue(dialogBox.getText().equals("Choose a Type"));

		// 14. Search for “city” and click on City on search results.
		driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("city");
		driver.findElement(By.xpath("//div[.='City']")).click();

		// 15. Repeat steps 12-14 with field name and type “Country”
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[.='Add another field']")).click();
		driver.findElement(By.xpath(
				"(//div[@id='fields']//input[starts-with(@id, 'schema_columns_attributes_')][contains(@id,'name')])[last()]"))
				.sendKeys("Country");

		driver.findElement(By.xpath("(//input[@placeholder='choose type...'])[last()]")).click();
		WebElement dialogBox2 = driver.findElement(By.xpath("//div[@id='type_dialog']//h3[.='Choose a Type']"));
		Thread.sleep(1000);
		assertTrue(dialogBox2.getText().equals("Choose a Type"));

		driver.findElement(By.xpath("//input[@id='type_search_field']")).clear();
		driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("Country");
		driver.findElement(By.xpath("//div[.='Country']")).click();

		// 16. Click on Download Data.
		Thread.sleep(1000);
		driver.findElement(By.xpath("//div[@class='footer']//button[@id='download']")).click();

		// 17. Open the downloaded file using BufferedReader.
		Thread.sleep(2000);
		
		File f = new File("C:\\Users\\Emre\\Downloads\\MOCK_DATA (3).csv");
		BufferedReader br = new BufferedReader(new FileReader(f));
		
		List<String> file = new ArrayList();
		String temp = br.readLine();
		while (temp != null) {
		    file.add(temp);
		    temp = br.readLine();
		}
		
		//18. Assert that first row is matching with Field names that we selected.
		assertEquals(file.get(0), "City,Country");
		
		//19. Assert that there are 1000 records
		file.remove(0);
		assertTrue(file.size()==1000);
		
		//20. From file add all Cities to Cities ArrayList
		List<String> cities = new ArrayList<String>();
		for (String str : file) {
		    cities.add(str.substring(0, str.indexOf(",")));
		}
		
		//21. Add all countries to Countries ArrayList
		List<String> countries = new ArrayList<String>();
		for(String country : countries) {
			countries.add(country.substring(country.indexOf(",") + 1));
		}

		//22. Sort all cities and find the city with the longest name and shortest name
		Collections.sort(cities);
		String cityWithLongest = cities.get(0);
		String cityWithShortest = cities.get(0);
		for(int i=1; i<cities.size();i++) {
			if(cityWithLongest.length() < cities.get(i).length()) {
				cityWithLongest = cities.get(i);
			}
			if(cityWithShortest.length() > cities.get(i).length()) {
				cityWithShortest = cities.get(i);
			}
		}
		System.out.println("City with the longest name : " + cityWithLongest);
		System.out.println("City with the shortest name : " + cityWithShortest);
		
		//23. In Countries ArrayList, find how many times each Country is mentioned. 
		SortedSet<String> sortedCountry = new TreeSet<String>(countries);
		for (String howMany : sortedCountry) {
			System.out.println(howMany + ":\t" + Collections.frequency(countries, howMany));
		}
		
		//24. From file add all Cities to citiesSet HashSet
		Set<String> citiesSet = new HashSet<String>(cities);
		
		//25. Count how many unique cities are in Cities ArrayList and assert that
		//it is matching with the count of citiesSet HashSet.
		int uniqueCities = 0;
		for (int i = 0; i < cities.size(); i++) {
			if (i == cities.lastIndexOf(cities.get(i)))
				uniqueCities++;
		}
		assertEquals(uniqueCities, citiesSet.size());
		
		//26. Add all Countries to countrySet HashSet 
		Set<String> countrySet = new HashSet<>(countries);
		
		//27. Count how many unique cities are in Countries ArrayList and assert that
		//it is matching with the count of countrySet HashSet.
		int uniqueCitiesCount = 0;
		for (int i = 0; i < countries.size(); i++) {
			if (i == countries.lastIndexOf(countries.get(i)))
				uniqueCitiesCount++;
		}
		assertEquals(uniqueCitiesCount, countrySet.size());
		
		
		
		
		
		
		
		
		
		
		

	}

	// @AfterMethod
	public void tearDown() {
		driver.close();
	}

}
