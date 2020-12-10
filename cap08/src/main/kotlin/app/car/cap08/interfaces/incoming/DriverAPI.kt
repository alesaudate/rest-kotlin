package app.car.cap08.interfaces.incoming

import app.car.cap08.domain.Driver
import app.car.cap08.domain.DriverRepository
import app.car.cap08.interfaces.incoming.errorhandling.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import org.springframework.web.bind.annotation.DeleteMapping

@Service
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DriverAPIImpl(
    val driverRepository: DriverRepository
) : DriverAPI{

    @GetMapping("/drivers")
    override fun listDrivers() = driverRepository.findAll()

    @GetMapping("/drivers/{id}")
    override fun findDriver(@PathVariable("id") id: Long) =
        driverRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @PostMapping("/drivers")
    override fun createDriver(@RequestBody driver: Driver) = driverRepository.save(driver)

    @PutMapping("/drivers/{id}")
    override fun fullUpdateDriver(@PathVariable("id") id:Long, @RequestBody driver:Driver) : Driver {
        val foundDriver = findDriver(id)
        val copyDriver = foundDriver.copy(
            birthDate = driver.birthDate,
            name = driver.name
        )
        return driverRepository.save(copyDriver)
    }

    @PatchMapping("/drivers/{id}")
    override fun incrementalUpdateDriver(@PathVariable("id") id:Long, @RequestBody driver: PatchDriver) : Driver {
        val foundDriver = findDriver(id)
        val copyDriver = foundDriver.copy(
            birthDate = driver.birthDate ?: foundDriver.birthDate,
            name = driver.name ?: foundDriver.name
        )
        return driverRepository.save(copyDriver)
    }

    @DeleteMapping("/drivers/{id}")
    override fun deleteDriver(@PathVariable("id") id: Long) =
        driverRepository.delete(findDriver(id))
}


data class PatchDriver(
    val name: String?,
    val birthDate: LocalDate?
)

@Tag(name = "Driver API", description = "Manipula dados de motoristas.")
interface DriverAPI {


    @Operation(description = "Lista todos os motoristas disponíveis")
    fun listDrivers() : List<Driver>

    @Operation(description = "Localiza um motorista específico", responses = [
        ApiResponse(responseCode = "200", description = "Caso o motorista tenha sido encontrado na base"),
        ApiResponse(responseCode = "404", description = "Caso o motorista não tenha sido encontrado",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ])
    fun findDriver(@Parameter(description = "ID do motorista a ser localizado") id: Long) : Driver

    fun createDriver(driver: Driver): Driver

    fun fullUpdateDriver(id:Long, driver:Driver) : Driver

    fun incrementalUpdateDriver(id:Long, driver: PatchDriver) : Driver

    fun deleteDriver(@PathVariable("id") id: Long)

}