package org.example.jdbc.dao;

import org.example.Node;

import java.sql.SQLException;
import java.util.List;

public class NodeDaoBatchImpl extends NodeDao {
    @Override
    public void saveAll(List<Node> nodes) {
        try {
            for (Node node : nodes) {
                saveToBatch(node);
            }
            this.preparedInsert.executeBatch();
            for (Node node : nodes) {
                saveConnectionsForNode(node);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public void save(Node node) {
        try {
            saveToBatch(node);
            this.preparedInsert.executeBatch();

            saveConnectionsForNode(node);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    private void saveToBatch(Node node) {
        try {
            prepareInsertStatement(node);
            this.preparedInsert.addBatch();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }
}
