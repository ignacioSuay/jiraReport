package com.suay.jirareport;

/**
 * Created by natxo on 16/04/16.
 */

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class UtilTest {

    public File loadFileFromResources(String fileName) throws IOException {
        ClassPathResource cpr = new ClassPathResource(fileName);
        return  cpr.getFile();
    }
}
