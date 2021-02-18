package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

public class Main {
    static final Logger log = LogManager.getLogger("Main");

    public static void main(String[] args) {
        log.info("Hello, World!");

        try {
            InputStream archivedStream = Main.class.getClassLoader().getResourceAsStream("RU-NVS.osm.bz2");
            OsmUnpackerDecorator unpackedStream = new OsmUnpackerDecorator(archivedStream);
            OsmXMLProcessor xmlProcessor = new OsmXMLProcessor(unpackedStream);
            xmlProcessor.unmarshalNodes();
            unpackedStream.close();
            archivedStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }
}
