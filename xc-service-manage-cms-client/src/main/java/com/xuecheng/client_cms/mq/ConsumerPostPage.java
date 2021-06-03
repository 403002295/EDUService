package com.xuecheng.client_cms.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.client_cms.dao.CmsPageRepository;
import com.xuecheng.client_cms.service.PageService;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * created by wangzexu on 4/29/21
 */
@Component
public class ConsumerPostPage {


    @Autowired
    RabbitTemplate template;
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    PageService pageService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);


    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg) {
        Map<String, String> map = JSON.parseObject(msg, Map.class);
        LOGGER.info("receive sms post page:{}"+msg.toString());
        System.out.println("receive sms post page:{}"+msg.toString());
        //获取cmspageId  判断是否有该页面
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(map.get("pageId"));
        if (optionalCmsPage.isPresent()) {
            LOGGER.error("没有对应的页面！");
            return;
        }
        pageService.savePageToServerPath(map.get("pageId"));
    }

}
