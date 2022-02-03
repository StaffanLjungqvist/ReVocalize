package se.agara.revocalize.adapters

class GameAdapter {

    var level = 0

    fun advanceLevel() : Int{
        if (level < 6) {
            level ++
        }
        return level
    }

}