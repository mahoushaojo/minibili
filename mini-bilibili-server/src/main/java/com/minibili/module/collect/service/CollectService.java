package com.minibili.module.collect.service;

import com.minibili.app.user.domain.dto.CollectListDTO;
import com.minibili.common.comment.PageResult;
import com.minibili.module.collect.domain.vo.CollectListVO;

public interface CollectService {
    Boolean setCollect(Long userId, Long videoId, Integer type);

    PageResult<CollectListVO> getCollectList(Long userId,CollectListDTO collectListDTO);
}
