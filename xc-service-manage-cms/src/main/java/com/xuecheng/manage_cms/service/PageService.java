package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * created by wangzexu on 2021/3/1
 */
@Service
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * ????????????????????????
     * @param page ????????????
     * @param size ??????????????????
     * @param queryPageRequest ????????????
     * @return ????????????
     */
  /*  public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {

        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 20;
        }
        //????????????
        Pageable pageable = new PageRequest(page, size);
        //????????????]
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);

        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();

        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());



        return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
    }*/

    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 20;
        }
        //???????????????
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("pageName",ExampleMatcher.GenericPropertyMatchers.contains());

        //?????????
        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplageId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplageId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageName())) {
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageType())) {
            cmsPage.setPageType(queryPageRequest.getPageType());
        }
        Example example = Example.of(cmsPage, exampleMatcher);
        Pageable pageable = new PageRequest(page, size);
        Page all = cmsPageRepository.findAll(example, pageable);
        QueryResult<CmsPage> queryResult = new QueryResult();
        queryResult.setTotal(all.getTotalElements());
        queryResult.setList(all.getContent());

        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    public CmsPageResult add(CmsPage cmsPage) {
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (null != cmsPage1) {
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        if (null == cmsPage1) {
            cmsPage.setPageId(null);//??????ID ????????????
            CmsPage cmsPage2 = cmsPageRepository.save(cmsPage);
            //????????????
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage2);
            return cmsPageResult;

        }
        return new CmsPageResult(CommonCode.FAIL, null);

    }

    /**
     * ??????ID??????
     * @param id
     * @return
     */
    public CmsPage findById(String id) {
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(id);
        if (optionalCmsPage.isPresent()) {
            return optionalCmsPage.get();
        }
        return null;
    }


    public CmsPageResult update(String id, CmsPage cmsPage) {
        //1???????????????????????????????????????
        CmsPage one = findById(id);
        if (null != one) {
            //???????????????????????????????????????
            //????????????ID
            one.setTemplateId(cmsPage.getTemplateId());
            //??????????????????
            one.setPageAliase(cmsPage.getPageAliase());
            //??????????????????
            one.setPageName(cmsPage.getPageName());
            //??????????????????
            one.setPageWebPath(cmsPage.getPageWebPath());
            //??????????????????
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //??????dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //??????ID
            one.setSiteId(cmsPage.getSiteId());
            //????????????
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //????????????
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    public ResponseResult delete(String id) {
        //??????????????????
        CmsPage cmsPage = this.findById(id);
        if (null != cmsPage) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * ???????????????
     * @param pageId ??????ID
     * @return
     */
    public String getPageHtml(String pageId) {
        //????????????????????????
        Map model = this.getModelByPageId(pageId);
        if (model == null) {
            //????????????
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //??????????????????
        java.lang.String template = this.getTemplateByPageId(pageId);
        if (template == null) {
            //??????????????????
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //???????????????
        String html = generateHtml(template, model);
        return html;
    }

    /**
     * ???????????????
     * @param template
     * @param model
     * @return
     */
    private String generateHtml(String template, Map model) {
        try {
            //???????????????
            Configuration configuration = new Configuration(Configuration.getVersion());
            //???????????????
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            //?????????????????????
            configuration.setTemplateLoader(stringTemplateLoader);
            //????????????
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ??????pageId ????????????
     * @param pageId
     * @return
     */
    private String getTemplateByPageId(String pageId) {
        //??????pageId ??????templateID
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        String templateId = null;
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        templateId = optional.get().getTemplateId();
        Optional<CmsTemplate> optionalCmsTemplate = cmsTemplateRepository.findById(templateId);
        if (!optionalCmsTemplate.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        String templateFileId = optionalCmsTemplate.get().getTemplateFileId();
        //????????????ID ????????????
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        //?????????????????????
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //??????resource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        try {
            String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ????????????????????????
     * @param pageId
     * @return
     */
    private Map getModelByPageId(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        String dataUrl = null;
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
        }
        CmsPage cmsPage = optional.get();
        dataUrl = cmsPage.getDataUrl();
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }


    public ResponseResult postPage(String pageId) {
        //???????????????
        String pageHtml = this.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
            System.out.println("???????????????");
        }
        //?????????????????????
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);


    }

    private void sendPostPage(String pageId) {
        CmsPage cmsPage = this.getById(pageId);
        Map<String,String> map = new HashMap<>();
        map.put("pageId", pageId);
        String msg = JSON.toJSONString(map);
        String siteId = cmsPage.getSiteId();
        this.rabbitTemplate.convertAndSend(RabbitConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);

    }

    public CmsPage getById(String pageId) {
        Optional<CmsPage> byId = cmsPageRepository.findById(pageId);
        if (!byId.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        return byId.get();
    }

    private CmsPage saveHtml(String pageId, String pageHtml) {
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if (!optionalCmsPage.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optionalCmsPage.get();
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }

        InputStream inputStream = IOUtils.toInputStream(pageHtml);
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        //????????????ID
        cmsPage.setHtmlFileId(objectId.toString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;


    }

}

