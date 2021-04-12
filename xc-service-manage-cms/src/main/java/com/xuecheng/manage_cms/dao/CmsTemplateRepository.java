package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * created by wangzexu on 2021/3/10
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
