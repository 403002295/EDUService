package com.xuecheng.manage_course.controller;

import com.xuecheng.api.cmsPage.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * created by wangzexu on 5/10/21
 */

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;

    @GetMapping("/TeachPlan/list/{courseId}")
    @Override
    public TeachplanNode findTeachPlanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }
    @PostMapping("/teachplan/add")
    @Override
    public ResponseResult addTeachPlan(Teachplan teachplan) {
        return courseService.addTeachPlan(teachplan);
    }

    @Override
    @PostMapping("/coursebase/add")
    public ResponseResult addCourse(@RequestBody CourseBase courseBase) {
        return courseService.addCourse(courseBase);
    }

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult courseList(@PathVariable("page") String page, @PathVariable("size") String size) {
        return courseService.showCourseList(page,size);
    }
}
