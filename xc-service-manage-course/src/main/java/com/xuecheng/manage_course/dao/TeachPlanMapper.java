package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * created by wangzexu on 5/10/21
 */
@Mapper
public interface TeachPlanMapper {
    /**
     * 查询三级菜单信息 每个课程都有个三级课程结构
     * @param courseId
     * @return
     */
    public TeachplanNode selectList(String courseId);
}
