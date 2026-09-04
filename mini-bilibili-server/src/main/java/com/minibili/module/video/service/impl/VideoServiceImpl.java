package com.minibili.module.video.service.impl;

import com.github.pagehelper.PageInfo;
import com.minibili.admin.video.domain.dto.AdminVideoPageDTO;
import com.minibili.admin.video.domain.vo.AdminVideoListVO;
import com.minibili.app.video.domain.dto.EditVideoDTO;
import com.minibili.app.video.domain.dto.HomeListDTO;
import com.minibili.app.video.domain.dto.PageVideoDTO;
import com.minibili.app.video.domain.vo.VideoDetailVO;
import com.minibili.app.video.domain.vo.VideoVO;
import com.minibili.common.comment.PageResult;
import com.minibili.common.comment.PageUtils;
import com.minibili.common.exception.BusinessException;
import com.minibili.common.id.IdGenerator;
import com.minibili.app.video.domain.dto.AddVideoDTO;
import com.minibili.module.collect.domain.entity.Collect;
import com.minibili.module.collect.mapper.CollectMapper;
import com.minibili.module.like.domain.entity.Likes;
import com.minibili.module.like.mapper.LikeMapper;
import com.minibili.module.user.domain.entity.Users;
import com.minibili.module.user.domain.enums.UserCountType;
import com.minibili.module.user.mapper.UserMapper;
import com.minibili.module.video.domain.entity.VideoCategories;
import com.minibili.module.video.domain.entity.Videos;
import com.minibili.module.video.domain.enums.VideoStatus;
import com.minibili.module.video.mapper.VideoCategoriesMapper;
import com.minibili.module.video.mapper.VideoMapper;
import com.minibili.module.video.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private VideoCategoriesMapper videoCategoriesMapper;

    private Users videoVerify(Long vid, Integer... status){
        if (vid == null){
            throw new BusinessException(40900,"视频id不能为null");
        }
        // 视频对应的用户 视频数量+1
        Videos video = videoMapper.findVideoById(vid);
        if (video == null){
            throw new BusinessException(40900,"视频不存在");
        }
        Users users = userMapper.findById(video.getUserId());
        if (users == null){
            throw new BusinessException(40900,"视频用户不存在");
        }
        // 如果视频状态不是待发布 需要拦截
        boolean statusValid = Arrays.asList(status).contains(video.getStatus());
        if (!statusValid){
            throw new BusinessException(40900,"当前视频状态不允许操作");
        }
        return users;
    }

    /**
     * 新增视频
     * 新增视频需要为对应的用户视频数量+1
     * 新增视频时需要选择视频分类
     * @param videoDTO
     * @return
     */
    @Override
    @Transactional
    public String addVideo(Long userId,AddVideoDTO videoDTO){
        Videos videos = new Videos();
        videos.setId(idGenerator.nextId()); //设置id
        videos.setUserId(userId); //设置用户id
        videos.setTitle(videoDTO.getTitle());
        videos.setCover(videoDTO.getCover());
        videos.setDesc(videoDTO.getDesc());
        videos.setDuration(videoDTO.getDuration());
        videos.setLinkUrl(videoDTO.getLinkUrl());
        // 新增视频
        videoMapper.addVideo(videos);
        // 新增视频与分类关联
        videoCategoriesMapper.batchAdd(videos.getId(),videoDTO.getCategoryIdList());
        return videos.getId().toString();
    };

    /**
     * Admin
     * 获取视频列表
     * @param adminVideoPageDTO
     * @return
     */
    @Override
    public PageResult<AdminVideoListVO> adminVideoList(AdminVideoPageDTO adminVideoPageDTO){
        PageUtils.startPage(adminVideoPageDTO);
        List<Videos> list = videoMapper.getAdminVideoList(adminVideoPageDTO);

        // 获取所有的videoId
        List<Long> videoIds = list.stream().map(Videos::getId).toList();
        // 一次性查询这些视频对应的分类 解决N+1问题
        List<VideoCategories> categoryList = videoCategoriesMapper.findByVideoIds(videoIds);
        // 按照videoId进行分组
        Map<Long, List<VideoCategories>> categoryMap = categoryList.stream().collect(Collectors.groupingBy(
                VideoCategories::getVideoId
        ));


        // 转换数据
        List<AdminVideoListVO> dataList = list.stream().map(videos -> {
            AdminVideoListVO vo = toAdminVideoListVO(videos);
            vo.setCategoryList(categoryMap.getOrDefault(videos.getId(), Collections.emptyList()));
            return vo;
        }).toList();

        // 进行分页处理
        return PageResult.from(new PageInfo<>(dataList));
    }

    /**
     * Admin
     * 发布视频
     * @param vid
     * @return
     */
    @Override
    @Transactional //使用事务
    public Boolean publishVideo(Long vid){
        Users users = videoVerify(vid, VideoStatus.PENDING.getCode());
        // 发布视频
        videoMapper.publishVideo(vid);
        // 用户的视频数量+1
        return userMapper.updateCount(users.getId(), UserCountType.VIDEO, 1);
    }

    /**
     * Admin
     * 下架视频
     * @param vid
     * @return
     */
    @Override
    @Transactional
    public Boolean videoHoldOut(Long vid){
        // 进行校验 获取用户信息
        Users users = videoVerify(vid, VideoStatus.PUBLISH.getCode());
        // 下架视频
        videoMapper.videoHoldOut(vid);
        //对应用户的视频数量-1
        return userMapper.updateCount(users.getId(),UserCountType.VIDEO,-1);
    }

    /**
     * App
     * 修改视频信息
     * @param editVideoDTO
     * @return
     */
    @Override
    @Transactional
    public Boolean editVideo(Long userId, EditVideoDTO editVideoDTO){
        // 进行校验
        Users users = videoVerify(editVideoDTO.getId(), VideoStatus.PENDING.getCode(),VideoStatus.HOLDOUT.getCode());
        if (!Objects.equals(userId,users.getId())){
            throw new BusinessException(40900,"不允许修改非本人作品");
        }
        int affectedRows = videoMapper.editVideo(userId,editVideoDTO);
        if (affectedRows != 1){
            throw new BusinessException(40900, "视频状态已变化或视频不存在");
        }
        // 先删除旧的分类关联关系
        videoCategoriesMapper.batchDelete(editVideoDTO.getId());
        // 再增加关联关系
        videoCategoriesMapper.batchAdd(editVideoDTO.getId(), editVideoDTO.getCategoryIdList());
        return true;
    };

    /**
     * App
     * 根据状态获取用户视频列表
     * @param userId
     * @param dto
     * @return
     */
    @Override
    public PageResult<VideoVO> videoList(Long userId, PageVideoDTO dto){
        List<Videos> list = videoMapper.videoList(userId, dto);
        List<VideoVO> dataList = list.stream().map(this::toVideoVo).toList();
        return PageResult.from(new PageInfo<>(dataList));
    }

    /**
     * App
     * 获取首页视频列表
     * @param dto
     * @return
     */
    @Override
    public PageResult<VideoVO> videoHomeList(HomeListDTO dto){
        List<Videos> list = videoMapper.videoHomeList(dto);
        List<VideoVO> dataList = list.stream().map(this::toVideoVo).toList();
        return PageResult.from(new PageInfo<>(dataList));
    };

    /**
     * App
     * 隐藏视频
     * @param userId
     * @param vid
     * @return
     */
    @Override
    @Transactional
    public Boolean hiddenVideo(Long userId, Long vid){
        // 校验视频状态
        Users users = videoVerify(vid, VideoStatus.PUBLISH.getCode());
        if (!Objects.equals(userId, users.getId())){
            throw new BusinessException(40900,"不允许修改非本人作品");
        }
        int affectedRows = videoMapper.hiddenVideo(userId,vid);
        if (affectedRows != 1){
            throw new BusinessException(40900, "视频状态已变化或视频不存在");
        }
        // 对应用户的视频数量-1
       return userMapper.updateCount(userId,UserCountType.VIDEO, -1);
    };

    /**
     * App
     * 重新提交视频
     * @param vid
     * @return
     */
    @Override
    public Boolean submitVideo(Long userId,Long vid){
        Users users = videoVerify(vid, VideoStatus.HOLDOUT.getCode());
        if (!Objects.equals(userId, users.getId())){
            throw new BusinessException(40900,"不允许修改非本人作品");
        }
        int rows = videoMapper.submitVideo(userId,vid);
        if (rows != 1){
            throw new BusinessException(40900, "视频状态已变化或视频不存在");
        }
        return true;
    };

    /**
     * App
     * 删除视频
     * 软删除 只改status 已下架才可删除
     * @param userId
     * @param vid
     * @return
     */
    @Override
    public Boolean deleteVideo(Long userId, Long vid){
        Users users = videoVerify(vid, VideoStatus.HOLDOUT.getCode());
        if (!Objects.equals(userId, users.getId())){
            throw new BusinessException(40900,"不允许修改非本人作品");
        }
        int rows = videoMapper.deleteVideo(userId,vid);
        if (rows != 1){
            throw new BusinessException(40900, "视频状态已变化或视频不存在");
        }
        return true;
    }

    /**
     * 获取视频详情
     * 需要根据用户是否登录 来进行逻辑处理
     * 如果已经登录 需要判断用户是否点赞与收藏过视频
     * @param vid
     * @return
     */
    @Override
    @Transactional
    public VideoDetailVO videoDetail(Long userId,Long vid){
        if (vid == null){
            throw new BusinessException(40900,"视频id不能为null");
        }
        Videos videos = videoMapper.findVideoById(vid);
        if (videos == null){
            throw new BusinessException(40900,"视频不存在");
        }
        // 转换数据
        VideoDetailVO detailVO = new VideoDetailVO();
        BeanUtils.copyProperties(videos,detailVO);

        boolean isLike = false;
        boolean isCollect = false;
        // 根据用户是否登录
        if (userId != null){
            log.info("已登录");
            // 获取用户是否点赞和收藏过视频
            Likes like = likeMapper.getLike(userId,vid);
            if (like != null){
                isLike = true;
            }
            Collect collect = collectMapper.getCollect(userId,vid);
            if (collect != null){
                isCollect = true;
            }
        }
        // 根据视频id获取分类列表
        List<VideoCategories> list = videoCategoriesMapper.findByVid(vid);
        // 获取分类id
        List<Long> categoryIdList = list.stream().map(VideoCategories::getCategoryId).toList();
        detailVO.setCategoryIdList(categoryIdList);

        detailVO.setIsCollect(isCollect);
        detailVO.setIsLike(isLike);
        return detailVO;
    };








    // admin转换数据
    private AdminVideoListVO toAdminVideoListVO(Videos videos){
        if (videos == null){
            return null;
        }
        AdminVideoListVO vo = new AdminVideoListVO();
        BeanUtils.copyProperties(videos, vo);
        // 为视频返回分类列表数据
        List<VideoCategories> list = videoCategoriesMapper.findByVid(videos.getId());
        vo.setCategoryList(list);
        return vo;
    }
    // app转换数据
    private VideoVO toVideoVo(Videos videos){
        if (videos == null){
            return null;
        }
        VideoVO vo = new VideoVO();
        BeanUtils.copyProperties(videos, vo);
        return vo;
    }
}
