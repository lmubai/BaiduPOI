import NetConst.URLUtils;
import model.Area;
import model.Point;
import model.Rectangle;
import model.StoreModel;
import utils.Cosnt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static utils.FileUtils.writeIntoCSV;

/**
 * Created by liyonglin on 2017/10/26.
 */
public class Main {


    public static void main(String[] args) throws SQLException {
        long startTime = System.currentTimeMillis();
        //write,表头
        writeHeader();
        List<Rectangle> tasklist = getTaskList();
        for (int i = 0; i < tasklist.size(); i++) {
            GetDatas.getByBounds(tasklist.get(i));
        }
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;
        StringBuilder sb = new StringBuilder();
        sb.append(costTime % 1000).append("毫秒");
        if (costTime > 1000) {
            costTime = costTime / 1000;
            sb.insert(0, "秒");
            sb.insert(0, costTime % 60);
            System.out.println(" 并发总数 ： " + URLUtils.concurrentNum + "   每分钟并发量：" + (URLUtils.concurrentNum / costTime) * 60.0);
        }
        if (costTime > 60) {
            costTime = costTime / 60;
            sb.insert(0, "分钟");
            sb.insert(0, costTime % 60);

        }
        if (costTime > 60) {
            costTime = costTime / 60;
            sb.insert(0, "小时");
            sb.insert(0, costTime % 60);
        }
        System.out.println("任务耗时" + sb.toString());
    }


    /**
     * 地区范围
     *
     * @return
     */
    public static List<Rectangle> getTaskList() {
        List<Rectangle> area = Cosnt.getCuttedAreaList(new Rectangle(new Point(109.133145,29.22902), new Point(116.271297,33.33572)));
        return area;

    }

    public static void writeHeader() {
        List<StoreModel> rowList = new ArrayList<StoreModel>(20);
        StoreModel header = new StoreModel();
        header.formatted_address = "综合地址";
        header.storeName = "村庄";
        header.longitude = "经度";
        header.latitude = "纬度";
        header.areaInfo = new Area();
        header.areaInfo.province = "省份";
        header.areaInfo.city = "市";
        header.areaInfo.district = "区县";
        header.areaInfo.town = "乡镇";
        header.areaInfo.street = "街道";
        header.areaInfo.street_number = "街道门牌号";
//        header.areaInfo.adcode = "邮编代码";
        rowList.add(header);
        writeIntoCSV(rowList);
    }


}
