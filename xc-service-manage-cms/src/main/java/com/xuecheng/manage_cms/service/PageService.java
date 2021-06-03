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
     * 页面列表分页查询
     * @param page 当前页码
     * @param size 页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
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
        //分页对象
        Pageable pageable = new PageRequest(page, size);
        //分页查询]
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
        //配置模糊项
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("pageName",ExampleMatcher.GenericPropertyMatchers.contains());

        //条件值
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
            cmsPage.setPageId(null);//页面ID 自动生成
            CmsPage cmsPage2 = cmsPageRepository.save(cmsPage);
            //返回结果
            CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage2);
            return cmsPageResult;

        }
        return new CmsPageResult(CommonCode.FAIL, null);

    }

    /**
     * 根据ID查询
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
        //1、查询数据库是否存在该数据
        CmsPage one = findById(id);
        if (null != one) {
            //以查询出来的数据为模板更改
            //跟新模板ID
            one.setTemplateId(cmsPage.getTemplateId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //站点ID
            one.setSiteId(cmsPage.getSiteId());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    public ResponseResult delete(String id) {
        //查询是否存在
        CmsPage cmsPage = this.findById(id);
        if (null != cmsPage) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 页面静态化
     * @param pageId 页面ID
     * @return
     */
    public String getPageHtml(String pageId) {
        //获取页面模型数据
        Map model = this.getModelByPageId(pageId);
        if (model == null) {
            //数据为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板
        java.lang.String template = this.getTemplateByPageId(pageId);
        if (template == null) {
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = generateHtml(template, model);
        return html;
    }

    /**
     * 执行静态化
     * @param template
     * @param model
     * @return
     */
    private String generateHtml(String template, Map model) {
        try {
            //生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            //配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //获取模板
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据pageId 查询模板
     * @param pageId
     * @return
     */
    private String getTemplateByPageId(String pageId) {
        //根据pageId 查询templateID
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
        //根据文件ID 下载文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        //获取下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建resource
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
     * 获取页面模型数据
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
        //执行静态化
        String pageHtml = this.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
            System.out.println("页面不存在");
        }
        //保存静态化页面
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
        //更新文件ID
        cmsPage.setHtmlFileId(objectId.toString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;


    }

}

