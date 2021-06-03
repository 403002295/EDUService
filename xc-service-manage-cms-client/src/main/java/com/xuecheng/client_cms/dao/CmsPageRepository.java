package com.xuecheng.client_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * created by wangzexu on 4/29/21
 *
 * 操作mongodb数据库的类 CmsPage
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
}
