package com.xuecheng.api.cmsPage;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * created by wangzexu on 5/10/21
 */
@Api(value = "课程计划",description = "课程管理服务，课程发布修改管理等一切关于课程本身的功能")
public interface CourseControllerApi {
    @ApiOperation(value = "课程计划查询")
    public TeachplanNode findTeachPlanList(String courseId);
    @ApiOperation("添加课程计划")
    public ResponseResult addTeachPlan(Teachplan teachplan);
    @ApiOperation("添加课程")
    public ResponseResult addCourse(CourseBase courseBase);
    @ApiOperation("展示课程")
    public QueryResponseResult courseList(String page, String size);
}
