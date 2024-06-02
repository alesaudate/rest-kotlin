package app.car.cap09.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size

@Entity
@Schema(description = "Representa um motorista dentro da plataforma")
data class Driver(
        @Id
        @GeneratedValue
        var id: Long? = null,

        @get:Schema(description = "Nome do motorista")
        @get:Size(min = 5, max = 255)
        val name: String,

        @get:Schema(description = "Data de nascimento do motorista")
        val birthDate: LocalDate
)

@Entity
data class Passenger(
        @Id
        @GeneratedValue
        var id: Long? = null,
        val name: String
)

@Entity
data class TravelRequest (
        @Id
        @GeneratedValue
        var id: Long? = null,

        @ManyToOne
        val passenger: Passenger,
        val origin: String,
        val destination: String,
        val status: TravelRequestStatus = TravelRequestStatus.CREATED,
        val creationDate: LocalDateTime = LocalDateTime.now()
)

enum class TravelRequestStatus {
        CREATED, ACCEPTED, REFUSED
}

@Entity
@Table(name = "users")
data class User(
        @Id
        @GeneratedValue
        var id: Long? = null,

        @Column(unique = true)
        val username: String,
        val password: String,
        val enabled: Boolean = true,

        @ElementCollection
        val roles: MutableList<String>

)