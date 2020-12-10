package app.car.cap08.interfaces.incoming.errorhandling

data class ErrorData(
    val message: String
)

data class ErrorResponse(
    val errors: List<ErrorData>
)