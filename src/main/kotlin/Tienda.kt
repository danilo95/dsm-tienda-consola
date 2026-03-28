import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Tienda {
    private val productos = mutableListOf(
        Producto(1, "Laptop", 850.0, 5),
        Producto(2, "Mouse", 20.0, 10),
        Producto(3, "Teclado", 35.0, 8),
        Producto(4, "Monitor", 200.0, 4)
    )

    private val carrito = mutableListOf<CarritoItem>()

    fun mostrarEncabezado() {
        println()
        println("====================================================")
        println("=            SISTEMA DE TIENDA EN CONSOLA          =")
        println("=              Proyecto DSM - Kotlin               =")
        println("====================================================")
    }

    fun mostrarSeparador(titulo: String) {
        println()
        println("----------------------------------------------------")
        println(" $titulo")
        println("----------------------------------------------------")
    }

    fun mostrarProductos() {
        mostrarSeparador("LISTA DE PRODUCTOS")
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
                Logger.registrar("Intento de seleccion con ID inexistente: $id")
                return
            }

            print("Ingrese la cantidad deseada: ")
            val cantidad = readln().toInt()

            if (cantidad <= 0) {
                println("La cantidad debe ser mayor a 0.")
                Logger.registrar("Cantidad invalida ingresada: $cantidad")
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
            println("Entrada invalida. Debe ingresar numeros correctos.")
            Logger.registrar("Error al agregar producto: ${e.message}")
        }
    }

    fun verCarrito() {
        mostrarSeparador("CARRITO DE COMPRAS")
        if (carrito.isEmpty()) {
            println("El carrito esta vacio.")
            return
        }

        var total = 0.0
        carrito.forEach {
            val subtotal = it.producto.precio * it.cantidad
            total += subtotal
            println("${it.producto.id}. ${it.producto.nombre} - Cantidad: ${it.cantidad} - Subtotal: $$subtotal")
        }
        println("TOTAL: $$total")
    }

    fun quitarUnidadesDelCarrito() {
        mostrarSeparador("QUITAR UNIDADES DEL CARRITO")
        if (carrito.isEmpty()) {
            println("El carrito esta vacio.")
            return
        }

        verCarrito()

        try {
            print("Ingrese el ID del producto al que desea quitar unidades: ")
            val id = readln().toInt()

            val item = carrito.find { it.producto.id == id }

            if (item == null) {
                println("Ese producto no esta en el carrito.")
                Logger.registrar("Intento de quitar unidades de un producto no existente en carrito: $id")
                return
            }

            print("Ingrese cuantas unidades desea quitar: ")
            val cantidadAQuitar = readln().toInt()

            if (cantidadAQuitar <= 0) {
                println("La cantidad a quitar debe ser mayor a 0.")
                Logger.registrar("Cantidad invalida para quitar unidades: $cantidadAQuitar")
                return
            }

            if (cantidadAQuitar > item.cantidad) {
                println("No puede quitar mas unidades de las que tiene en el carrito.")
                Logger.registrar("Intento de quitar mas unidades de las disponibles en carrito. Producto: ${item.producto.nombre}, tiene: ${item.cantidad}, intento quitar: $cantidadAQuitar")
                return
            }

            item.cantidad -= cantidadAQuitar
            item.producto.cantidadDisponible += cantidadAQuitar

            if (item.cantidad == 0) {
                carrito.remove(item)
                println("Se quitaron todas las unidades y el producto fue eliminado del carrito.")
                Logger.registrar("Se eliminaron todas las unidades de ${item.producto.nombre} desde la opcion de quitar unidades")
            } else {
                println("Unidades quitadas correctamente.")
                Logger.registrar("Se quitaron $cantidadAQuitar unidades de ${item.producto.nombre} del carrito")
            }

        } catch (e: Exception) {
            println("Entrada invalida. Debe ingresar valores numericos.")
            Logger.registrar("Error al quitar unidades del carrito: ${e.message}")
        }
    }

    fun eliminarProductoCompletoDelCarrito() {
        mostrarSeparador("ELIMINAR PRODUCTO COMPLETO DEL CARRITO")
        if (carrito.isEmpty()) {
            println("El carrito esta vacio.")
            return
        }

        verCarrito()

        try {
            print("Ingrese el ID del producto que desea eliminar completamente: ")
            val id = readln().toInt()

            val item = carrito.find { it.producto.id == id }

            if (item == null) {
                println("Ese producto no esta en el carrito.")
                Logger.registrar("Intento de eliminar producto completo no existente en carrito: $id")
                return
            }

            item.producto.cantidadDisponible += item.cantidad
            carrito.remove(item)

            println("Producto eliminado completamente del carrito.")
            Logger.registrar("Producto eliminado completamente del carrito: ${item.producto.nombre}, cantidad devuelta al stock: ${item.cantidad}")

        } catch (e: Exception) {
            println("Entrada invalida. Debe ingresar un ID numerico.")
            Logger.registrar("Error al eliminar producto completo del carrito: ${e.message}")
        }
    }

    fun confirmarCompra() {
        mostrarSeparador("FACTURA DE COMPRA")
        if (carrito.isEmpty()) {
            println("No se puede confirmar la compra porque el carrito esta vacio.")
            return
        }

        val fecha = LocalDateTime.now()
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val numeroFactura = "FAC-${System.currentTimeMillis()}"

        var subtotalGeneral = 0.0

        println("Numero de factura: $numeroFactura")
        println("Fecha: ${fecha.format(formato)}")
        println()
        println("Detalle de compra:")

        carrito.forEach {
            val subtotalProducto = it.producto.precio * it.cantidad
            subtotalGeneral += subtotalProducto
            println("- ${it.producto.nombre} | Cantidad: ${it.cantidad} | Precio unitario: $${it.producto.precio} | Subtotal: $$subtotalProducto")
        }

        val iva = subtotalGeneral * 0.13
        val totalFinal = subtotalGeneral + iva

        println()
        println("Subtotal: $$subtotalGeneral")
        println("IVA (13%): $$iva")
        println("TOTAL A PAGAR: $$totalFinal")
        println()

        print("Desea confirmar la compra? (S/N): ")
        val confirmacion = readln().trim().uppercase()

        if (confirmacion == "S") {
            carrito.clear()
            println()
            println("Compra confirmada exitosamente.")
            println("Factura generada correctamente en consola.")
            Logger.registrar("Compra confirmada. Factura: $numeroFactura, Subtotal: $subtotalGeneral, IVA: $iva, Total: $totalFinal")
        } else {
            println("La compra fue cancelada.")
            Logger.registrar("Compra cancelada por el usuario.")
        }
    }

    fun menu() {
        var opcion: Int

        do {
            mostrarEncabezado()
            println("1. Mostrar productos")
            println("2. Agregar producto al carrito")
            println("3. Ver carrito")
            println("4. Quitar unidades de un producto del carrito")
            println("5. Eliminar producto completo del carrito")
            println("6. Confirmar compra y generar factura")
            println("7. Salir")
            print("Seleccione una opcion: ")

            opcion = try {
                readln().toInt()
            } catch (e: Exception) {
                Logger.registrar("Opcion invalida en menu: ${e.message}")
                -1
            }

            when (opcion) {
                1 -> mostrarProductos()
                2 -> agregarAlCarrito()
                3 -> verCarrito()
                4 -> quitarUnidadesDelCarrito()
                5 -> eliminarProductoCompletoDelCarrito()
                6 -> confirmarCompra()
                7 -> println("Saliendo del sistema...")
                else -> println("Opcion invalida.")
            }

        } while (opcion != 7)
    }
}