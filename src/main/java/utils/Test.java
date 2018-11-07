package utils;

import NetConst.URLUtils;

import static NetConst.URLUtils.sendURLWithParams;

/**
 * @Author: LXL
 */
public class Test {

    public static void main(String[] args) {
        String poiParam = "http://api.map.baidu.com/place/v2/suggestion?output=json&ak=iQ2AllfKh8qKWGUVFSP7Lrjly5x4EcsC&query=日本&region=武汉市&city_limit=true";
        String result = sendURLWithParams(poiParam);
        System.out.println(result);
    }
}
