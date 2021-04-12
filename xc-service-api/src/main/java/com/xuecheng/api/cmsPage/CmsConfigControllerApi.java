package com.xuecheng.api.cmsPage;

import com.xuecheng.framework.domain.cms.response.CmsConfigResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * created by wangzexu on 2021/3/8
 */
@Api(value = "cms配置管理接口",description = "cms配置管理接口,提供数据模型的管理查询接口。")
public interface CmsConfigControllerApi {
    @ApiOperation("根据ID查询CMS配置信息")
    public CmsConfigResult getModel(String id);
}
