package Youtube;

import java.sql.DriverAction;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;

import io.github.bonigarcia.wdm.WebDriverManager;

public class facebook {

	public static void main(String[] args) throws InterruptedException {
facebook.facebook();

	}

	
	/**
	 * @throws InterruptedException
	 */
	static  void facebook() throws InterruptedException {
		
		ChromeOptions option = new ChromeOptions();
		option.addArguments("--disable-notifications");
		
		
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver(option);
		driver.manage().window().maximize();
		driver.get("https://www.facebook.com");
		driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
		driver.findElement(By.id("email")).sendKeys("amitkashinwar@yahoo.in");
		driver.findElement(By.id("pass")).sendKeys("Laksh@989126");		
		//Thread.sleep(1000);
		driver.findElement(By.xpath("//button[@value='1']")).click();
		
		driver.findElement(By.xpath("//span[contains(text(),'Amit Kumar')]")).click();
		//Thread.sleep(3000);
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,1000)");
		
		driver.findElement(By.xpath("//span[contains(text(),'See All Friends')]")).click();
		
		  String frinedsCount = driver.findElement(By.xpath("//span[contains(text(),'516')]")).getText();
	        int count = Integer.parseInt(frinedsCount);
 
		
		
	
		
		
		
		
		
		
	
	}
}
