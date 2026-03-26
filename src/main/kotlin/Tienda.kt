class Tienda {
    // cualquier producto agregar desde aca con id numeral en caso contrario modificar la clase producto
    private val productos = mutableListOf(
        Producto(1, "Laptop", 850.0, 5),
        Producto(2, "Mouse", 20.0, 10),
        Producto(3, "Teclado", 35.0, 8),
        Producto(4, "Monitor", 200.0, 4)
    )

    private val carrito = mutableListOf<CarritoItem>()

    fun mostrarProductos() {
        println("\n--- LISTA DE PRODUCTOS ---")
        productos.forEach {
            println("${it.id}. ${it.nombre} - $${it.precio} - Stock: ${it.cantidadDisponible}")
        }
    }

    fun agregarAlCarrito() {
        try {
            print("Ingrese el ID del producto: ")
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
                Logger.registrar("Stock insuficiente para ${producto.nombre}. Solicitado: $cantidad, Disponible: ${producto.cantidadDisponible}")
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

    fun verCarrito() {
        println("\n--- CARRITO DE COMPRAS ---")
        if (carrito.isEmpty()) {
            println("El carrito está vacío.")
            Logger.registrar("Se consultó el carrito, pero está vacío.")
            return
        }

        var total = 0.0
        carrito.forEach {
            val subtotal = it.producto.precio * it.cantidad
            total += subtotal
            println("${it.producto.nombre} - Cantidad: ${it.cantidad} - Subtotal: $$subtotal")
        }
        println("TOTAL: $$total")
        Logger.registrar("Se consultó el carrito. Total actual: $$total")
    }

    fun menu() {
        var opcion: Int

        do {
            println("\n===== TIENDA EN CONSOLA =====")
            println("1. Mostrar productos")
            println("2. Agregar producto al carrito")
            println("3. Ver carrito")
            println("4. Salir")
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
                3 -> verCarrito()
                4 -> println("Saliendo del sistema...")
                else -> println("Opción inválida.")
            }

        } while (opcion != 4)
    }
}