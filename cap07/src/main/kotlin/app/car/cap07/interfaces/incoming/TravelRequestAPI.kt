package app.car.cap07.interfaces.incoming

import app.car.cap07.domain.TravelRequestStatus
import app.car.cap07.domain.TravelService
import app.car.cap07.interfaces.incoming.mapping.TravelRequestMapper
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.hateoas.EntityModel
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime


@Service
@RestController
@RequestMapping(path = ["/travelRequests"], produces = [MediaType.APPLICATION_JSON_VALUE])
class TravelRequestAPI(
    val travelService: TravelService,
    val mapper: TravelRequestMapper
) {

    @PostMapping
    fun makeTravelRequest(@RequestBody @Valid travelRequestInput: TravelRequestInput)
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
    @get:NotNull(message = "O campo passengerId não pode ser nulo")
    val passengerId: Long?,

    @get:NotEmpty(message = "O campo origin não pode estar em branco")
    val origin: String?,

    @get:NotEmpty(message = "O campo destination não pode estar em branco")
    val destination: String?
)

data class TravelRequestOutput(
    val id: Long,
    val origin: String,
    val destination: String,
    val status: TravelRequestStatus,
    val creationDate: LocalDateTime
)