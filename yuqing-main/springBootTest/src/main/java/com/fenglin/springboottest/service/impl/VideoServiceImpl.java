package com.fenglin.springboottest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenglin.springboottest.entity.Video;
import com.fenglin.springboottest.mapper.VideoMapper;
import com.fenglin.springboottest.service.VideoService;
import com.fenglin.springboottest.vo.VideoVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Override
    public List<VideoVO> listDailyRecommend() {
        List<Video> list = this.list(new LambdaQueryWrapper<Video>()
                .eq(Video::getDailyPick, 1)
                .orderByDesc(Video::getPlay));
        if (list.isEmpty()) {
            // 当日无推荐标记时回退为全部视频
            list = this.list(new LambdaQueryWrapper<Video>().orderByDesc(Video::getPlay));
        }
        return list.stream().map(VideoServiceImpl::toVO).collect(Collectors.toList());
    }

    @Override
    public List<VideoVO> listRank() {
        List<Video> list = this.list(new LambdaQueryWrapper<Video>().orderByDesc(Video::getPlay));
        List<VideoVO> vos = list.stream().map(VideoServiceImpl::toVO).collect(Collectors.toList());
        for (int i = 0; i < vos.size(); i++) {
            vos.get(i).setRank(i + 1);
        }
        return vos;
    }

    @Override
    public List<VideoVO> search(String q) {
        String kw = (q == null ? "" : q).trim();
        if (kw.isEmpty()) {
            return listDailyRecommend();
        }
        List<Video> list = this.list(new LambdaQueryWrapper<Video>()
                .like(Video::getTitle, kw)
                .or().like(Video::getUploader, kw)
                .or().like(Video::getTags, kw)
                .orderByDesc(Video::getPlay));
        return list.stream().map(VideoServiceImpl::toVO).collect(Collectors.toList());
    }

    @Override
    public VideoVO getDetail(Long id) {
        Video v = this.getById(id);
        return v == null ? null : toVO(v);
    }

    /** 实体转对外视图对象：tags 逗号串 -> 字符串数组，fav 0/1 -> 布尔 */
    private static VideoVO toVO(Video v) {
        VideoVO vo = new VideoVO();
        vo.setId(v.getId());
        vo.setTitle(v.getTitle());
        vo.setUploader(v.getUploader());
        vo.setViews(v.getViews());
        vo.setPlay(v.getPlay());
        vo.setDanmaku(v.getDanmaku());
        vo.setDuration(v.getDuration());
        vo.setTags(splitTags(v.getTags()));
        vo.setDesc(v.getDescription());
        vo.setFav(v.getFav() != null && v.getFav() == 1);
        vo.setGrad(v.getGrad());
        vo.setCoverUrl(v.getCoverUrl());
        vo.setSrc(v.getSrc());
        return vo;
    }

    private static List<String> splitTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
