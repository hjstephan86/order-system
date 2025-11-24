package com.example.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RestApiIT {
    
    @ArquillianResource
    private URL deploymentUrl;
    
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackages(true, "com.example")
            .addAsResource("META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @BeforeEach
    public void setup() {
        RestAssured.baseURI = deploymentUrl.toString() + "api";
    }
    
    @Test
    public void testCreateAndGetKunde() {
        String kundeJson = """
            {
                "name": "REST Test Kunde",
                "email": "rest@test.com",
                "adresse": "REST Straße 1"
            }
            """;
        
        Integer kundeId = given()
            .contentType(ContentType.JSON)
            .body(kundeJson)
        .when()
            .post("/kunden")
        .then()
            .statusCode(201)
            .body("name", equalTo("REST Test Kunde"))
            .body("email", equalTo("rest@test.com"))
            .extract().path("id");
        
        given()
        .when()
            .get("/kunden/" + kundeId)
        .then()
            .statusCode(200)
            .body("name", equalTo("REST Test Kunde"));
    }
    
    @Test
    public void testGetAllProdukte() {
        given()
        .when()
            .get("/produkte")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(0)));
    }
    
    @Test
    public void testSearchProdukte() {
        given()
            .queryParam("name", "laptop")
        .when()
            .get("/produkte/suche")
        .then()
            .statusCode(200);
    }
}
