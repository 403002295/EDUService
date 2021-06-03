package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.TeachPlanMapper;
import com.xuecheng.manage_course.dao.TeachPlanRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * created by wangzexu on 5/10/21
 */
@Service
public class CourseService {
    @Autowired
    TeachPlanMapper teachPlanMapper;
    @Autowired
    TeachPlanRepository teachPlanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;

    /**
     * 查询课程计划
     * @param courseId 课程计划ID
     * @return
     */
    public TeachplanNode findTeachplanList(String courseId) {
        TeachplanNode teachplanNode = teachPlanMapper.selectList(courseId);
        return teachplanNode;
    }

    //根据课程跟节点如果没有则添加跟节点
    public String getTeachPlanRoot(String courseId) {
        //验证课程ID
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        List<Teachplan> teachplans = teachPlanRepository.findByCourseidAndParentid(courseId, "0");
        if (null == teachplans || teachplans.size() < 1) {
            Teachplan teachplan = new Teachplan();
            teachplan.setCourseid(courseId);
            teachplan.setPname(courseBase.getName());
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setStatus("0");
            teachPlanRepository.save(teachplan);
            return teachplan.getId();
        }
        Teachplan teachplan = teachplans.get(0);
        return teachplan.getId();

    }

    /**
     * 添加课程计划
     * @param teachplan 数据
     * @return 响应结果
     */
    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {
        //1、检验课程ID 和 课程计划名称 关键字段是否为合法数据
        if (null == teachplan || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //取出课程ID
        String courseid = teachplan.getCourseid();
        //取出父节点ID
        String parentid = teachplan.getParentid();
        //如果父节点ID为null，则获取跟节点
        if (StringUtils.isEmpty(parentid)) {
            parentid = getTeachPlanRoot(courseid);
        }
        //取出父节点的信息
        Optional<Teachplan> optional = teachPlanRepository.findById(parentid);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan teachplanParent = optional.get();
        String gradeParent = teachplanParent.getGrade();
        teachplan.setParentid(parentid);
        if ("1".equals(gradeParent)) {
            teachplan.setGrade("2");
        } else if ("2".equals(gradeParent)) {
            teachplan.setGrade("3");
        }
        //设置课程ID
        teachplan.setCourseid(courseid);
        teachPlanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public ResponseResult addCourse(CourseBase courseBase) {
        if (null == courseBase
                || StringUtils.isEmpty(courseBase.getName())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        courseBaseRepository.save(courseBase);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public QueryResponseResult showCourseList(String page, String size) {
        Pageable pageable = new PageRequest(Integer.valueOf(page), Integer.valueOf(size));
        Page<CourseBase> all = courseBaseRepository.findAll(pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
