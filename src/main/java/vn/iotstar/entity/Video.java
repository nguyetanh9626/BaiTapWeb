package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Entity Video ánh xạ tới bảng Videos trong SQL Server theo đúng yêu cầu giảng viên.
 * Thuộc tính active dùng kiểu boolean, Description dùng NVARCHAR(MAX) NULL.
 */
@Entity
@Table(name = "Videos")
@NamedQuery(name = "Video.findAll", query = "SELECT v FROM Video v")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "VideoId")
    private String videoId;

    @Column(name = "Active")
    private boolean active;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX) NULL")
    private String description;

    @Column(name = "Poster", columnDefinition = "NVARCHAR(500) NULL")
    private String poster;

    @Column(name = "Title", columnDefinition = "NVARCHAR(500) NULL")
    private String title;

    @Column(name = "Views")
    private int views;

    // Quan hệ hai chiều: Nhiều Video thuộc về một Category
    @ManyToOne
    @JoinColumn(name = "CategoryId")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setActive(int active) {
        this.active = (active == 1);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
