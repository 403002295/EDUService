package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * created by wangzexu on 2021/3/1
 */

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {

    //diy dao method
    //search accroding to page name
    CmsPage findByPageName(String pageName);

    //searched by pagename and pagetype
    CmsPage findByPageNameAndPageType(String pageName, String pageType);

    //searched by sideid and pageType
    CmsPage findBySiteIdAndPageType(String siteId, String pageType);

    //searched count by siteId and pageType
    int countBySiteIdAndPageType(String siteId, String pageType);

    //根据页面名称、站点ID 、 页面访问路径 查询
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName, String siteId, String pageWebPath);
}
