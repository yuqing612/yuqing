package com.fenglin.springboottest.service;

import com.fenglin.springboottest.entity.Bangumi;
import com.fenglin.springboottest.vo.BangumiVO;
import com.fenglin.springboottest.vo.ScheduleVO;

import java.util.List;

/** 番剧业务接口：首页新番墙、Hero 轮播、番剧页、番剧时间表共用 */
public interface BangumiService {

    /**
     * 番剧列表
     * @param category 分类过滤，为空或"全部"时不过滤
     */
    List<BangumiVO> list(String category);

    /** 番剧详情，不存在返回 null */
    BangumiVO detail(Long id);

    /** 取原始实体（含 cover_svg），供封面接口使用 */
    Bangumi findEntity(Long id);

    /** 全部分类，按表中出现顺序去重 */
    List<String> categories();

    /** 本周放送时间表 */
    ScheduleVO schedule();
}
