package com.minibili.module.collect.service.impl;

import com.github.pagehelper.PageInfo;
import com.minibili.app.user.domain.dto.CollectListDTO;
import com.minibili.common.comment.PageResult;
import com.minibili.common.exception.BusinessException;
import com.minibili.common.id.IdGenerator;
import com.minibili.module.collect.domain.entity.Collect;
import com.minibili.module.collect.domain.vo.CollectListVO;
import com.minibili.module.collect.mapper.CollectMapper;
import com.minibili.module.collect.service.CollectService;
import com.minibili.module.user.domain.enums.UserCountType;
import com.minibili.module.user.mapper.UserMapper;
import com.minibili.module.video.domain.enums.VideoCountType;
import com.minibili.module.video.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class collectServiceImpl implements CollectService {
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 收藏/取消收藏
     * @param userId
     * @param vid
     * @param type
     * @return
     */
    @Override
    @Transactional
    public Boolean setCollect(Long userId, Long vid, Integer type){
        if (vid == null){
            throw new BusinessException(40900,"视频id不能为空");
        }
        if (type == null){
            throw new BusinessException(40900,"type不能为空");
        }
        int rows;
        if (type == 1){
            Collect collectData = collectMapper.getCollect(userId,vid);
            if (collectData != null){
                throw new BusinessException(40900,"已收藏过该视频");
            }
            // 收藏
            Collect collect = new Collect();
            collect.setId(idGenerator.nextId());
            collect.setUserId(userId);
            collect.setVideoId(vid);
            rows = collectMapper.setCollect(collect);
            if (rows != 1){
                throw new BusinessException(50000, "点赞失败");
            }
            // 收藏成功之后 用户的收藏数量+1，对应的视频收藏数量+1
            userMapper.updateCount(userId, UserCountType.COLLECT,1);
            videoMapper.updateCount(vid, VideoCountType.COLLECT, 1);
        }else if (type == 0){
            // 取消收藏
            rows = collectMapper.deleteCollect(userId,vid);
            if (rows != 1){
                throw new BusinessException(50000, "取消点赞失败");
            }
            userMapper.updateCount(userId, UserCountType.COLLECT,-1);
            videoMapper.updateCount(vid, VideoCountType.COLLECT, -1);
        }else{
            throw new BusinessException(40900, "type参数错误");
        }
        return true;
    }

    @Override
    public PageResult<CollectListVO> getCollectList(Long userId, CollectListDTO dto){
        List<CollectListVO> list = collectMapper.getCollectList(userId,dto.getKeyword());
        return PageResult.from(new PageInfo<>(list));
    }
}
