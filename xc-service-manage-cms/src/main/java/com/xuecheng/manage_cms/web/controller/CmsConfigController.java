package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cmsPage.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.response.CmsConfigResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by wangzexu on 2021/3/8
 */
@RestController
@RequestMapping("/cms/config")
public class CmsConfigController implements CmsConfigControllerApi {
    @Autowired
    CmsConfigService cmsConfigService;
    @Override
    @GetMapping("/getmodel/{id}")
    public CmsConfigResult getModel(@PathVariable("id") String id) {
        CmsConfig cmsConfig = cmsConfigService.getCmsConfigById(id);
        if (null != cmsConfig) {
            return new CmsConfigResult(CommonCode.SUCCESS, cmsConfig);
        }
        return new CmsConfigResult(CommonCode.FAIL,null);
    }
}
