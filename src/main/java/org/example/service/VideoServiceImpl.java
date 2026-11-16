package org.example.service;

import org.example.dao.VideoDao;
import org.example.dao.VideoDaoImpl;
import org.example.model.Video;

import java.util.List;

/**
 * Triển khai VideoService.
 */
public class VideoServiceImpl implements VideoService {

    private final VideoDao videoDao = new VideoDaoImpl();

    @Override
    public void insert(Video video) {
        if (video != null) {
            videoDao.insert(video);
        }
    }

    @Override
    public void update(Video video) {
        if (video != null) {
            videoDao.update(video);
        }
    }

    @Override
    public void delete(int videoId) {
        videoDao.delete(videoId);
    }

    @Override
    public Video findById(int videoId) {
        return videoDao.findById(videoId);
    }

    @Override
    public List<Video> findTop10Newest() {
        return videoDao.findTop10Newest();
    }

    @Override
    public List<Video> findAll(int page, int pageSize) {
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        int offset = (safePage - 1) * safePageSize;
        return videoDao.findAll(offset, safePageSize);
    }

    @Override
    public int countAll() {
        return videoDao.countAll();
    }

    @Override
    public int getTotalPages(int pageSize) {
        int total = countAll();
        int safePageSize = Math.max(1, pageSize);
        return (int) Math.ceil((double) total / safePageSize);
    }

    @Override
    public List<Video> findByUserId(int userId, int page, int pageSize) {
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        int offset = (safePage - 1) * safePageSize;
        return videoDao.findByUserId(userId, offset, safePageSize);
    }

    @Override
    public int countByUserId(int userId) {
        return videoDao.countByUserId(userId);
    }

    @Override
    public void incrementViews(int videoId) {
        videoDao.incrementViews(videoId);
    }

    @Override
    public List<Video> search(String keyword, int page, int pageSize) {
        int safePage = Math.max(1, page);
        int safePageSize = Math.max(1, pageSize);
        int offset = (safePage - 1) * safePageSize;
        return videoDao.search(keyword, offset, safePageSize);
    }

    @Override
    public int countSearch(String keyword) {
        return videoDao.countSearch(keyword);
    }
}
