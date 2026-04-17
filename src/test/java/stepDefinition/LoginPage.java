package stepDefinition;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import utility.DynamoDBUtil;

import java.util.List;
import java.util.Map;

import static pages.HomePage.click_hamburger_menu;
import static pages.HomePage.click_signIn_link;
import static pages.LoginPage.*;

public class LoginPage {

    @When("User successfully enters the log in details")
    public void user_successfully_enters_the_log_in_details() throws InterruptedException {
        Map<String, String> data = DynamoDBUtil.getTestData("LOGIN_01_Success");

      try {
          String username = data.get("username");
          String password = data.get("password");
          sendkeys_username(username);
          sendkeys_password(password);
          click_login_btn();
          DynamoDBUtil.insertTestResult("LOGIN_01_Success", "PASS");
      } catch (Exception e) {
          DynamoDBUtil.insertTestResult("LOGIN_01_Success", "FAIL");
      }
    }

    @When("User clicks on new Registration button")
    public void user_clicks_on_new_registration_button() throws InterruptedException {
        click_NewRegister_btn();
    }

}
