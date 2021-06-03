package com.xuecheng.client_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.client_cms.dao.CmsPageRepository;
import com.xuecheng.client_cms.dao.CmsSiteRepository;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * created by wangzexu on 4/29/21
 * doa 操作数据库
 * grid 用来操作文件属性
 * bucket 存储文件的地方
 */
@Service
public class PageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsSiteRepository cmsSiteRepository;


    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    public void savePageToServerPath(String pageId) {

        //根据pageID 查询页面的屋里路径
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if (optionalCmsPage.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        CmsPage cmsPage = optionalCmsPage.get();


        //页面所属站点
        String siteId = cmsPage.getSiteId();
        //页面静态文件ID
        Optional<CmsSite> optionalCmsSite = cmsSiteRepository.findById(siteId);
        CmsSite cmsSite = optionalCmsSite.get();
        //  !!  ==  文件存放位置
        String pagePath = cmsSite.getSitePhysicalPath()+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
        //根据页面信息 获取实际的文件信息
        String htmlFileId = cmsPage.getHtmlFileId();
        InputStream inputStream = this.getFileById(htmlFileId);
        if (null == inputStream) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
            FileOutputStream outputStream = null;
            File file = new File(pagePath);
        try {
            try {
                if (!file.exists()) {
                    file = new File("/Users/wangzexu/Documents/xuecheng/front/"+cmsPage.getPageName());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            } catch (Exception e) {

            }finally {
                file =  new File("/Users/wangzexu/Documents/xuecheng/front/"+cmsPage.getPageName());
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            outputStream = new FileOutputStream(file);

            IOUtils.copy(inputStream, outputStream);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //读取文件输入流
//        inputStream.read()

    }

    private InputStream getFileById(String htmlFileId) {


        try {
            //获取文件信息
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
            //获取下载流 根据 文件ID
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

            // gridFSResource 文件系统资源
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,
                    gridFSDownloadStream);
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


}
