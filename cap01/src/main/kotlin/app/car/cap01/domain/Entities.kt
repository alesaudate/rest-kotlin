package app.car.cap01.domain

import java.time.LocalDate
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Driver(
        @Id
        var id: Long? = null,
        val name: String,
        val birthDate: LocalDate
)