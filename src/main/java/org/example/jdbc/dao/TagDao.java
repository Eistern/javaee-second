package org.example.jdbc.dao;

import org.example.Tag;

import java.util.List;

public interface TagDao {
    void saveAll(List<Tag> tags);
    void save(Tag tag);
}
