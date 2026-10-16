package com.fenglin.springboottest.controller;

import com.fenglin.springboottest.common.Result;
import com.fenglin.springboottest.entity.Bangumi;
import com.fenglin.springboottest.service.BangumiService;
import com.fenglin.springboottest.vo.BangumiVO;
import com.fenglin.springboottest.vo.ScheduleVO;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 番剧接口：首页 Hero 轮播、番剧新番墙、番剧页、番剧时间表。
 * 除封面外均需登录（走 AuthInterceptor），封面为公开接口，见 WebConfig 白名单。
 */
@RestController
@RequestMapping("/api")
public class BangumiController {

    private final BangumiService bangumiService;

    public BangumiController(BangumiService bangumiService) {
        this.bangumiService = bangumiService;
    }

    /** GET /api/bangumi?category=日漫 —— 不传或传"全部"时返回全部 */
    @GetMapping("/bangumi")
    public Result<List<BangumiVO>> list(@RequestParam(required = false) String category) {
        return Result.ok(bangumiService.list(category));
    }

    /** GET /api/bangumi/categories —— 分类 Tab 数据来源 */
    @GetMapping("/bangumi/categories")
    public Result<List<String>> categories() {
        return Result.ok(bangumiService.categories());
    }

    /** GET /api/bangumi/{id} —— 详情，不存在返回 null */
    @GetMapping("/bangumi/{id}")
    public Result<BangumiVO> detail(@PathVariable Long id) {
        return Result.ok(bangumiService.detail(id));
    }

    /**
     * GET /api/bangumi/{id}/cover —— 直接吐内联 SVG 插画（image/svg+xml）。
     * 浏览器 <img src> 不会带 Authorization，故此接口在 WebConfig 中放行。
     */
    @GetMapping(value = "/bangumi/{id}/cover", produces = "image/svg+xml;charset=UTF-8")
    public ResponseEntity<String> cover(@PathVariable Long id) {
        Bangumi b = bangumiService.findEntity(id);
        if (b == null) {
            return ResponseEntity.notFound().build();
        }
        if (StringUtils.hasText(b.getCoverUrl())) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, b.getCoverUrl()).build();
        }
        if (!StringUtils.hasText(b.getCoverSvg())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .contentType(new MediaType("image", "svg+xml", java.nio.charset.StandardCharsets.UTF_8))
                .body(b.getCoverSvg());
    }

    /** GET /api/schedule —— 首页侧栏"番剧时间表" */
    @GetMapping("/schedule")
    public Result<ScheduleVO> schedule() {
        return Result.ok(bangumiService.schedule());
    }
}
