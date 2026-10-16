package com.fenglin.springboottest.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 番剧放送时间表，对应 bangumi_schedule 表。
 * weekday：1=周一 … 7=周日（与 java.time.DayOfWeek 对齐）。
 */
@TableName("bangumi_schedule")
public class BangumiSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 逻辑外键，指向 bangumi.id */
    private Long bangumiId;

    private Integer weekday;

    /** 放送时间 HH:mm */
    private String airTime;

    /** 当日更新至第几话 */
    private Integer episode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBangumiId() {
        return bangumiId;
    }

    public void setBangumiId(Long bangumiId) {
        this.bangumiId = bangumiId;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public String getAirTime() {
        return airTime;
    }

    public void setAirTime(String airTime) {
        this.airTime = airTime;
    }

    public Integer getEpisode() {
        return episode;
    }

    public void setEpisode(Integer episode) {
        this.episode = episode;
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
