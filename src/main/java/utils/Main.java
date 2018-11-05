package utils;

import NetConst.URLUtils;
import model.Area;
import model.Point;
import model.Rectangle;
import model.StoreModel;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static utils.FileUtils.writeIntoCSV;

/**
 * Created by liyonglin on 2017/10/26.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        //write,表头
        writeHeader();
        //存取经纬度到txt文本
//        storeTaskToTXT();
        List<Rectangle> tasklist = new ArrayList<Rectangle>();
        readTaskList(tasklist);
        for (Rectangle rectangle : tasklist) {
            GetDatas.getByBounds(rectangle);
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

    private static void readTaskList(List<Rectangle> tasklist) throws IOException {
        // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
        FileInputStream fis = new FileInputStream(URLUtils.task_list);
        // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
            if (StringUtils.isNotEmpty(line)) {
                String x = line.replaceAll("\\{", "").replaceAll("}", "");
                String[] split = x.split(",");
                Point leftbottom = new Point(Double.valueOf(split[0]), Double.valueOf(split[1]));
                Point rightTop = new Point(Double.valueOf(split[2]), Double.valueOf(split[3]));
                Rectangle rectangle = new Rectangle(leftbottom, rightTop);
                tasklist.add(rectangle);
            }
        }
        br.close();
        isr.close();
        fis.close();
    }

    private static void storeTaskToTXT() {
        List<Rectangle> tasklist = getTaskList();
        int i = 0;
        String filePath = "G:\\POITask\\TaskList";
        for (Rectangle rectangle : tasklist) {
            try {
                if (i % 20 == 0) {
                    filePath = "G:\\POITask\\TaskList" + i;
                }
                writeContentToTxt(rectangle.leftbottom.toString() + "," + rectangle.rightTop.toString(), filePath + ".txt");
                i++;
            } catch (Exception e) {
            }
        }
    }


    /**
     * 地区范围
     *
     * @return
     */
    public static List<Rectangle> getTaskList() {
        List<Rectangle> area = Cosnt.getCuttedAreaList(new Rectangle(new Point(109.133145, 29.22902), new Point(116.271297, 33.33572)));
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
        header.tag = "标签";
//        header.areaInfo.adcode = "邮编代码";
        rowList.add(header);
        writeIntoCSV(rowList);
    }
    public static void writeContentToTxt(String content, String filePath) {
        FileWriter fw = null;
        try {
            // 如果文件存在，则追加内容；如果文件不存在，则创建文件
            // File f = new File("E:\\hubeiCasePenaltyBackup.txt");
            File f = new File(filePath);
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(content + "\n");
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
