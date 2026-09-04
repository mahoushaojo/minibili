package com.minibili.module.like.service;

public interface LikeService {
    Boolean setLike(Long userId, Long vid, Integer type);
}
