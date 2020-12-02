package app.car.cap04.interfaces.incoming.mapping

import app.car.cap04.domain.PassengerRepository
import app.car.cap04.domain.TravelRequest
import app.car.cap04.interfaces.incoming.TravelRequestInput
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.hateoas.EntityModel

import app.car.cap04.interfaces.incoming.TravelRequestOutput
import app.car.cap04.interfaces.incoming.PassengerAPI

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder


@Component
class TravelRequestMapper(
    val passengerRepository: PassengerRepository
) {



    fun map(input: TravelRequestInput) : TravelRequest {

        val passenger = passengerRepository.findById(input.passengerId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        return TravelRequest(passenger = passenger,
            origin = input.origin,
            destination = input.destination)

    }

    fun map(travelRequest: TravelRequest) : TravelRequestOutput {
        return TravelRequestOutput(
            id = travelRequest.id!!,
            origin = travelRequest.origin,
            destination = travelRequest.destination,
            status = travelRequest.status,
            creationDate = travelRequest.creationDate
        )
    }

    fun buildOutputModel(travelRequest: TravelRequest, output: TravelRequestOutput): EntityModel<TravelRequestOutput> {

        val passengerLink = WebMvcLinkBuilder
            .linkTo(PassengerAPI::class.java)
            .slash(travelRequest.passenger.id)
            .withRel("passenger")
            .withTitle(travelRequest.passenger.name)

        return EntityModel.of(output, passengerLink)
    }


    fun buildOutputModel(requests: List<TravelRequest>) =
        requests.map { buildOutputModel(it, map(it)) }


}