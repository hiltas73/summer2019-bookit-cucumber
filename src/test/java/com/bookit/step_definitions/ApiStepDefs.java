package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {

    String token;
    Response response;
    String emailGlobal;

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {

        token = BookItApiUtils.generateToken(email,password);
        System.out.println("token = " + token);
        emailGlobal = email;
    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {

        String url = ConfigurationReader.get("qa2api.uri")+"/api/users/me";

        response = given().header("Authorization", token)
                .when().get(url);

    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
        Assert.assertEquals(response.statusCode(),statusCode);

    }

    @Then("the information about current user from api and database should be match")
    public void the_information_about_current_user_from_api_and_database_should_be_match() {

        //===== API - DATABASE ====================================================

        //GET INFORMATION FROM DATABASE
        String query ="select id,firstname,lastname,role\n" +
                "from users\n" +
                "where email = '"+emailGlobal+"';";

        Map<String, Object> rowMap = DBUtils.getRowMap(query);
        System.out.println("rowMap = " + rowMap);

        //save data from database
        long expectedId = (long) rowMap.get("id");
        String expectedFirstname = (String) rowMap.get("firstname");
        String expectedLastname = (String) rowMap.get("lastname");
        String expectedRole = (String) rowMap.get("role");

        //GET INFORMATION FROM API

        JsonPath jsonPath = response.jsonPath();

        long actualId = jsonPath.getLong("id");
        String actualFirstname = jsonPath.getString("firstName");
        String actualLastname = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        //assert database against to api

        Assert.assertEquals(expectedId,actualId);
        Assert.assertEquals(expectedFirstname,actualFirstname);
        Assert.assertEquals(expectedLastname,actualLastname);
        Assert.assertEquals(expectedRole,actualRole);

    }

    @Then("UI,API and Database user information must be match")
    public void ui_API_and_Database_user_information_must_be_match() {
        //API-DB

        //GET INFORMATION FROM DATABASE
        String query ="select id,firstname,lastname,role\n" +
                "from users\n" +
                "where email = '"+emailGlobal+"';";

        Map<String, Object> rowMap = DBUtils.getRowMap(query);
        System.out.println("rowMap = " + rowMap);

        //save data from database
        long expectedId = (long) rowMap.get("id");
        String expectedFirstname = (String) rowMap.get("firstname");
        String expectedLastname = (String) rowMap.get("lastname");
        String expectedRole = (String) rowMap.get("role");

        //GET INFORMATION FROM API

        JsonPath jsonPath = response.jsonPath();

        long actualId = jsonPath.getLong("id");
        String actualFirstname = jsonPath.getString("firstName");
        String actualLastname = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        //assert database against to api

        Assert.assertEquals(expectedId,actualId);
        Assert.assertEquals(expectedFirstname,actualFirstname);
        Assert.assertEquals(expectedLastname,actualLastname);
        Assert.assertEquals(expectedRole,actualRole);

        //GET INFORMATION FROM THE UI

        SelfPage selfPage = new SelfPage();
        String actualFullnameUI = selfPage.name.getText();
        String actualRoleUI = selfPage.role.getText();

        //verify db vs ui
        String expectedFullname = expectedFirstname+" "+expectedLastname;

        Assert.assertEquals(expectedFullname,actualFullnameUI);
        Assert.assertEquals(expectedRole,actualRoleUI);

        // API VS UI
        //Create fullname from api
        String actualFullname = actualFirstname+" "+actualLastname;

        Assert.assertEquals(actualFullnameUI,actualFullname);
        Assert.assertEquals(actualRoleUI,actualRole);
    }


}