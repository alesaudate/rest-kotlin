package app.car.cap05.domain

import org.springframework.data.jpa.repository.JpaRepository

interface DriverRepository : JpaRepository<Driver, Long>

interface PassengerRepository: JpaRepository<Passenger, Long>

interface TravelRequestRepository: JpaRepository<TravelRequest, Long> {
    fun findByStatus(status: TravelRequestStatus): List<TravelRequest>
}