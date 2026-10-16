package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.service.VideoService;
import com.fenglin.springboottest.vo.VideoVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 视频接口，对接 anime-login-vue 首页每日推荐 / 热播排行 / 搜索 / 详情。
 * 所有 /api/** 路径默认受 AuthInterceptor 保护（需登录令牌），与首页需登录的设定一致。
 */
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /** 首页每日推荐 */
    @GetMapping
    public Result<List<VideoVO>> listDailyRecommend() {
        return Result.ok(videoService.listDailyRecommend());
    }

    /** 热播排行（带 rank 序号） */
    @GetMapping("/rank")
    public Result<List<VideoVO>> listRank() {
        return Result.ok(videoService.listRank());
    }

    /** 关键词搜索 */
    @GetMapping("/search")
    public Result<List<VideoVO>> search(@RequestParam("q") String q) {
        return Result.ok(videoService.search(q));
    }

    /** 单个视频详情 */
    @GetMapping("/{id}")
    public Result<VideoVO> getDetail(@PathVariable("id") Long id) {
        VideoVO vo = videoService.getDetail(id);
        if (vo == null) {
            return Result.fail("视频不存在");
        }
        return Result.ok(vo);
    }
}
