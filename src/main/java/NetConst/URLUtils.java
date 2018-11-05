package NetConst;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * Created by liyonglin on 2017/10/26.
 */
public class URLUtils {

    public static String fileName = null;
    public static String keyword = null;
    public static String API_KEY = null;
    public static String task_list = null;
    public static String out_path = null;

    static {
        Properties properties = new Properties();
        try {
            String path=URLUtils.class.getClassLoader().getResource("config.properties").toURI().getPath();
            System.out.println(path);
            properties.load(new FileInputStream(path));
            fileName = properties.getProperty("fileName");
            keyword = new String(properties.getProperty("keyword").getBytes("iso-8859-1"),"gbk");
            API_KEY = properties.getProperty("API_KEY");
            task_list = properties.getProperty("task_list");
            out_path=properties.getProperty("out_path");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(fileName);
        System.out.println(keyword);
        System.out.println(API_KEY);
        System.out.println(task_list);
    }
    /**
     * 请求并发量
     */
    public static double concurrentNum = 0.0d;

    public static String geocoder = "http://api.map.baidu.com/geocoder/v2/?output=json&pois=1&ak=" + API_KEY;


    /**
     * @param lat
     * @param longtitude Note: final url looks like :  "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=39.934,116.329
     *                   &output=json&pois=1&ak=您的ak";
     */
    public static String geoCodeReversURL(String lat, String longtitude) {
        StringBuilder sb = new StringBuilder(geocoder);
        sb.append("&location=")
                .append(lat)
                .append(",")
                .append(longtitude);

        return sb.toString();

    }

    public static String sendURLWithParams(String url) {
        //访问返回结果
        String result = "";
        //读取访问结果
        BufferedReader read = null;
        try {
            //创建url
            URL realurl = new URL(url);
            //打开连接
            URLConnection connection = realurl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //建立连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;//循环读取
            while ((line = read.readLine()) != null) {
                result += line;
            }
            concurrentNum++;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
/*        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return result;
    }
}
