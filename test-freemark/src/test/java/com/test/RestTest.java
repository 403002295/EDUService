package com.test;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.test.freemarker.FreemarkerTestApplication;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * created by wangzexu on 2021/3/8
 */
@SpringBootTest(classes = FreemarkerTestApplication.class)
@RunWith(SpringRunner.class)
public class RestTest {
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;

    @Test
    public void testGridFs() {
        File file = new File("/Users/wangzexu/Documents/xuecheng/front/xc-ui-pc-static-portal/include/index_banner.html");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            //向gridFs 存储文件
            ObjectId ob = gridFsTemplate.store(inputStream, "轮播图测试文件01", "");
            System.out.println(ob.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void queryFile() throws IOException {
        String id= "60487c5f6fdf70da83309c1c";
        //根据文件ID查询
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(s);
    }

    @Test
    public void deleteFile() {
        String id= "60464af6ae6610d4062ffeff";
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
    }

}
