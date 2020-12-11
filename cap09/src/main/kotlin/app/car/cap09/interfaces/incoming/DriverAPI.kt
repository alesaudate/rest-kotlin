package app.car.cap09.interfaces.incoming


import app.car.cap09.domain.Driver
import app.car.cap09.domain.DriverRepository
import app.car.cap09.interfaces.incoming.errorhandling.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.linkTo
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
import org.springframework.web.bind.annotation.RequestParam



@Service
@RestController
@RequestMapping(path = ["/drivers"], produces = [MediaType.APPLICATION_JSON_VALUE])
class DriverAPIImpl(
    val driverRepository: DriverRepository
) : DriverAPI{

    @GetMapping
    override fun listDrivers(@RequestParam(name = "page", defaultValue = "0") page: Int): Drivers {
        val driverPage = driverRepository.findAll(PageRequest.of(page, PAGE_SIZE))
        val drivers = driverPage.content.map { EntityModel.of(it) }
        val lastPageLink = linkTo<DriverAPIImpl> { listDrivers(driverPage.totalPages - 1) }.withRel("lastPage")
        return Drivers(drivers, listOf(lastPageLink))
    }


    @GetMapping("/{id}")
    override fun findDriver(@PathVariable("id") id: Long) =
        driverRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @PostMapping
    override fun createDriver(@RequestBody driver: Driver) = driverRepository.save(driver)

    @PutMapping("/{id}")
    override fun fullUpdateDriver(@PathVariable("id") id:Long, @RequestBody driver:Driver) : Driver {
        val foundDriver = findDriver(id)
        val copyDriver = foundDriver.copy(
            birthDate = driver.birthDate,
            name = driver.name
        )
        return driverRepository.save(copyDriver)
    }

    @PatchMapping("/{id}")
    override fun incrementalUpdateDriver(@PathVariable("id") id:Long, @RequestBody driver: PatchDriver) : Driver {
        val foundDriver = findDriver(id)
        val copyDriver = foundDriver.copy(
            birthDate = driver.birthDate ?: foundDriver.birthDate,
            name = driver.name ?: foundDriver.name
        )
        return driverRepository.save(copyDriver)
    }

    @DeleteMapping("/{id}")
    override fun deleteDriver(@PathVariable("id") id: Long) =
        driverRepository.delete(findDriver(id))

    companion object {
        private const val PAGE_SIZE: Int = 10
    }
}


data class PatchDriver(
    val name: String?,
    val birthDate: LocalDate?
)


open class Drivers(
    val drivers: List<EntityModel<Driver>>,
    val links: List<Link> = emptyList()
)

@Tag(name = "Driver API", description = "Manipula dados de motoristas.")
interface DriverAPI {


    @Operation(description = "Lista todos os motoristas disponíveis")
    fun listDrivers(page: Int) : Drivers

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