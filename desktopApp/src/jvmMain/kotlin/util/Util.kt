package util

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

fun listDirectory(dir: File): List<File> {
    val hiddenFilesWindows = listOf("NTUSER.DAT", "ntuser.dat.LOG1", "ntuser.dat.LOG2", "NTUSER.DAT{", "ntuser.ini")
    return dir.listFiles()
        ?.filter {
            !it.name.startsWith(".") && !it.name.endsWith(".ini") &&
                    hiddenFilesWindows.none { prefix -> it.name.startsWith(prefix) }
        }
        ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        ?: emptyList()
}

fun extractTextLines(file: File): List<String> {
    return try {
        when (file.extension.lowercase()) {
            "pdf" -> PDDocument.load(file).use { PDFTextStripper().getText(it).split("\n") }
            else -> file.readLines()
        }
    } catch (e: Exception) {
        listOf("Erro ao abrir arquivo: ${e.message}")
    }
}