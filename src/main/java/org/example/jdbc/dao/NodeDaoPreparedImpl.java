package org.example.jdbc.dao;

import org.example.Node;

import java.util.List;

public class NodeDaoPreparedImpl extends NodeDao {
    @Override
    public void saveAll(List<Node> nodes) {
        for (Node node: nodes) {
            save(node);
        }
    }
}
