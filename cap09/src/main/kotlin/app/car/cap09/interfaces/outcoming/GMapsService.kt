package app.car.cap09.interfaces.outcoming

import com.jayway.jsonpath.JsonPath
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GMapsService(
    @Value("\${app.car.domain.googlemaps.apikey}")
    val appKey: String,

    @Value("\${interfaces.outcoming.gmaps.host:https://maps.googleapis.com}")
    val gMapsHost: String
) {

    val GMAPS_TEMPLATE: String = "$gMapsHost/maps/api/directions/json?origin={origin}&destination={destination}&key={key}"

    fun getDistanceBetweenAddresses(addressOne: String, addressTwo: String) : Int {
        val template = RestTemplate()
        val jsonResult = template.getForObject(GMAPS_TEMPLATE, String::class.java, addressOne, addressTwo, appKey)

        val rawResults: List<Int> = JsonPath.parse(jsonResult).read("\$..legs[*].duration.value")
        return rawResults.minOfOrNull { it } ?: Int.MAX_VALUE
    }

}