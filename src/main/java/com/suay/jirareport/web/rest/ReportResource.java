package com.suay.jirareport.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import com.suay.jirareport.domain.jira.ReportDTO;
import com.suay.jirareport.service.ReportService;
import com.suay.jirareport.web.rest.dto.LoggerDTO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by suay on 5/24/16.
 */
@RestController
@RequestMapping("/api")
public class ReportResource {

    @Inject
    ReportService reportService;

    @RequestMapping(value = "/report",
        method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    public void createReport(@RequestBody MultipartFile file, @RequestBody ReportDTO reportDTO) {
        try {
            reportService.createWordDocument(file.getInputStream(), reportDTO, "tem");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
