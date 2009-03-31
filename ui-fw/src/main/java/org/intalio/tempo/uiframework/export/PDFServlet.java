package org.intalio.tempo.uiframework.export;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PDFServlet extends ExternalTasksServlet {

    public void generateFile(HttpServletRequest request, String token, String user, ServletOutputStream outputStream) throws Exception {

        // sort tasks
        ArrayList<Map<ExportKey, String>> tasks = sortTasks();

        // do nothing if no tasks
        if (tasks.size() < 1)
            return;

        Document document = new Document(PageSize.A4.rotate());
        document.addTitle("Tasks for:" + user);
        PdfWriter.getInstance(document, outputStream);
        
        document.open();
        BaseFont helvetica = BaseFont.createFont("Helvetica", BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        Font font = new Font(helvetica, 9);

        Set<ExportKey> keySet = tasks.get(0).keySet();

        PdfPTable table = new PdfPTable(keySet.size());
        table.setWidthPercentage((float)100);

        // write headers
        if (tasks.size() > 1) {
            for (ExportKey key : keySet) {
                PdfPCell cell = new PdfPCell();
                cell.setBackgroundColor(Color.GRAY);
                cell.setPhrase(new Phrase(key.name().toUpperCase()));
                table.addCell(cell);
            }
        }

        // write entries
        for (Map<ExportKey, String> entry : tasks) {
            Collection<String> en = entry.values();
            for (String value : en) table.addCell(new Phrase(value, font));
        }

        document.add(table);
        document.close();
    }

    @Override
    public String getFileExt() {
        return ".pdf";
    }

    @Override
    public String getFileMimeType() {
        return "application/pdf";
    }

}
