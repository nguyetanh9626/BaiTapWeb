package org.example.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model Video ánh xạ bảng dbo.Videos trong database baitapweb (quan hệ 1-N với User qua userId).
 */
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    private int videoId;
    private String title;
    private String description;
    private String videoPath;
    private String thumbnail;
    private Timestamp uploadDate;
    private int views;
    private int status = 1;
    private int userId;

    // Thuộc tính tiện ích hiển thị thông tin người đăng (JOIN User)
    private String uploaderName;
    private String uploaderUsername;

    public Video() {
    }

    public Video(int videoId, String title, String description, String videoPath, String thumbnail,
                 Timestamp uploadDate, int views, int status, int userId) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.videoPath = videoPath;
        this.thumbnail = thumbnail;
        this.uploadDate = uploadDate;
        this.views = views;
        this.status = status;
        this.userId = userId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Timestamp getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderUsername() {
        return uploaderUsername;
    }

    public void setUploaderUsername(String uploaderUsername) {
        this.uploaderUsername = uploaderUsername;
    }
}
