package util

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.contentstream.PDFStreamEngine
import org.apache.pdfbox.contentstream.operator.Operator
import org.apache.pdfbox.cos.COSBase
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
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
            "pdf" -> extractPdfWithImageMarkers(file)
            else -> file.readLines()
        }
    } catch (e: Exception) {
        listOf("Erro ao abrir arquivo: ${e.message}")
    }
}

private fun extractPdfWithImageMarkers(file: File): List<String> {
    val result = mutableListOf<String>()

    PDDocument.load(file).use { document ->
        val stripper = object : PDFTextStripper() {
            override fun writeString(text: String, textPositions: List<org.apache.pdfbox.text.TextPosition>) {
                result.add(text)
            }
        }

        val imageDetector = object : PDFStreamEngine() {
            override fun processOperator(operator: Operator, operands: List<COSBase>) {
                super.processOperator(operator, operands)

                if (operator.name == "Do") {
                    val objectName = operands.firstOrNull() as? COSName ?: return
                    val xObject = resources.getXObject(objectName)
                    if (xObject is PDImageXObject) {
                        result.add("[image]")
                    }
                }
            }
        }

        for (pageIndex in 0 until document.numberOfPages) {
            val page = document.getPage(pageIndex)
            imageDetector.processPage(page)
            stripper.startPage = pageIndex + 1
            stripper.endPage = pageIndex + 1
            stripper.getText(document)
        }
    }

    return result
}
