package app.car.cap05.interfaces.incoming

import app.car.cap05.domain.TravelRequestStatus
import app.car.cap05.domain.TravelService
import app.car.cap05.interfaces.incoming.mapping.TravelRequestMapper
import org.springframework.hateoas.EntityModel
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import org.springframework.web.bind.annotation.RequestParam

import org.springframework.web.bind.annotation.GetMapping




@Service
@RestController
@RequestMapping(path = ["/travelRequests"], produces = [MediaType.APPLICATION_JSON_VALUE])
class TravelRequestAPI(
    val travelService: TravelService,
    val mapper: TravelRequestMapper
) {

    @PostMapping
    fun makeTravelRequest(@RequestBody travelRequestInput: TravelRequestInput)
        : EntityModel<TravelRequestOutput> {
        val travelRequest = travelService.saveTravelRequest(mapper.map(travelRequestInput))
        val output = mapper.map(travelRequest)
        return mapper.buildOutputModel(travelRequest, output)
    }

    @GetMapping("/nearby")
    fun listNearbyRequests(@RequestParam currentAddress: String): List<EntityModel<TravelRequestOutput>> {
        val requests = travelService.listNearbyTravelRequests(currentAddress)
        return mapper.buildOutputModel(requests)
    }
}


data class TravelRequestInput(
    val passengerId: Long,
    val origin: String,
    val destination: String
)

data class TravelRequestOutput(
    val id: Long,
    val origin: String,
    val destination: String,
    val status: TravelRequestStatus,
    val creationDate: LocalDateTime
)