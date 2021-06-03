package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cmsPage.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * created by wangzexu on 2021/3/1
 */
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    PageService pageService;
    @ApiImplicitParams(@ApiImplicitParam(name = "size", value = "每天记录数"
            , required = true, paramType = "path", dataType = "int"))
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,@PathVariable("size") int size, QueryPageRequest queryPageRequest) {
  /*      //use test  data protest interface general
        QueryResult<Object> queryResult = new QueryResult<>();
        queryResult.setTotal(2);
        List list = new ArrayList();
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试页面");
        list.add(cmsPage);
        queryResult.setList(list);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);*/
        QueryResponseResult queryResponseResult = pageService.findList(page, size, queryPageRequest);
        return queryResponseResult;

    }

    @RequestMapping("/add")
    @Override
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }

    @GetMapping("/get/{id}")
    @Override
    public CmsPageResult findById(@PathVariable("id") String id) {
        CmsPage cmsPage = pageService.findById(id);
        if (cmsPage != null) {
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);
            return cmsPageResult;
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    @PutMapping("/edit/{id}")
    @Override
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id,cmsPage);
    }

    @DeleteMapping("/delete/{id}")
    @Override
    public ResponseResult delete(@PathVariable("id") String id) {
        return pageService.delete(id);
    }

    @PostMapping("/postPage/{pageId}")
    @Override
    public ResponseResult post(@PathVariable("pageId") String pageId) {

        return pageService.postPage(pageId);
    }
}
