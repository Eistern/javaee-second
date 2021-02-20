package org.example.jdbc.dao;

import org.example.Tag;
import org.example.jdbs.connection.OSMConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TagDaoImpl implements TagDao {
    private static final String INSERT_STATEMENT = "insert into tags values (?, ?) on conflict do nothing";

    private final PreparedStatement preparedInsert;

    public TagDaoImpl() {
        try {
            Connection connection = OSMConnectionProvider.getConnection();
            this.preparedInsert = connection.prepareStatement(INSERT_STATEMENT);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }

    @Override
    public void saveAll(List<Tag> tags) {
        for (Tag tag : tags) {
            save(tag);
        }
    }

    @Override
    public void save(Tag tag) {
        try {
            this.preparedInsert.setString(1, tag.getK());
            this.preparedInsert.setString(2, tag.getV());
            this.preparedInsert.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException(throwables);
        }
    }
}
