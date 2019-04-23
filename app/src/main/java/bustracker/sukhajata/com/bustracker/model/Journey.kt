package bustracker.sukhajata.com.bustracker.model

data class Journey(val id: String, val from: String, val to: String) {
    companion object {
        val ITEMS: MutableList<Journey> = mutableListOf()
    }

    override fun toString(): String = from + " to " + to

}
