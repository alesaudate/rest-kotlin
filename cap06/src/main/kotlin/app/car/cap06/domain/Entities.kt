package app.car.cap06.domain

import java.time.LocalDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
data class Driver(
        @Id
        @GeneratedValue
        var id: Long? = null,
        val name: String,
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