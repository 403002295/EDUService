package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * created by wangzexu on 5/31/21
 */
public interface TeachPlanRepository extends JpaRepository<Teachplan,String> {

    //定义方法根据课程ID和父节点ID 查询节点列表，可以使用此方法实现查询跟节点
    public List<Teachplan> findByCourseidAndParentid(String courseId, String parentId);
}
