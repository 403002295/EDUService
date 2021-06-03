package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * created by wangzexu on 6/2/21
 */
@Mapper
public interface CourseMapper {

    CourseBase findCourseBaseById(String id);
    Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
}
