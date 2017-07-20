import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 王麟东 on 2017/5/8 0008.
 */
public class ImageToolsDriver {

    public static void main(String args[]) throws Exception{
        String path = "image";
        jiandan(path);
        youmeizi(path);
    }

    public static void jiandan(String path){
        String url = "http://jandan.net/ooxx";
        int max = 100;

        int count = 0;
        for (int i = 0; i < max; i ++){
            //获取网页并得到Document对象
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            }catch (IOException e){
                System.out.println(url + "请求失败");
            }

            //获取所有img标签
            Elements elements = doc.getElementsByTag("img");
            for (Element element : elements){
                //获取标签中src属性的绝对路径
                String imgSrc = element.attr("abs:src");
                if (downloadImage(imgSrc, path)){
                    count ++;
                }
            }

            try {
                url = doc.getElementsByClass("previous-comment-page").get(0)
                        .getElementsByTag("a").attr("abs:href");
            }catch (Exception e){
                System.out.println("没链接了~");
                break;
            }
        }
        System.out.println("下载了" + count + "张图片~");
    }

    public static void youmeizi(String path){
        String url = "http://www.youmzi.com/tuinvlang.html";
        int max = 1;

        int count = 0;
        for (int i = 0; i < max; i ++){
            //获取网页并得到Document对象
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            }catch (IOException e){
                System.out.println(url + "请求失败");
            }

            Elements imageUrl = doc.getElementsByClass("tzpic3-mzindex").get(0).getElementsByTag("a");
            ArrayList<String> urlList = new ArrayList<String>();
            for (Element element : imageUrl){
                urlList.add(element.attr("abs:href"));
                System.out.println(element.attr("abs:href"));
            }

            for (String s : urlList){
                String next = s;
                while (true){
                    try {
                        doc = Jsoup.connect(next).get();
                    }catch (IOException e){
                        System.out.println(url + "请求失败");
                    }
                    Element e = doc.getElementsByClass("arpic").get(0);
                    //获取所有img标签
                    Elements elements = e.getElementsByTag("img");
                    for (Element element : elements){
                        //获取标签中src属性的绝对路径
                        String imgSrc = element.attr("abs:src");
                        if (downloadImage(imgSrc, path)){
                            count ++;
                        }
                    }
                    String tmp = next;
                    try {
                        Elements nextPage = doc.getElementsByClass("jogger2").get(0).getElementsByTag("a");

                        next = null;
                        for (Element element : nextPage){
                            //获取标签中src属性的绝对路径
                            if (element.text().equals("下一页")){
                                next = element.attr("abs:href");
                            }
                        }
                    }catch (Exception ex){
                        System.out.println("没链接了~");
                        break;
                    }
                    if (next == null || tmp.equals(next)){
                        break;
                    }

                }

            }

            try {
                Elements nextPage = doc.getElementsByClass("jogger").get(0).getElementsByTag("a");
                url = null;
                for (Element element : nextPage){
                    //获取标签中src属性的绝对路径
                    if (element.text().equals("下一页")){
                        url = element.attr("abs:href");
                    }
                }

            }catch (Exception e){
                System.out.println("没链接了~");
                break;
            }
            if (url == null){
                break;
            }
        }
        System.out.println("下载了" + count + "张图片~");
    }

    /**
     * 下载图片
     * @param imageUrl image链接
     * @param path 保存路径
     * @throws Exception
     */

    public static boolean downloadImage(String imageUrl, String path) {
        try {
            //分割字符串，获得文件名
            String filePath = path + imageUrl.substring(imageUrl.lastIndexOf("/"));
            //获得文件流
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            InputStream in = connection.getInputStream();

            //写入本地文件
            File file = new File(filePath);
            FileOutputStream out = new FileOutputStream(file);
            int i = 0;
            while ((i = in.read()) != -1){
                out.write(i);
            }
            System.out.println(imageUrl + "下载成功");
            out.close();
            in.close();
            return true;
        }catch (Exception e){
            System.out.println(imageUrl + "下载失败");
            return false;
        }

    }

}
