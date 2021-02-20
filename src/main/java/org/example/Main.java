package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.jdbc.dao.NodeDao;
import org.example.jdbc.dao.NodeDaoBatchImpl;
import org.example.jdbc.dao.NodeDaoPreparedImpl;
import org.example.jdbc.dao.NodeDaoStringImpl;

import java.io.InputStream;
import java.util.List;

public class Main {
    static final Logger log = LogManager.getLogger("Main");

    public static void main(String[] args) {
        log.info("Hello, World!");

        NodeDao[] implementations = {new NodeDaoBatchImpl(), new NodeDaoStringImpl(), new NodeDaoPreparedImpl()};
        long startTime;

        try {
            InputStream archivedStream = Main.class.getClassLoader().getResourceAsStream("RU-NVS.osm.bz2");
            OsmUnpackerDecorator unpackedStream = new OsmUnpackerDecorator(archivedStream);
            OsmXMLProcessor xmlProcessor = new OsmXMLProcessor(unpackedStream);
            for (NodeDao implementation: implementations) {
                List<Node> nodes = xmlProcessor.unmarshalFirstNodes(100);
                startTime = System.nanoTime();
                implementation.saveAll(nodes);
                log.info("{} took {} ns/element", implementation.getClass(), (System.nanoTime() - startTime) / nodes.size());
            }
            unpackedStream.close();
            archivedStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }
}
