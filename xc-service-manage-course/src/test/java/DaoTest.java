import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.manage_course.CourseSpringbootApplication;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import com.xuecheng.manage_course.dao.TeachPlanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * created by wangzexu on 5/28/21
 */
@SpringBootTest(classes = CourseSpringbootApplication.class)
@RunWith(SpringRunner.class)
public class DaoTest {

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    TeachPlanMapper teachPlanMapper;

    @Autowired
    CourseMapper courseMapper;
    @Test
    public void testCourseBaseRepository() {

        Optional<CourseBase> byId = courseBaseRepository.findById("");
    }

    @Test
    public void testPageHelper() {
        PageHelper.startPage(1, 10);
        CourseListRequest courseListRequest=new CourseListRequest();
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
//        courseMapper.findCourseListPage();
        List<CourseInfo> content = courseListPage.getResult();
        System.out.println(content);
    }
}
