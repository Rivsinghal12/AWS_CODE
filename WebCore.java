package library;

import java.io.IOException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.reusable.UIwebDriver;

public class WebCore {

  public static WebDriver Driver;

  public static void LaunchDriverAndGet( String URL, String Browser, String Pstatus ) throws IOException {
    try {
      if ( Browser.toLowerCase().contains( "firefox" ) ) {
        if ( Pstatus.toLowerCase().contains( "true" ) || Pstatus.toLowerCase().contains( "yes" ) )
          UIwebDriver.setDriverProxy();
        Driver = UIwebDriver.launchFirefoxDriver( URL );
      } else
        if ( Browser.toLowerCase().contains( "chrome" ) ) {
          if ( Pstatus.toLowerCase().contains( "true" ) || Pstatus.toLowerCase().contains( "yes" ) )
            UIwebDriver.setDriverProxy();
          Driver = UIwebDriver.launchChromeDriver( URL );
        }
      if ( Driver.findElements( By.xpath( "//*[@id=\"main-message\"]/h1/span" ) ).size() != 0 )
        Assert.fail( "Error Opening URL --->" + URL );
    } catch ( Exception e ) {
      Assert.fail( "Error launching browser!!!" );
     
    }
  }

  public static void NavigateToURL( String URL ) throws IOException {
    try {
      if ( URL != null && !URL.isEmpty() ) {
        Driver.navigate().to( URL );
        if ( Driver.findElements( By.xpath( "//*[@id=\"main-message\"]/h1/span" ) ).size() != 0 ) {
          Driver.close();
          Assert.fail( "Error Opening URL --->" + URL );
        }
      }
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Error navigating URL --->" + URL );
    }
  }

  public static boolean checkIfLoadComplete() {
    try {
      if ( ( (JavascriptExecutor) Driver ).executeScript( "return document.readyState" ).equals( "loaded" )
          || ( (JavascriptExecutor) Driver ).executeScript( "return document.readyState" ).equals( "complete" ) )
        return true;
      else
        return false;
    } catch ( Exception e ) {
      closeDriver();
      return false;
    }
  }

  public static boolean ElementVisibility_Xpath( String Xpath ) {
    try {
      WebDriverWait wait = new WebDriverWait( Driver, 10 );
      return wait.until( ExpectedConditions.or( ExpectedConditions.visibilityOfElementLocated( By.xpath( Xpath ) ) ) );
    } catch ( Exception e ) {
      closeDriver();
      return false;
    }
  }

  public static boolean ElementEnabled_Xpath( String Xpath ) {
    try {
      return Driver.findElement( By.xpath( Xpath ) ).isEnabled();
    } catch ( Exception e ) {
      closeDriver();
      return false;
    }
  }

  public static boolean ElementVisibility_ID( String ID ) {
    try {
      WebDriverWait wait = new WebDriverWait( Driver, 10 );
      return wait.until( ExpectedConditions.or( ExpectedConditions.visibilityOfElementLocated( By.id( ID ) ) ) );
    } catch ( Exception e ) {
      closeDriver();
      return false;
    }
  }

  public static void findElement_Xpath( String Xpath ) throws IOException {
    try {
      if ( Driver.findElement( By.xpath( Xpath ) ).isDisplayed() == true )
        System.out.println( "Element found with xpath ---> " + Xpath );
      else
        Assert.fail( "Element not found!" );
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Exception while checking findElement_Xpath!!!" );
    }
  }

  public static void findElement_Id( String ID ) throws IOException {
    try {
      if ( Driver.findElement( By.id( ID ) ).isDisplayed() == true )
        System.out.println( "Element found with ID ---> " + ID );
      else
        Assert.fail( "Element not found!" );
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Exception while checking findElement_ID!!!" );
    }
  }

  public static void clickElement_Xpath( String Xpath ) throws IOException {
    try {
      if ( Driver.findElement( By.xpath( Xpath ) ).isDisplayed() == true )
        Driver.findElement( By.xpath( Xpath ) ).click();
      else
        Assert.fail( "Element not found!" );
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Exception while clickElement_Xpath!!!" );
    }
  }

  public static void clickElement_Id( String ID ) throws IOException {
    try {
      if ( Driver.findElement( By.id( ID ) ).isDisplayed() == true )
        Driver.findElement( By.id( ID ) ).click();
      else
        Assert.fail( "Element not found!" );
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Exception while clickElement_Id!!!" );
    }
  }

  public static String getValueElement_Xpath( String Xpath ) throws IOException {
    String elementValue = null;
    try {
      if ( Driver.findElement( By.xpath( Xpath ) ).isDisplayed() == true )
        elementValue = Driver.findElement( By.xpath( Xpath ) ).getText();
      else
        Assert.fail( "Element not found!" );
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Exception while getValueElement_Xpath!!!" );
    }
    return elementValue;
  }

  public static String getValueElement_Id( String ID ) throws IOException {
    String elementValue = null;
    try {
      if ( Driver.findElement( By.id( ID ) ).isDisplayed() == true )
        elementValue = Driver.findElement( By.id( ID ) ).getText();
      else
        Assert.fail( "Element not found!" );
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Exception while getValueElement_Id!!!" );
    }
    return elementValue;
  }

  public static void sendValueElement_Xpath( String Value, String Xpath ) throws IOException {
    try {
      if ( Driver.findElement( By.xpath( Xpath ) ).isDisplayed() == true )
        Driver.findElement( By.xpath( Xpath ) ).sendKeys( Value );
      else
        Assert.fail( "Element not found!" );
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Exception while sendValueElement_Xpath!!!" );
    }
  }

  public static void sendValueElement_Id( String Value, String ID ) throws IOException {
    try {
      if ( Driver.findElement( By.id( ID ) ).isDisplayed() == true )
        Driver.findElement( By.id( ID ) ).sendKeys( Value );
      else
        Assert.fail( "Element not found!" );
    } catch ( Exception e ) {
      closeDriver();
      Assert.fail( "Exception while sendValueElement_Xpath!!!" );
    }
  }

  public static void closeDriver() {
    try {
      UIwebDriver.closeDriver( Driver );
    } catch ( Exception e ) {
      Assert.fail( "Exception while closing webDriver!!!" );
    }
  }
}
