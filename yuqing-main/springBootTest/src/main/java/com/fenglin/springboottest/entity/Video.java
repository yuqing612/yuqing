package com.fenglin.springboottest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 视频表实体，对应 video（首页每日推荐数据源）
 */
@TableName("video")
public class Video {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** UP 主 / 投稿方 */
    private String uploader;

    /** 播放量展示文案（如 128.5万） */
    private String views;

    /** 真实播放量（用于排行排序） */
    private Long play;

    /** 弹幕数展示文案（如 3.2万） */
    private String danmaku;

    /** 时长（如 04:12） */
    private String duration;

    /** 标签，逗号分隔（如 治愈,混剪,4K） */
    private String tags;

    /** 简介 */
    private String description;

    /** 是否收藏：1 收藏，0 否 */
    private Integer fav;

    /** 封面渐变 CSS（如 linear-gradient(...)） */
    private String grad;

    /** 封面图 URL（有值时前端优先展示图片，否则用 grad 渐变兜底） */
    private String coverUrl;

    /** 播放地址 */
    private String src;

    /** 是否每日推荐：1 推荐，0 否 */
    private Integer dailyPick;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public Long getPlay() {
        return play;
    }

    public void setPlay(Long play) {
        this.play = play;
    }

    public String getDanmaku() {
        return danmaku;
    }

    public void setDanmaku(String danmaku) {
        this.danmaku = danmaku;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFav() {
        return fav;
    }

    public void setFav(Integer fav) {
        this.fav = fav;
    }

    public String getGrad() {
        return grad;
    }

    public void setGrad(String grad) {
        this.grad = grad;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Integer getDailyPick() {
        return dailyPick;
    }

    public void setDailyPick(Integer dailyPick) {
        this.dailyPick = dailyPick;
    }
}
