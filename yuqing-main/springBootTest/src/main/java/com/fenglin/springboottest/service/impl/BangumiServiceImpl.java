package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.entity.Bangumi;
import com.fenglin.springboottest.entity.BangumiSchedule;
import com.fenglin.springboottest.mapper.BangumiMapper;
import com.fenglin.springboottest.mapper.BangumiScheduleMapper;
import com.fenglin.springboottest.service.BangumiService;
import com.fenglin.springboottest.vo.BangumiVO;
import com.fenglin.springboottest.vo.ScheduleItemVO;
import com.fenglin.springboottest.vo.ScheduleVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BangumiServiceImpl extends ServiceImpl<BangumiMapper, Bangumi> implements BangumiService {

    /** 周一到周日，与 bangumi_schedule.weekday（1~7）一一对应 */
    private static final List<String> WEEKDAYS = List.of("周一", "周二", "周三", "周四", "周五", "周六", "周日");

    /** 分类"全部"是前端 Tab 的占位值，不作为过滤条件 */
    private static final String ALL = "全部";

    /** 标签分隔符：支持 / 、 与 , 三种写法 */
    private static final String TAG_SPLIT = "[/、,]";

    private final BangumiScheduleMapper scheduleMapper;

    public BangumiServiceImpl(BangumiScheduleMapper scheduleMapper) {
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public List<BangumiVO> list(String category) {
        LambdaQueryWrapper<Bangumi> q = new LambdaQueryWrapper<Bangumi>()
                .orderByDesc(Bangumi::getScore)
                .orderByAsc(Bangumi::getId);
        if (StringUtils.hasText(category) && !ALL.equals(category.trim())) {
            q.eq(Bangumi::getCategory, category.trim());
        }
        return this.list(q).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public BangumiVO detail(Long id) {
        Bangumi b = this.getById(id);
        return b == null ? null : toVO(b);
    }

    @Override
    public Bangumi findEntity(Long id) {
        return this.getById(id);
    }

    @Override
    public List<String> categories() {
        // 只查分类列，避免把 cover_svg 大字段捞出来
        // 只查分类列，避免把 cover_svg 大字段捞出来；按 id 升序保证 Tab 顺序稳定
        List<Bangumi> rows = this.list(new LambdaQueryWrapper<Bangumi>()
                .select(Bangumi::getCategory)
                .isNotNull(Bangumi::getCategory)
                .orderByAsc(Bangumi::getId));
        return rows.stream()
                .map(Bangumi::getCategory)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleVO schedule() {
        // 时间表按 周几 + 放送时间 升序
        List<BangumiSchedule> rows = scheduleMapper.selectList(new LambdaQueryWrapper<BangumiSchedule>()
                .orderByAsc(BangumiSchedule::getWeekday, BangumiSchedule::getAirTime));

        // 轻量取标题，不查 cover_svg
        Map<Long, String> titles = this.list(new LambdaQueryWrapper<Bangumi>()
                        .select(Bangumi::getId, Bangumi::getTitle))
                .stream()
                .collect(Collectors.toMap(Bangumi::getId, Bangumi::getTitle, (a, b) -> a));

        Map<String, List<ScheduleItemVO>> items = new LinkedHashMap<>();
        for (String day : WEEKDAYS) {
            items.put(day, new ArrayList<>());
        }
        for (BangumiSchedule row : rows) {
            int idx = row.getWeekday() == null ? 0 : row.getWeekday() - 1;
            if (idx < 0 || idx >= WEEKDAYS.size()) {
                continue;
            }
            items.get(WEEKDAYS.get(idx)).add(new ScheduleItemVO(
                    titles.get(row.getBangumiId()),
                    row.getEpisode(),
                    row.getAirTime()));
        }

        ScheduleVO vo = new ScheduleVO();
        vo.setWeekdays(WEEKDAYS);
        vo.setToday(WEEKDAYS.get(LocalDate.now().getDayOfWeek().getValue() - 1));
        vo.setItems(items);
        return vo;
    }

    /* ================== 实体 → VO ================== */

    private BangumiVO toVO(Bangumi b) {
        BangumiVO vo = new BangumiVO();
        vo.setId(b.getId());
        vo.setTitle(b.getTitle());
        vo.setCover(b.getCoverGradient());
        vo.setIllust(resolveIllust(b));
        vo.setEpisodes(b.getEpisodes());
        vo.setStatus(b.getStatus());
        vo.setScore(b.getScore() == null ? 0.0 : b.getScore().doubleValue());
        vo.setStudio(b.getStudio());
        vo.setCategory(b.getCategory());
        vo.setTags(splitTags(b.getTags()));
        vo.setDesc(b.getDescription());
        return vo;
    }

    /** 取图优先级：外部 URL > 内联 SVG 接口 > 无（前端回落到渐变封面） */
    private String resolveIllust(Bangumi b) {
        if (StringUtils.hasText(b.getCoverUrl())) {
            return b.getCoverUrl();
        }
        if (StringUtils.hasText(b.getCoverSvg())) {
            return "/api/bangumi/" + b.getId() + "/cover";
        }
        return null;
    }

    private List<String> splitTags(String raw) {
        if (!StringUtils.hasText(raw)) {
            return List.of();
        }
        return Arrays.stream(raw.split(TAG_SPLIT))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}
