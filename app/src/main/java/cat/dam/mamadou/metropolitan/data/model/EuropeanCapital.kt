package cat.dam.mamadou.metropolitan.data.model

data class EuropeanCapital(
    val country: String,
    val capital: String,
    val latitude: Double,
    val longitude: Double,
    val countryCode: String
) {
    companion object {
        val capitals = listOf(
            EuropeanCapital("Germany", "Berlin", 52.5200, 13.4050, "DE"),
            EuropeanCapital("Spain", "Madrid", 40.4168, -3.7038, "ES"),
            EuropeanCapital("France", "Paris", 48.8566, 2.3522, "FR"),
            EuropeanCapital("Italy", "Rome", 41.9028, 12.4964, "IT"),
            EuropeanCapital("Netherlands", "Amsterdam", 52.3676, 4.9041, "NL"),
            EuropeanCapital("Belgium", "Brussels", 50.8503, 4.3517, "BE"),
            EuropeanCapital("Austria", "Vienna", 48.2082, 16.3738, "AT"),
            EuropeanCapital("Switzerland", "Bern", 46.9481, 7.4474, "CH"),
            EuropeanCapital("Sweden", "Stockholm", 59.3293, 18.0686, "SE")
        )
    }
}