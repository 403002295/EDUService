package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;

/**
 * created by wangzexu on 2021/3/8
 */
@Data
public class CmsConfigResult extends ResponseResult {
    CmsConfig cmsConfig;

    public CmsConfigResult(ResultCode resultCode,CmsConfig cmsConfig) {
        super(resultCode);
        this.cmsConfig=cmsConfig;
    }
}
