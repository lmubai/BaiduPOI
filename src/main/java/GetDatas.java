import NetConst.URLUtils;
import model.Area;
import model.Point;
import model.Rectangle;
import model.StoreModel;
import net.sf.json.JSONObject;
import utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static NetConst.URLUtils.sendURLWithParams;

/**
 * @author Administrator
 */
public class GetDatas {
    public static String poiUrl = "http://api.map.baidu.com/place/v2/search";
    public static final int PAGE_SIZE = 20;
    public static int total_count = 0;
    public static void getByBounds(Rectangle rectangle) {
        if (rectangle == null) {
            return;
        }
        Point lb = rectangle.leftbottom;
        Point rt = rectangle.rightTop;
        //先纬度，再经度
        String leftbottom = lb.latitude + "," + lb.longitude;
        //先纬度，再经度
        String righttop = rt.latitude + "," + rt.longitude;
        int currentPageIndex = 0;
        String poiParam = "?scope=2&q=" + URLUtils.keyword + "&output=json&ak=" + URLUtils.API_KEY + "&page_size=20&bounds=" + leftbottom + "," + righttop + "&page_num=" + currentPageIndex;
        String result = sendURLWithParams(poiUrl + poiParam);
        JSONObject poiJsonroot = JSONObject.fromObject(result);
        int total = poiJsonroot.getInt("total");
        System.out.println("当前区域 ----->" + rectangle);
        if (total >= 400) {
            //四分法切片，切片后进入递归
            double half_long = (lb.longitude + rt.longitude) / 2;
            double half_lat = (lb.latitude + rt.latitude) / 2;
            // 新规划出的5个点，
            // 中心点
            Point centerPoint = new Point(half_long, half_lat);
            //左中
            Point p1 = new Point(lb.longitude, half_lat);
            //中上
            Point p2 = new Point(half_long, rt.latitude);
            //中下
            Point p3 = new Point(half_long, lb.latitude);
            //右中
            Point p4 = new Point(rt.longitude, half_lat);
            //4分法递归 防止区域划分过于狭长
//            getByBounds(new Rectangle(rectangle.currentAreaName, lb, centerPoint));
//            getByBounds(new Rectangle(rectangle.currentAreaName, centerPoint, rt));
//            getByBounds(new Rectangle(rectangle.currentAreaName, p1, p2));
//            getByBounds(new Rectangle(rectangle.currentAreaName, p3, p4));
            model.Rectangle r1 = new model.Rectangle(rectangle.currentAreaName, lb, p4);
            model.Rectangle r2 = new model.Rectangle(rectangle.currentAreaName, p1, rt);
            getByBounds(r1);
            getByBounds(r2);
        } else {
            //进入数采集，
            int pages = 0;
            if (total % PAGE_SIZE == 0) {
                pages = total / PAGE_SIZE;
            } else {
                pages = total / PAGE_SIZE + 1;
            }
            List<StoreModel> storeModelList = new ArrayList<StoreModel>(20);
            System.out.println("**************当前切片区域总数 " + total + "     分页数{" + pages + "}     currentPageIndex" + currentPageIndex + "  " + rectangle.toString());
            for (int i = 0; i < pages; i++) {
                try {
                    String pageparam = "?&q=" + URLUtils.keyword + "&scope=2&output=json&ak=" + URLUtils.API_KEY + "&page_size=20&bounds=" + leftbottom + "," + righttop + "&page_num=" + currentPageIndex;
                    currentPageIndex++;//开始第二页的请求
                    String r = sendURLWithParams(poiUrl + pageparam);
                    JSONObject page = JSONObject.fromObject(r);
                    addPageData(page, storeModelList, rectangle.currentAreaName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //先保存数据
            FileUtils.writeIntoCSV(storeModelList);
            storeModelList.clear();
            storeModelList = null;
            //set as default.
            currentPageIndex = 0;
        }
    }

    public static void addPageData(JSONObject page, List<StoreModel> storeModelList, String currentAreaName) {
        List<JSONObject> stores = page.getJSONArray("results");
        for (int k = 0; k < stores.size(); k++) {
            StoreModel storeModel = new StoreModel();
            if (stores.get(k).containsKey("name")) {
                storeModel.storeName = stores.get(k).getString("name");
            }
            if (stores.get(k).containsKey("detail_info")) {
                storeModel.tag = stores.get(k).getJSONObject("detail_info").getString("tag");
            }
            if (stores.get(k).containsKey("location") && stores.get(k).getJSONObject("location").containsKey("lng") && stores.get(k).getJSONObject("location").containsKey("lat")) {
                storeModel.longitude = stores.get(k).getJSONObject("location").getString("lng");
                storeModel.latitude = stores.get(k).getJSONObject("location").getString("lat");
                Area area = getDistinct(storeModel.latitude, storeModel.longitude);
                //不属于新疆维吾尔
                if (area == null || !area.province.equals(Area.HUBEI)) {
                    System.out.println("非湖北地区   ****      " + area.toString() + storeModel.longitude + " , " + storeModel.latitude);
                    continue;
                }
                storeModel.areaInfo = area;
                if (area.formatted_address.length() == 0) {
                    storeModel.formatted_address = currentAreaName;
                } else {
                    storeModel.formatted_address = area.formatted_address;
                }
            }
            storeModelList.add(storeModel);
            System.out.println("总计数： " + (total_count++) + "     " + storeModel.toString());
        }
    }

    /**
     * 获取新政区域区域信息
     *
     * @param latitude
     * @param longtitude
     * @return
     */
    public static Area getDistinct(String latitude, String longtitude) {
        String url = URLUtils.geoCodeReversURL(latitude, longtitude);
        String r = sendURLWithParams(url);
        JSONObject page = JSONObject.fromObject(r);
        if (page == null) {
            return null;
        }
        Area result = new Area();
        JSONObject object = page.getJSONObject("result");
        if (object == null) {
            return null;
        }
        result.formatted_address = object.getString("formatted_address");
        if (object.containsKey("addressComponent")) {
            result.country = object.getJSONObject("addressComponent").getString("country");
            //目前只需筛选到省，自治区
            result.province = object.getJSONObject("addressComponent").getString("province");
            //目前只需筛选到省，自治区
            result.city = object.getJSONObject("addressComponent").getString("city");
            result.district = object.getJSONObject("addressComponent").getString("district");
            result.town = object.getJSONObject("addressComponent").getString("town");
            result.street = object.getJSONObject("addressComponent").getString("street");
            result.street_number = object.getJSONObject("addressComponent").getString("street_number");
        }
        return result;
    }
}