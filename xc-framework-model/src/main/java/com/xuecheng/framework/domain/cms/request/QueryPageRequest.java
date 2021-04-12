package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryPageRequest extends RequestData {

    //站点ID
    @ApiModelProperty("站点")
    private String siteId;
    //page id
    @ApiModelProperty("页面ID")
    private String pageId;
    //page name
    @ApiModelProperty("页面名称")
    private String pageName;
    // alias name
    @ApiModelProperty("页面别名")
    private String pageAliase;
    //template id
    @ApiModelProperty("模板ID")
    private String templageId;
    @ApiModelProperty("页面类型")
    private String pageType;

}
