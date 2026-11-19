package org.example.service;

import org.example.model.Video;

import java.util.List;

/**
 * Service Layer cung cấp các nghiệp vụ liên quan đến Video.
 */
public interface VideoService {

    void insert(Video video);

    void update(Video video);

    void delete(int videoId);

    Video findById(int videoId);

    Video findByVideoPath(String videoPath);

    List<Video> findTop10Newest();

    List<Video> findAll(int page, int pageSize);

    int countAll();

    int getTotalPages(int pageSize);

    List<Video> findByUserId(int userId, int page, int pageSize);

    int countByUserId(int userId);

    void incrementViews(int videoId);

    List<Video> search(String keyword, int page, int pageSize);

    int countSearch(String keyword);
}
