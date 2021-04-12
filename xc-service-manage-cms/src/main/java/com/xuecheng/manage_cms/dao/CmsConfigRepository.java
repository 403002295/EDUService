package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * created by wangzexu on 2021/3/8
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
