package app.car.cap08.interfaces.incoming

import app.car.cap08.infrastructure.loadFileContents
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.hamcrest.Matchers.equalTo as equalToHamcrest


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = WireMockConfiguration.DYNAMIC_PORT)
@ActiveProfiles("test")
class TravelRequestAPITestIT {

    @LocalServerPort
    private var port: Int = 0


    @Autowired
    lateinit var server: WireMockServer

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = "https://localhost:$port"
        RestAssured.useRelaxedHTTPSValidation()
        RestAssured.authentication = RestAssured.basic("admin", "password")
    }

    @Test
    fun testFindNearbyTravelRequests() {
        setupServer()
        val passengerId = given()
            .contentType(ContentType.JSON)
            .body(loadFileContents("/requests/passengers_api/create_new_passenger.json"))
            .post("/passengers")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("name", equalToHamcrest("Alexandre Saudate"))
            .extract()
            .body()
            .jsonPath().getString("id")

        val data = mapOf<String, String>(
            "passengerId" to passengerId
        )

        val travelRequestId = given()
            .contentType(ContentType.JSON)
            .body(loadFileContents("/requests/travel_requests_api/create_new_request.json", data))
            .post("/travelRequests")
            .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("origin", equalToHamcrest("Avenida Paulista, 1000"))
            .body("destination", equalToHamcrest("Avenida Ipiranga, 100"))
            .body("status", equalToHamcrest("CREATED"))
            .body("_links.passenger.title", equalToHamcrest("Alexandre Saudate"))
            .extract()
            .jsonPath().getInt("id")

        given()
            .get("/travelRequests/nearby?currentAddress=Avenida Paulista, 900")
            .then()
            .statusCode(200)
            .body("[0].id", equalToHamcrest(travelRequestId))
            .body("[0].origin", equalToHamcrest("Avenida Paulista, 1000"))
            .body("[0].destination", equalToHamcrest("Avenida Ipiranga, 100"))
            .body("[0].status", equalToHamcrest("CREATED"))
    }

    fun setupServer() {

        server.stubFor(get(urlPathEqualTo("/maps/api/directions/json"))
            .withQueryParam("origin", equalTo("Avenida Paulista, 900"))
            .withQueryParam("destination", equalTo("Avenida Paulista, 1000"))
            .withQueryParam("key", equalTo("APIKEY"))
            .willReturn(okJson(loadFileContents("/responses/gmaps/sample_response.json")))
        )

    }
}