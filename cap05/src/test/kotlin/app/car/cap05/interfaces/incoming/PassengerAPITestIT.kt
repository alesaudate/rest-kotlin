package app.car.cap05.interfaces.incoming

import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort

import io.restassured.RestAssured.given
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PassengerAPITestIT {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.port = port
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