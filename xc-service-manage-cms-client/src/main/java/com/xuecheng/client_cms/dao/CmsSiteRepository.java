package com.xuecheng.client_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * created by wangzexu on 4/29/21
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
