class Tienda {

    // cualquier producto agregar desde aca con id numeral en caso contrario modificar la clase producto
    private val productos = mutableListOf(
        Producto(1, "Laptop", 850.0, 5),
        Producto(2, "Mouse", 20.0, 10),
        Producto(3, "Teclado", 35.0, 8),
        Producto(4, "Monitor", 200.0, 4),

    )

    private val carrito = mutableListOf<CarritoItem>()
    private val ivaPorcentaje = 0.13

    fun mostrarProductos() {
        println("\n--- LISTA DE PRODUCTOS ---")
        productos.forEach {
            println("${it.id}. ${it.nombre} - Precio: $${"%.2f".format(it.precio)} - Stock: ${it.cantidadDisponible}")
        }
    }

    fun agregarAlCarrito() {
        try {
            mostrarProductos()
            print("\nIngrese el ID del producto: ")
            val id = readln().toInt()

            val producto = productos.find { it.id == id }
            if (producto == null) {
                println("Producto no encontrado.")
                Logger.registrar("Intento de selección con ID inexistente: $id")
                return
            }

            print("Ingrese la cantidad deseada: ")
            val cantidad = readln().toInt()

            if (cantidad <= 0) {
                println("La cantidad debe ser mayor a 0.")
                Logger.registrar("Cantidad inválida ingresada: $cantidad")
                return
            }

            if (cantidad > producto.cantidadDisponible) {
                println("No hay suficiente stock.")
                Logger.registrar(
                    "Stock insuficiente para ${producto.nombre}. Solicitado: $cantidad, Disponible: ${producto.cantidadDisponible}"
                )
                return
            }

            producto.cantidadDisponible -= cantidad

            val itemExistente = carrito.find { it.producto.id == producto.id }
            if (itemExistente != null) {
                itemExistente.cantidad += cantidad
            } else {
                carrito.add(CarritoItem(producto, cantidad))
            }

            println("Producto agregado al carrito.")
            Logger.registrar("Producto agregado: ${producto.nombre}, cantidad: $cantidad")

        } catch (e: Exception) {
            println("Entrada inválida. Debe ingresar números correctos.")
            Logger.registrar("Error al agregar producto: ${e.message}")
        }
    }

    fun eliminarDelCarrito() {
        try {
            if (carrito.isEmpty()) {
                println("\nEl carrito está vacío.")
                return
            }

            println("\n--- PRODUCTOS EN EL CARRITO ---")
            carrito.forEach {
                println("${it.producto.id}. ${it.producto.nombre} - Cantidad en carrito: ${it.cantidad}")
            }

            print("Ingrese el ID del producto que desea eliminar: ")
            val id = readln().toInt()

            val item = carrito.find { it.producto.id == id }
            if (item == null) {
                println("Ese producto no está en el carrito.")
                Logger.registrar("Intento de eliminar producto inexistente del carrito. ID: $id")
                return
            }

            print("Ingrese la cantidad que desea eliminar: ")
            val cantidadEliminar = readln().toInt()

            if (cantidadEliminar <= 0) {
                println("La cantidad debe ser mayor a 0.")
                Logger.registrar("Cantidad inválida al eliminar del carrito: $cantidadEliminar")
                return
            }

            if (cantidadEliminar > item.cantidad) {
                println("No puede eliminar más de lo que tiene en el carrito.")
                Logger.registrar(
                    "Intento de eliminar más cantidad de la existente. Producto: ${item.producto.nombre}, eliminar: $cantidadEliminar, en carrito: ${item.cantidad}"
                )
                return
            }

            item.cantidad -= cantidadEliminar
            item.producto.cantidadDisponible += cantidadEliminar

            if (item.cantidad == 0) {
                carrito.remove(item)
            }

            println("Producto actualizado correctamente en el carrito.")
            Logger.registrar("Producto eliminado del carrito: ${item.producto.nombre}, cantidad: $cantidadEliminar")

        } catch (e: Exception) {
            println("Entrada inválida.")
            Logger.registrar("Error al eliminar del carrito: ${e.message}")
        }
    }

    fun verCarrito() {
        println("\n--- CARRITO DE COMPRAS ---")
        if (carrito.isEmpty()) {
            println("El carrito está vacío.")
            return
        }

        var total = 0.0

        carrito.forEach {
            val subtotal = it.calcularSubtotal()
            total += subtotal

            println(
                "${it.producto.nombre} | " +
                        "Cantidad: ${it.cantidad} | " +
                        "Precio Unitario: $${"%.2f".format(it.producto.precio)} | " +
                        "Total: $${"%.2f".format(subtotal)}"
            )
        }

        println("--------------------------------")
        println("TOTAL GENERAL: $${"%.2f".format(total)}")
    }

    private fun generarFactura(): Factura {
        val subtotal = carrito.sumOf { it.calcularSubtotal() }
        val iva = subtotal * ivaPorcentaje
        val total = subtotal + iva

        return Factura(
            items = carrito.map { CarritoItem(it.producto, it.cantidad) },
            subtotal = subtotal,
            iva = iva,
            total = total
        )
    }

    fun confirmarCompra() {
        if (carrito.isEmpty()) {
            println("\nNo puede confirmar una compra con el carrito vacío.")
            return
        }

        val factura = generarFactura()
        factura.mostrar()
        Logger.registrar("Compra confirmada. Total: $${"%.2f".format(factura.total)}")

        carrito.clear()
        println("\nCompra finalizada. Puede seguir comprando si lo desea.")
    }

    fun menu() {
        var opcion: Int

        do {
            println("\n===== TIENDA EN CONSOLA =====")
            println("1. Mostrar productos")
            println("2. Agregar producto al carrito")
            println("3. Eliminar producto del carrito")
            println("4. Ver carrito")
            println("5. Confirmar compra y generar factura")
            println("6. Salir")
            print("Seleccione una opción: ")

            opcion = try {
                readln().toInt()
            } catch (e: Exception) {
                Logger.registrar("Opción inválida en menú: ${e.message}")
                -1
            }

            when (opcion) {
                1 -> mostrarProductos()
                2 -> agregarAlCarrito()
                3 -> eliminarDelCarrito()
                4 -> verCarrito()
                5 -> confirmarCompra()
                6 -> println("Saliendo del sistema...")
                else -> println("Opción inválida.")
            }

        } while (opcion != 6)
    }
}
