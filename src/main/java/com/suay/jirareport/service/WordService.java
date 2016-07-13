package com.suay.jirareport.service;

import com.suay.jirareport.domain.jira.FieldName;
import com.suay.jirareport.domain.jira.Section;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by suay on 7/13/16.
 */
@Service
public class WordService {


    public static void addColumnsToTable(XWPFTable table, Section section){
        int i = 0;
        for(FieldName column: section.getTotalColumns()){
            table.getRow(0).getCell(i).setText(column.getColumnName());
            i++;
        }
    }

    public static void changeTitle(XWPFDocument doc, String title) {
        replaceText(doc, "templateTitle", title);
    }

    public static void changeAuthors(XWPFDocument doc, String authors) {
        replaceText(doc, "templateAuthors", authors);
    }

    public static void replaceText(XWPFDocument doc, String textToFind, String textToReplace) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(textToFind) && textToReplace != null) {
                        text = text.replace(textToFind, textToReplace);
                        r.setText(text, 0);
                    }
                }
            }
        }
    }


    public static void addSection(XWPFDocument doc, String title){
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Heading1");
        XWPFRun r1 = p.createRun();
        r1.setText(title);
        r1.addBreak();
    }

    public static void addSubSection(XWPFDocument doc, String title){
        XWPFParagraph p = doc.createParagraph();
        p.setStyle("Heading2");
        XWPFRun r1 = p.createRun();
        r1.setText(title);
    }
}
