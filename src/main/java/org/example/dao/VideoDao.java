package org.example.dao;

import org.example.model.Video;

import java.util.List;

/**
 * Interface DAO quản lý các thao tác CRUD và truy vấn Video với SQL Server.
 */
public interface VideoDao {

    void insert(Video video);

    void update(Video video);

    void delete(int videoId);

    Video findById(int videoId);

    List<Video> findTop10Newest();

    List<Video> findAll(int offset, int limit);

    int countAll();

    List<Video> findByUserId(int userId, int offset, int limit);

    int countByUserId(int userId);

    void incrementViews(int videoId);

    List<Video> search(String keyword, int offset, int limit);

    int countSearch(String keyword);
}
