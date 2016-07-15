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
import org.springframework.core.io.InputStreamResource;
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
import java.util.List;
import java.util.Map;

/**
 * Created by suay on 5/24/16.
 */
@RestController
@RequestMapping("/api")
public class ReportResource {

    private final Logger log = LoggerFactory.getLogger(ReportResource.class);

    public final static String UPLOAD_FILE = "/var/jiraReport/uploadFiles/";

    public final static String REPORT_PATH = "/var/jiraReport/reports/";


    @Inject
    ReportService reportService;

    @RequestMapping(value = "/report",
        method = RequestMethod.POST,
        consumes = {"multipart/form-data"},
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    public ResponseEntity<Void> createReport(@RequestPart("uuid") String uuid, @RequestPart("reportDTO") ReportDTO reportDTO) {

        String outputFile = null;
        try {
            File file = new File(UPLOAD_FILE + uuid);
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            outputFile = reportService.createWordDocument(inputStream, reportDTO, "template.docx");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        final String finalOutputFile = outputFile;
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("newReport", finalOutputFile)).build();
    }



    @RequestMapping(value = "/download/{filename}", method = RequestMethod.GET)
    public void getFile(@PathVariable String filename,
        HttpServletResponse response) {
        try {
            // get your file as InputStream
            File file = new File(REPORT_PATH + filename+".docx");
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

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/upload")
    public void upload(@RequestParam("file") MultipartFile file, @RequestParam("uuid") String uuid ) throws IOException {
        log.info("loading file with uuid ");
        File newFile = new File(UPLOAD_FILE + uuid);
        file.transferTo(newFile);
    }

}
