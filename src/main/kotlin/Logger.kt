import java.io.File
import java.time.LocalDateTime

object Logger {
    private val archivo = File("log.txt")

    fun registrar(mensaje: String) {
        val texto = "[${LocalDateTime.now()}] $mensaje\n"
        archivo.appendText(texto)
    }
}