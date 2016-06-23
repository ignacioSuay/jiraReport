package com.suay.jirareport.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import com.suay.jirareport.domain.jira.ReportDTO;
import com.suay.jirareport.service.ReportService;
import com.suay.jirareport.web.rest.dto.LoggerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;

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
        method = RequestMethod.POST, consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    public void createReport(@RequestPart("file") MultipartFile file, @RequestPart("reportDTO") ReportDTO reportDTO) {
        try {
//            if (!file.isEmpty()) {
//                try {
//                    BufferedOutputStream stream = new BufferedOutputStream(
//                        new FileOutputStream(new File(Application.ROOT + "/" + name)));
//                    FileCopyUtils.copy(file.getInputStream(), stream);
//                    stream.close();
//                    redirectAttributes.addFlashAttribute("message",
//                        "You successfully uploaded " + name + "!");
//                }
//                catch (Exception e) {
//                    redirectAttributes.addFlashAttribute("message",
//                        "You failed to upload " + name + " => " + e.getMessage());
//                }
//            }
//            else {
//                redirectAttributes.addFlashAttribute("message",
//                    "You failed to upload " + name + " because the file was empty");
//            }


            reportService.createWordDocument(file.getInputStream(), reportDTO, "tem");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/report/only",
        method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    public void createReport(@RequestBody ReportDTO reportDTO) {

            System.out.println("report in");
//            reportService.createWordDocument(file.getInputStream(), reportDTO, "tem");

    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void getFile(
        HttpServletResponse response) {
        try {
            // get your file as InputStream
            File file = new File("/home/suay/ignacioSuay/jiraReport/simple.docx");
//            FileInputStream is = new FileInputStream(file);
            String mimeType= URLConnection.guessContentTypeFromName(file.getName());
            if(mimeType==null){
                System.out.println("mimetype is not detectable, will take default");
                mimeType = "application/msword";
            }
            System.out.println("mimetype : "+mimeType);

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
