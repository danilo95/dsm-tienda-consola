data class Factura(
    val items: List<CarritoItem>,
    val subtotal: Double,
    val iva: Double,
    val total: Double
) {
    fun mostrar() {
        println("\n=========== FACTURA ===========")
        items.forEachIndexed { index, item ->
            val totalProducto = item.calcularSubtotal()
            println(
                "${index + 1}. ${item.producto.nombre} | " +
                        "Cantidad: ${item.cantidad} | " +
                        "Precio Unitario: $${"%.2f".format(item.producto.precio)} | " +
                        "Total: $${"%.2f".format(totalProducto)}"
            )
        }
        println("--------------------------------")
        println("Subtotal: $${"%.2f".format(subtotal)}")
        println("IVA (13%): $${"%.2f".format(iva)}")
        println("TOTAL GENERAL: $${"%.2f".format(total)}")
        println("================================")
    }
}
