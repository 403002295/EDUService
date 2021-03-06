package com.xuecheng.api.cmsPage;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * created by wangzexu on 2021/3/1
 */
@Api(value = "cms页面管理接口",description = "cms页面管理接口，提供页面的增删改查")
public interface CmsPageControllerApi {

    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true,
            paramType = "path", dataType = "int"),

            @ApiImplicitParam(name = "size", value = "每天记录数"
                    , required = true, paramType = "path", dataType = "int")})
    public  QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    @ApiOperation(value = "添加页面")
    public CmsPageResult add(CmsPage cmsPage);

    @ApiOperation(value = "通过ID查询页面")
    public CmsPageResult findById(String id);

    @ApiOperation(value = "修改页面")
    public CmsPageResult edit(String id, CmsPage cmsPage);

    @ApiOperation(value = "删除页面")
    public ResponseResult delete(String id);

    @ApiOperation(value = "发布页面")
    public ResponseResult post(String pageId);

}
