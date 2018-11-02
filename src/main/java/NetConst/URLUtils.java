package NetConst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by liyonglin on 2017/10/26.
 */
public class URLUtils {

    /**
     * 请求并发量
     */
    public static double concurrentNum = 0.0d;
    public static String API_KEY = "5K8C4T0cf1xPDDmhFENqGeGZQ3YiVYAs";

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
