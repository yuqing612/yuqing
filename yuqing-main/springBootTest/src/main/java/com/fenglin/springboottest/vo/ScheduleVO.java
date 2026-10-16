package com.fenglin.springboottest.vo;

import java.util.List;
import java.util.Map;

/**
 * 番剧时间表出参：
 * { weekdays: [周一..周日], today: 今天, items: { 周一: [...], ... } }
 */
public class ScheduleVO {

    /** 固定七个，顺序为周一到周日 */
    private List<String> weekdays;

    /** 今天对应的中文星期，如 周五 */
    private String today;

    /** key 为中文星期，value 为当天按放送时间升序的条目 */
    private Map<String, List<ScheduleItemVO>> items;

    public List<String> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(List<String> weekdays) {
        this.weekdays = weekdays;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public Map<String, List<ScheduleItemVO>> getItems() {
        return items;
    }

    public void setItems(Map<String, List<ScheduleItemVO>> items) {
        this.items = items;
    }
}
