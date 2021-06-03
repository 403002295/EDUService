package com.test;

import org.junit.Test;

/**
 * created by wangzexu on 4/21/21
 */

public class CommonTest {

    /**
     * 检测String的equals 方法 参数 为null是否报错 ！！！
     * 实际测试 没有报错 可以传null
     * result: false
     */
    @Test
   public void testStringNull() {
        String test = "安徽|北京|重庆|福建|甘肃|广东|广西|贵州|海南|河北|河南|黑龙江|湖北|湖南|吉林|江苏|江西|辽宁|内蒙古|宁夏|青海|山东|山西|陕西|上海|四川|天津|西藏|新疆|云南|浙江|国漫|号百|全国|";
        String[] split = test.split("\\|");
        String join = String.join("电信|", split);
        System.out.println(join);
    }
}
