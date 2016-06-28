package com.suay.jirareport.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import com.suay.jirareport.domain.jira.ReportDTO;
import com.suay.jirareport.service.ReportService;
import com.suay.jirareport.web.rest.dto.LoggerDTO;
import com.suay.jirareport.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by suay on 5/24/16.
 */
@RestController
@RequestMapping("/api")
public class ReportResource {

    private final Logger log = LoggerFactory.getLogger(ReportResource.class);


    @Inject
    ReportService reportService;

    @RequestMapping(value = "/report",
        method = RequestMethod.POST,
        consumes = {"multipart/form-data"},
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    public ResponseEntity<Void> createReport(@RequestPart("file") MultipartFile file, @RequestPart("reportDTO") ReportDTO reportDTO) {

        String outputFile = null;
        try {

            outputFile = reportService.createWordDocument(file.getInputStream(), reportDTO, "template.docx");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        final String finalOutputFile = outputFile;
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("newReport", finalOutputFile)).build();

    }

    @RequestMapping(value = "/report/only",
        method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    public void createReport(@RequestBody ReportDTO reportDTO) {

            System.out.println("report in");
//            reportService.createWordDocument(file.getInputStream(), reportDTO, "tem");

    }

    @RequestMapping(value = "/download/{filename}", method = RequestMethod.GET)
    public void getFile(@PathVariable String filename,
        HttpServletResponse response) {
        try {
            // get your file as InputStream
            File file = new File("/home/suay/ignacioSuay/jiraReport/files/"+filename);
            String mimeType= "application/msword";
            response.setContentType(mimeType);

            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
            response.setContentLength((int)file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, response.getOutputStream());

        } catch (IOException ex) {
            log.info("Error writing file to output stream. Filename was '{}'", ex);
            throw new RuntimeException("IOError writing file to output stream");
        }

    }


}
