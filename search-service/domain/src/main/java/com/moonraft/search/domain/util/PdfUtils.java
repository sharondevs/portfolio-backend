package com.moonraft.search.domain.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfUtils {
    private PdfUtils() {
    }
    public static String[] parsePdf(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            return null;
        }

        try (PDDocument document = PDDocument.load(file)) {
            int pageCount = document.getNumberOfPages();
            String[] pages = new String[pageCount];
            PDFTextStripper stripper = new PDFTextStripper();

            for (int i = 0; i < pageCount; i++) {
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                pages[i] = stripper.getText(document).replaceAll("[^a-zA-Z_0-9\\s]", "");
            }
            document.close();
            return pages;
        } catch (IOException e) {
            System.out.println("Error occurred while parsing PDF: " + e.getMessage());
            return null;
        }
    }
}
