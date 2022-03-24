package io.iohk.metadata.api;

import groovy.json.JsonException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.internal.UriValidator;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Steps {
    private final String validSubjectString = "2048c7e09308f9138cef8f1a81733b72e601d016eea5eef759ff2933416d617a696e67436f696e";
    private final String[] properties = new String[]{"url", "name", "ticker", "logo", "description"};
    private String subjectValue;
    private String propertyName;
    private String url;
    private Response response;
    private JSONObject request;
    private List<String> subjectList;
    private List<Response> responseList;
    public Steps() {
        request = new JSONObject();
        JSONArray subjects = new JSONArray("[\"789ef8ae89617f34c07f7f6a12e4d65146f958c0bc15a97b4ff169f16861707079636f696e\"]");
        JSONArray props = new JSONArray(properties);
        request.put("subjects", subjects);
        request.put("properties",props);

        responseList = new ArrayList<Response>();
    }
    @Given("^I have an invalid subject as (.*)$")
    public void iHaveInvalidSubject(String invalidValue) {
        subjectValue = invalidValue;
        propertyName = null;
    }

    @Given("^I have a valid subject$")
    public void iHaveValidSubject() {
        subjectValue = validSubjectString;
        propertyName = null;
    }

    @Given("^I have the valid property name as (.*)$")
    public void iHaveValidProperty(String validPropertyName) {
        propertyName = validPropertyName;
    }

    @Given("^I have an invalid property as (.*)$")
    public void iHaveInvalidProperty(String invalidPropertyName) {
        propertyName = invalidPropertyName;
    }

    @When("^I call (.*)$")
    public void iCallMetaData(String url) {
        RestAssured.baseURI = url;

        if(!url.contains("query")) {
            if (propertyName == null) {
                response = RestAssured.get(url + subjectValue);
            } else {
                response = RestAssured.get(url + subjectValue + "/properties/" + propertyName);
            }
        } else {
            response = RestAssured.given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(request.toString())
                    .when()
                    .post(url);
        }
    }

    @Then("^I can see the status code (.*)$")
    public void iCanSeeStatusCodeStatusCode(int statusCode) {
        response.then().statusCode(statusCode);

        if(!responseList.isEmpty()) {
            responseList.forEach(
                    response -> {
                        response.then().statusCode(statusCode);
                    }
            );
        }
    }

    @Then("I can get the successful response")
    public void iCanGetTheSuccessfulResponse() {
        response.then().statusCode(200);
    }

    @And("I can see the subject is matched")
    public void iCanSeeTheSubjectIsMatched() {
        response.then().body("subject", Matchers.equalTo(subjectValue));
    }

   @And("^I can see all properties include (.*)")
    public void iCanSeeAllPropertiesIncludeSequenceNumber(String attrName) {
        response.then()
                .assertThat()
                .body("url." + attrName, Matchers.notNullValue())
                .body("name." + attrName, Matchers.notNullValue())
                .body("ticker. " + attrName, Matchers.notNullValue())
                .body("logo. " + attrName, Matchers.notNullValue())
                .body("description. " + attrName, Matchers.notNullValue());
    }

    @And("^I can see all (.*) are valid in all properties")
    public void iCanSeeAllSubAttrsAreValidInAllProperties(String subAttrName) {
        for (String name :
             properties) {
            ArrayList<String>
                    value = response.then()
                    .assertThat()
                    .extract()
                    .path(name + ".signatures." + subAttrName);
            value.forEach(
                    s -> {
                        Assert.assertTrue(subAttrName + " should be valid hex encoded string but it isn't. : [" + s + "]", TestHelper.checkHexString(s, subAttrName.equalsIgnoreCase("signature") ? 128 : 64));
                    }
            );
        }
    }

    @And("^I have the (.*) property$")
    public void iHaveProperty(String name) {
        propertyName = name;
    }

    @And("^I can see the valid (.*)$")
    public void iCanSeeTheValidAttrsValues(String attrName) {
       Object value =  response.then()
                                .assertThat()
                                .extract()
                                .path(attrName);
       switch(attrName) {
           case "sequenceNumber":
               Assert.assertTrue("SequenceNumber should be number type.", value instanceof Integer);
               break;
           case "value":
               if(propertyName.equalsIgnoreCase("url")) {
                   Assert.assertTrue("URL's value should URI type string", value instanceof String && UriValidator.isUri(value.toString()));
               } else {
                   Assert.assertTrue(propertyName + "'s value should be string", value instanceof String);
               }
               break;
           case "signatures":
               Assert.assertTrue("Signatures should return the list of signature and publicKey pair.", value instanceof ArrayList);
               ArrayList signatures = (ArrayList)value;

               signatures.forEach(
                       pair -> {
                           LinkedHashMap<String, String> details = (LinkedHashMap<String, String>)pair;
                           Assert.assertNotNull(details);
                           Assert.assertTrue("signature should be 128 length hex encoded string", TestHelper.checkHexString(details.get("signature"), 128));
                           Assert.assertTrue("public key should be 64 length hex encoded string", TestHelper.checkHexString(details.get("publicKey"), 64));
                       }
               );
               break;
       }
    }

    @Given("^I have (.*) data in (subjects|properties)$")
    public void iHaveInvalidDataInAttribute(String invalidValue, String attr) {
        JSONArray invalidArray = new JSONArray( invalidValue.split(","));
        request.put(attr, invalidArray);
    }

    @And("^I have (the valid|no) properties data$")
    public void iHaveTheValidPropertiesData(String indicator) {
        if(indicator.equalsIgnoreCase("no")) {
            request.remove("properties");
        }
    }

    @When("^I repeatedly call (.*)$")
    public void iRepeatedlyCallWithSubjects(String uri) {
        subjectList.forEach(
                subject -> {
                    responseList.add(RestAssured.get(uri + "subject"));
                }
        );
    }

    @And("I can see the matched data with above subjects and their properties")
    public void iCanSeeTheMatchedDataWithAboveSubjectsAndTheirProperties() {
        Assert.assertEquals(subjectList.size(), responseList.size());
        responseList.forEach(
                response -> {
                    response.then()
                            .body("subject", Matchers.arrayContaining(subjectList));
                }
        );
    }

    @Given("^I have the valid subjects data as (.*)$")
    public void iHaveTheValidSubjectsDataAsSubjects(String subjects) {
        subjectList = new ArrayList<String>(Arrays.asList(subjects.split(",")));
        JSONArray subs = new JSONArray(subjectList.toArray());
        request.put("subjects", subs);
    }

    @And("^(.*) attribute has value$")
    public void nameAttributeHasValue(String attrName) {
        try {
            response.then()
                    .assertThat()
                    .body(attrName, Matchers.notNullValue());
        } catch(JsonException exception) {
            Assert.assertTrue(exception.getMessage(), false);
        }
    }
}
