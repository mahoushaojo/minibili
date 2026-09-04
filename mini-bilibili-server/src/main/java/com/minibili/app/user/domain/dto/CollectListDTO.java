package com.minibili.app.user.domain.dto;

import com.minibili.common.comment.PageUtils;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectListDTO extends PageUtils.PageQuery {
    @Size(max = 100,message = "最多100个字符")
    private String keyword;
}
