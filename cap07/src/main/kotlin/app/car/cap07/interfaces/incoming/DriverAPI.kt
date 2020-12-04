package app.car.cap07.interfaces.incoming

import app.car.cap07.domain.Driver
import app.car.cap07.domain.DriverRepository
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
class DriverAPI(
    val driverRepository: DriverRepository
) {

    @GetMapping("/drivers")
    fun listDrivers() = driverRepository.findAll()

    @GetMapping("/drivers/{id}")
    fun findDriver(@PathVariable("id") id: Long) =
        driverRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @PostMapping("/drivers")
    fun createDriver(@RequestBody driver: Driver) = driverRepository.save(driver)

    @PutMapping("/drivers/{id}")
    fun fullUpdateDriver(@PathVariable("id") id:Long, @RequestBody driver:Driver) : Driver {
        val foundDriver = findDriver(id)
        val copyDriver = foundDriver.copy(
            birthDate = driver.birthDate,
            name = driver.name
        )
        return driverRepository.save(copyDriver)
    }

    @PatchMapping("/drivers/{id}")
    fun incrementalUpdateDriver(@PathVariable("id") id:Long, @RequestBody driver: PatchDriver) : Driver {
        val foundDriver = findDriver(id)
        val copyDriver = foundDriver.copy(
            birthDate = driver.birthDate ?: foundDriver.birthDate,
            name = driver.name ?: foundDriver.name
        )
        return driverRepository.save(copyDriver)
    }


    @DeleteMapping("/drivers/{id}")
    fun deleteDriver(@PathVariable("id") id: Long) =
        driverRepository.delete(findDriver(id))


}


data class PatchDriver(
    val name: String?,
    val birthDate: LocalDate?
)