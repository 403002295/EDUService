package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * created by wangzexu on 2021/3/1
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindPage(){
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);

        System.out.println("===============================================");
        System.out.println(all);
        List<CmsPage> content = all.getContent();
        for (CmsPage cmsPage : content) {
            System.out.println(cmsPage);
        }
        System.out.println("===============================================");
        Stream<CmsPage> cmsPageStream = content.stream().filter(new Predicate<CmsPage>() {
            @Override
            public boolean test(CmsPage cmsPage) {
                boolean 首页 = cmsPage.getPageAliase().equals("首页");
                return 首页;
            }
        });
        Iterator<CmsPage> iterator1 = cmsPageStream.iterator();
        Iterator<CmsPage> iterator = iterator1;
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }

    }
    @Test
    /**
     * 精确查询
     */
    public void testAcidSearch() {
        //页面名称模糊查询，需要自定义字符串的匹配器实现模糊匹配
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.startsWith());
        //条件值
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageAliase("课程");
//        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
//        cmsPage.setTemplateId("5a962c16b00ffc514038fafd");

        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        Pageable pageable = new PageRequest(0, 10);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println("===========");
        for (CmsPage page : content) {
            System.out.println(page);
        }
        Stream<CmsPage> cmsPageStream = content.stream().filter(new Predicate<CmsPage>() {
            @Override
            public boolean test(CmsPage cmsPage) {
                boolean 首页 = cmsPage.getPageAliase().equals("首页");
                return 首页;
            }
        });
        Iterator<CmsPage> iterator = cmsPageStream.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }


    }
    @Autowired
    RestTemplate restTemplate;

    /**
     * 测试http
     */
    @Test
    public void testRestTemplate() {

        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        System.out.println(forEntity);
    }
    @Autowired
    PageService pageService;

    @Test
    public void testPreview() {
        String pageId = "5a795ac7dd573c04508f3a56";
        String pageHtml = pageService.getPageHtml(pageId);
        System.out.println(pageHtml);
    }
}
