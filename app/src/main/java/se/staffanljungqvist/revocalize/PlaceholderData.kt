package se.staffanljungqvist.revocalize

object Colors {
    val colors = listOf(
        "#FF4BF8",
        "#4BEBFF",
        "#38FF75",
        "#FFF384",
        "#DDA9FF",
        "#FF4B7C",
        "#9FFF94",
        "#9FFF94",

    )
}

enum class PowerUp(name : String, val RGB: String) {
    REMOVE("REMOVE","#FF4BF8"),
    TRY("TRY", "#38FF75"),
    CLICK("CLICK", "#4BEBFF")
}
