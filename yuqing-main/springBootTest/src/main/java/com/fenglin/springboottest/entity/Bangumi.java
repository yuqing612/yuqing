package com.fenglin.springboottest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 番剧实体，对应 bangumi 表。
 * 封面三档：cover_url（外部图）> cover_svg（内联插画）> cover_gradient（CSS 渐变兜底）。
 */
@TableName("bangumi")
public class Bangumi {

    /** 主键，101~112，与前端 mock 数据 id 保持一致 */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    /** 内联 SVG 源码（3:4 竖版海报），体积大，不直接下发给列表接口 */
    private String coverSvg;

    private String coverUrl;

    /** CSS 渐变，如 linear-gradient(135deg,#ff8fb1,#c084fc) */
    private String coverGradient;

    private Integer episodes;

    /** 连载中 / 已完结 */
    private String status;

    private BigDecimal score;

    private String studio;

    /** 分类：日漫 / 国漫 / 剧场 / 美漫 */
    private String category;

    /** 标签，斜杠分隔，如 原创/科幻/治愈 */
    private String tags;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

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

    public String getCoverSvg() {
        return coverSvg;
    }

    public void setCoverSvg(String coverSvg) {
        this.coverSvg = coverSvg;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getCoverGradient() {
        return coverGradient;
    }

    public void setCoverGradient(String coverGradient) {
        this.coverGradient = coverGradient;
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

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
