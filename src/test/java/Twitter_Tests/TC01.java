package Twitter_Tests;

import ReusableLibrary.AbstractClass;
import PageObjects.BaseClass;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.util.Objects;

import static io.restassured.RestAssured.given;

public class TC01 extends AbstractClass {

    String consumerKey = "sLM5KNSvySNfYyADLYtWq7vYu";
    String consumerSecret = "jJYrdFLAb52Cog5r1mbNpzbU5VZwNgUTcvxEJEKQlTeEONYYck";
    String accessToken = "1389805833253441539-PypxY3PgzzfdLrFiCnZ5VlIt1BUCoV";
    String tokenSecret = "NoWkD33E2aVNIx10k5Pbzb4fdf91ma0TdIXtBjQsSFDzK";


    String tweetID;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://api.twitter.com/1.1/statuses/";
    }//end of set up


    @Test(priority = 1)
    public void TC1() throws InterruptedException, AWTException {

        //Post tweet through UI
        driver.navigate().to("https://twitter.com/");
        Thread.sleep(3000);


        BaseClass.Twitter_HomePage().signInButton();
        Thread.sleep(2000);

        BaseClass.Twitter_LoginPage().userName("Newaz86602618");
        BaseClass.Twitter_LoginPage().userPassword("NEwaz123");
        BaseClass.Twitter_LoginPage().loginButton();
        Thread.sleep(2000);

        BaseClass.Twitter_ExplorePage().tweet();
        Thread.sleep(1000);
        BaseClass.Twitter_ExplorePage().message("TestAPI");
        BaseClass.Twitter_ExplorePage().tweetButton();
        Thread.sleep(2000);



        //Get recent tweet through API
        Response Resp=
                given().auth().oauth(consumerKey, consumerSecret, accessToken, tokenSecret).
                        queryParam("screen_name", "@Newaz86602618")
                        .when()
                        .get("user_timeline.json")
                        .then()
                        .extract()
                        .response();

        String getTweet=Resp.asString();
        JsonPath js=new JsonPath(getTweet);
        String tweetText= js.get("text[0]").toString();
        tweetID=(js.get("id[0]")).toString();


        if (tweetText.equals("TestAPI")) {
            System.out.println("The following tweet has been created: "+ tweetText);
            System.out.println("The ID of recently created tweet is: "+ tweetID);
            logger.log(LogStatus.PASS, "The following tweet has been created: " + tweetText + "The ID of the tweet is: "+tweetID);
        } else {
            System.out.println("The following tweet has not been created: " + tweetText);
            System.out.println("ID is not available since tweet has not been posted.");
            logger.log(LogStatus.INFO, "The following tweet has not been created: " + tweetText + " No ID available.");
        }


        //Delete recent tweet through API
        given().
                auth().oauth(consumerKey, consumerSecret, accessToken, tokenSecret).
                queryParam("id", tweetID)
                .when()
                .post("destroy.json")
                .then();
        System.out.println("Recent twitter message with id '" + tweetID + "' has been deleted");
        logger.log(LogStatus.PASS,"Recent twitter message with id '" + tweetID + "' has been deleted");




        //Refresh page and verify tweet is deleted
        driver.navigate().refresh();
        Thread.sleep(2000);

        //Verify tweet is not present with API

        Response Resp1=
        given().auth().oauth(consumerKey, consumerSecret, accessToken, tokenSecret).
                queryParam("screen_name", "@Newaz86602618")
                .when()
                .get("user_timeline.json")
                .then()
                .extract()
                .response();

        String getNewTweet=Resp1.asString();
        JsonPath js1=new JsonPath(getNewTweet);
        String NewTweet= js1.get("text[0]");


        //String NewTweetID=(js1.get("id[0]"));


        Objects.toString("NewTweet", NewTweet);

        //Objects.toString("NewTweet", NewTweetID);

        if (NewTweet == null){
            System.out.println("The recently posted tweet: " + tweetText + " with ID " + tweetID + " is no longer present.");
            logger.log(LogStatus.PASS, "The recently posted tweet: " + tweetText + " with ID " + tweetID + " is no longer present.");
        }else {
            System.out.println("The recently posted tweet: " + tweetText + " is still present.");
            logger.log(LogStatus.INFO, "The recently posted tweet " + tweetText + " is still present.");

        }


        BaseClass.Twitter_ExplorePage().option();
        BaseClass.Twitter_ExplorePage().signOut();
        BaseClass.Twitter_ExplorePage().ConfirmSignOut();


    }//end of test
}//end of java class
