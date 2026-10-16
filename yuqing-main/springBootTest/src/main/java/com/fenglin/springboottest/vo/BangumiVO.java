package com.fenglin.springboottest.vo;

import java.util.List;

/**
 * 番剧出参视图，字段与前端 mock 数据一一对应，
 * 前端切换到真实接口后页面零改动。
 */
public class BangumiVO {

    private Long id;

    private String title;

    /** CSS 渐变封面，用于 :style="{ background: b.cover }" */
    private String cover;

    /** 插画地址：外部 URL 或 /api/bangumi/{id}/cover；无图时为 null */
    private String illust;

    private Integer episodes;

    private String status;

    /** 评分，前端直接 toFixed(1) */
    private Double score;

    private String studio;

    private String category;

    private List<String> tags;

    private String desc;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIllust() {
        return illust;
    }

    public void setIllust(String illust) {
        this.illust = illust;
    }

    public Integer getEpisodes() {
        return episodes;
    }

    public void setEpisodes(Integer episodes) {
        this.episodes = episodes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
