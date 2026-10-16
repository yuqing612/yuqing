package com.fenglin.springboottest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenglin.springboottest.entity.Video;
import com.fenglin.springboottest.vo.VideoVO;

import java.util.List;

/**
 * 视频服务：首页每日推荐、热播排行、搜索、详情。
 */
public interface VideoService extends IService<Video> {

    /**
     * 首页每日推荐列表：取 daily_pick=1 的视频，按真实播放量降序；
     * 若当日无任何推荐标记，则回退为全部视频（按播放量降序）。
     */
    List<VideoVO> listDailyRecommend();

    /**
     * 热播排行：全部视频按真实播放量降序，并附 rank 序号（1 起）。
     */
    List<VideoVO> listRank();

    /**
     * 关键词搜索：匹配 标题 / UP主 / 标签，模糊查询后按播放量降序。
     */
    List<VideoVO> search(String q);

    /**
     * 单个视频详情，不存在返回 null。
     */
    VideoVO getDetail(Long id);
}
