package org.example.jdbc.dao;

import org.example.Node;
import org.example.Tag;
import org.example.jdbs.connection.OSMConnectionProvider;

import java.sql.*;
import java.util.List;

public abstract class NodeDao {
    protected static final String INSERT_STATEMENT = "insert into nodes values (?, ?, ?, ?, ?, ?, ?, ?, ?) on conflict do nothing";
    protected static final String INSERT_CONNECTION_STATEMENT = "insert into node_tags values (?, ?) on conflict do nothing";

    protected final PreparedStatement preparedInsert;
    protected final PreparedStatement preparedConnectionInsert;

    protected final TagDao tagDao = new TagDaoImpl();
    protected final Connection connection;

    protected NodeDao() {
        try {
            this.connection = OSMConnectionProvider.getConnection();
            this.preparedInsert = connection.prepareStatement(INSERT_STATEMENT);
            this.preparedConnectionInsert = connection.prepareStatement(INSERT_CONNECTION_STATEMENT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    public abstract void saveAll(List<Node> nodes);

    public void save(Node node) {
        try {
            prepareInsertStatement(node);
            this.preparedInsert.execute();

            saveConnectionsForNode(node);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    protected void prepareInsertStatement(Node node) throws SQLException {
        this.preparedInsert.setLong(1, node.getId().longValue());
        setCheckedDouble(preparedInsert, 2, node.getLat());
        setCheckedDouble(preparedInsert, 3, node.getLon());
        this.preparedInsert.setString(4, node.getUser());
        setCheckedLong(this.preparedInsert, 5, node.getUid().longValue());
        setCheckedBoolean(this.preparedInsert, 6, node.isVisible());
        setCheckedLong(this.preparedInsert, 7, node.getVersion().longValue());
        setCheckedLong(this.preparedInsert, 8, node.getChangeset().longValue());
        this.preparedInsert.setDate(9, new Date(node.getTimestamp().toGregorianCalendar().getTimeInMillis()));
    }

    private void setCheckedLong(PreparedStatement preparedStatement, int index, Long value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.BIGINT);
        } else {
            preparedStatement.setLong(index, value);
        }
    }

    private void setCheckedDouble(PreparedStatement preparedStatement, int index, Double value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.DOUBLE);
        } else {
            preparedStatement.setDouble(index, value);
        }
    }

    private void setCheckedBoolean(PreparedStatement preparedStatement, int index, Boolean value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.BOOLEAN);
        } else {
            preparedStatement.setBoolean(index, value);
        }
    }

    protected void saveConnectionsForNode(Node node) {
        try {
            this.tagDao.saveAll(node.getTag());
            for (Tag tag : node.getTag()) {
                this.preparedConnectionInsert.setLong(1, node.getId().longValue());
                this.preparedConnectionInsert.setString(2, tag.getK());
                this.preparedConnectionInsert.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }
}
