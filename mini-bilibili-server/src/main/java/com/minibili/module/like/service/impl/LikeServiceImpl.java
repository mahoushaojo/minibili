package com.minibili.module.like.service.impl;

import com.minibili.common.exception.BusinessException;
import com.minibili.common.id.IdGenerator;
import com.minibili.module.collect.domain.entity.Collect;
import com.minibili.module.like.domain.entity.Likes;
import com.minibili.module.like.mapper.LikeMapper;
import com.minibili.module.like.service.LikeService;
import com.minibili.module.video.domain.enums.VideoCountType;
import com.minibili.module.video.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private IdGenerator idGenerator;

    @Override
    @Transactional
    public Boolean setLike(Long userId, Long vid, Integer type){
        if (vid == null){
            throw new BusinessException(40900,"视频id不能为空");
        }
        if (type == null){
            throw new BusinessException(40900,"type不能为空");
        }
        int rows;
        if (type == 1) {
            Likes like = likeMapper.getLike(userId, vid);
            if (like != null){
                throw new BusinessException(40900,"已点赞过该视频");
            }
            Likes likes = new Likes();
            likes.setId(idGenerator.nextId());
            likes.setUserId(userId);
            likes.setVideoId(vid);
            rows = likeMapper.addLike(likes);
            if (rows != 1) {
                throw new BusinessException(50000, "点赞失败");
            }
            // 视频点赞数量+1
            videoMapper.updateCount(vid, VideoCountType.LIKE, 1);
        } else if (type == 0) {
            rows = likeMapper.deleteLike(userId, vid);
            if (rows != 1) {
                throw new BusinessException(40900, "当前视频未点赞");
            }
            // 视频点赞数量-1
            videoMapper.updateCount(vid,VideoCountType.LIKE,-1);
        } else {
            throw new BusinessException(40900, "type参数错误");
        }
        return true;
    }
}
