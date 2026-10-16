package com.fenglin.springboottest.vo;

/** 番剧时间表条目：某番剧在某天某时的更新信息 */
public class ScheduleItemVO {

    private String title;

    /** 更新至第几话 */
    private Integer ep;

    /** 放送时间 HH:mm */
    private String time;

    public ScheduleItemVO() {
    }

    public ScheduleItemVO(String title, Integer ep, String time) {
        this.title = title;
        this.ep = ep;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEp() {
        return ep;
    }

    public void setEp(Integer ep) {
        this.ep = ep;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
