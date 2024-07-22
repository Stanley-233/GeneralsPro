package top.bearingwall.game.util

import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import jxl.Workbook
import jxl.write.Label
import top.bearingwall.game.ClientMain.logger
import java.io.File
import java.io.FileOutputStream


object ReportGenerator {
    fun writePDF() {
        val document = Document()
        val baseFontChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED)
        val fontTitle = Font(baseFontChinese,22f,Font.BOLD)
        val fontChinese = Font(baseFontChinese,12f,Font.NORMAL)
        PdfWriter.getInstance(document, FileOutputStream("report.pdf"))
        document.open()
        val title: Element = Paragraph("将军棋Pro 运行报告",fontTitle)
        document.add(title)
        val powerText: Element = Paragraph("1. 总兵力折线图",fontChinese)
        document.add(powerText)
        val power = Image.getInstance("power.png")
        var originalWidth = power.width
        var originalHeight = power.height
        val pageSize = document.pageSize
        var pageWidth = pageSize.width
        var scaleRatio = (pageWidth-140f) / originalWidth
        var newWidth = originalWidth * scaleRatio
        var newHeight = originalHeight * scaleRatio
        power.scaleToFit(newWidth, newHeight)
        power.alignment = Element.ALIGN_CENTER
        document.add(power)
        val gridText: Element = Paragraph("2. 占领格数折线图",fontChinese)
        document.add(gridText)
        val grid = Image.getInstance("grid.png")
        originalWidth = grid.width
        originalHeight = grid.height
        pageWidth = pageSize.width
        scaleRatio = (pageWidth-140f) / originalWidth
        newWidth = originalWidth * scaleRatio
        newHeight = originalHeight * scaleRatio
        grid.scaleToFit(newWidth, newHeight)
        grid.alignment = Element.ALIGN_CENTER
        document.add(grid)
        document.close()
    }

    fun writeExcel(list: List<ExcelRow>) {
        val file = File("report.xls");
        val wwb = Workbook.createWorkbook(file)
        val ws = wwb.createSheet("报告",0)

        val labelTurn = Label(0,0,"回合(turn)")
        val labelGrid = Label(1,0,"占领格数(grid)")
        val labelPower = Label(2,0,"总兵力(power)")
        ws.addCell(labelTurn)
        ws.addCell(labelGrid)
        ws.addCell(labelPower)

        for (i in 0..list.size - 1) {
            val turn = Label(0,i+1,list.get(i).turn.toString())
            val grid = Label(1,i+1,list.get(i).grid.toString())
            val power = Label(2,i+1,list.get(i).power.toString())
            ws.addCell(turn)
            ws.addCell(grid)
            ws.addCell(power)
        }
        wwb.write()
        logger.info("excel导出成功")
        wwb.close()
    }
}
