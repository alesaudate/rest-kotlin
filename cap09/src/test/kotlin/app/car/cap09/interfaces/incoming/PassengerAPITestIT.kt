package app.car.cap09.interfaces.incoming

import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


import io.restassured.RestAssured.given
import io.restassured.RestAssured.basic
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PassengerAPITestIT {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "https://localhost:$port"
        RestAssured.useRelaxedHTTPSValidation()
        RestAssured.authentication = basic("admin","password")
    }

    @Test
    fun testCreatePassenger() {
        val createPassengerJSON = """
            {"name":"Alexandre Saudate"}
        """.trimIndent()

        given()
            .contentType(io.restassured.http.ContentType.JSON)
            .body(createPassengerJSON)
            .post("/passengers")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("name", equalTo("Alexandre Saudate"))

    }
}