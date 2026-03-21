data class CarritoItem(
    val producto: Producto,
    var cantidad: Int
){
    fun calcularSubtotal(): Double {
        return producto.precio * cantidad
    }
}