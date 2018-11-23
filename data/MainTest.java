package com;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import me.midday.FoolNLTK;
import me.midday.lexical.Entity;
import me.midday.lexical.LexicalAnalyzer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.displaytag.util.ParamEncoder;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.ocr.AipOcr;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Other.AhoCorasickDoubleArrayTrieSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.ksource.core.encrypt.EncryptUtil;
import com.ksource.core.fulltext.impl.PdfImpl;
import com.ksource.core.util.FileUtil;
import com.ksource.core.util.HanlpPropertiesUitl;
import com.ksource.core.util.PdfToImg;
import com.ksource.core.util.StringUtil;
import com.ksource.platform.job.ClueDomainJudgeJob;
import com.ksource.pwlp.dao.collect.CasePenaltyDao;
import com.ksource.pwlp.model.collect.CasePenalty;
import com.ksource.pwlp.model.judge.CsobjCoords;
import com.ksource.pwlp.model.repository.CaseInfoRep;
import com.ksource.pwlp.util.ImgCompareUtil;

/**
 * @author:LXL
 */
public class MainTest {

	@Resource
	private CasePenaltyDao casePenaltyDao;

	@Test
	public void test1() throws ParseException {
		CasePenalty cp1 = new CasePenalty();
		CasePenalty cp2 = new CasePenalty();
		cp2.setCaseId("skdfjlksdf");
		cp1 = cp2;
		System.out.println(cp1.getCaseId());
	}

	@Test
	public void testFile() {
		String filePath = "F:/123.png";
		String filePath2 = "F:/abc.png";
		File file = new File(filePath);
		File file2 = new File(filePath2);
		copyFile(file, file2);
	}

	/**
	 * 复制文件
	 * 
	 * @param oldFile
	 * @param newFile
	 */
	public void copyFile(File oldFile, File newFile) {
		InputStream inputStream = null;
		BufferedInputStream bufferedInputStream = null;

		OutputStream outputStream = null;
		BufferedOutputStream bufferedOutputStream = null;

		try {
			inputStream = new FileInputStream(oldFile);
			bufferedInputStream = new BufferedInputStream(inputStream);

			outputStream = new FileOutputStream(newFile);
			bufferedOutputStream = new BufferedOutputStream(outputStream);

			byte[] b = new byte[1024]; // 代表一次最多读取1KB的内容

			int length = 0; // 代表实际读取的字节数
			while ((length = bufferedInputStream.read(b)) != -1) {// 读取并存到b里面
				// length 代表实际读取的字节数
				bufferedOutputStream.write(b, 0, length);
			}
			// 缓冲区的内容写入到文件
			bufferedOutputStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (bufferedOutputStream != null) {
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 测试文件目录
	 */
	@Test
	public void test() throws Exception {
		File f = new File("E:\\hubeiCasePenaltyBackup.txt");
		FileReader fileReader = new FileReader(f);
		BufferedReader reader = new BufferedReader(fileReader);
		String line = "";
		while ((line = reader.readLine()) != null) {
			File file = new File("E:\\" + line);
			if (!file.exists()) {
				this.writeContentToTxt(line, "E:\\needCopyFile.txt");
			}
		}
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
		pw.println(content);
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testClass() {
		JSONObject jsonObject = new JSONObject();
		String msg = "lxl";
		jsonObject.put("flag", true);
		jsonObject.put("msg", msg);
		System.out.println(jsonObject);
	}

	@Test
	public void testDigist() {
		System.out.println("请输入一个整数");
		int i = 9841818;
		// 忽略数据校验和负数，负数请使用绝对值
		// 计数器，因为不考虑负数且while条件控制在i >= 10， 那么无法进入循环的默认是1位数
		int j = 1;
		// 如果比10大，进入循环
		while (i >= 10) {
			// i 除以 10，自动忽略余数，再将结果赋值给i。 即：将i的末位数抹去。
			i = i / 10;
			// 计数器自增
			j++;
		}
		System.out.println("这是个" + j + "位数");
	}

	@Test
	public void testAssert() {
		// test data
		int num = 5;
		String temp = null;
		String str = "Junit is working fine";
		// check for equality
		assertEquals("Junit is working fine", str);
		// check for false condition
		assertFalse(num > 1);
		// check for not null value
		assertNotNull(str);
	}

	@Test
	public void testGetSign() {
		String jsapi_ticket = "jsapi_ticket";
		// 注意 URL 一定要动态获取，不能 hardcode
		String url = "http://example.com";
		Map<String, String> ret = sign(jsapi_ticket, url);
		for (Map.Entry entry : ret.entrySet()) {
			System.out.println(entry.getKey() + ", " + entry.getValue());
		}
	}

	public static Map<String, String> sign(String jsapi_ticket, String url) {
		Map<String, String> ret = new HashMap<String, String>();
		String nonce_str = create_nonce_str();
		String timestamp = create_timestamp();
		String string1;
		String signature = "";

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
		System.out.println(string1);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("jsapi_ticket", jsapi_ticket);
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	private static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

	private static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	/**
	 * 追加文件：使用FileWriter
	 *
	 * @param fileName
	 * @param content
	 */
	public void writeContent(String fileName, String content) {
		FileWriter writer = null;
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(fileName, true);
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testString() {
		String name = "湖北省丹江口市丹赵路办事处杨山生态林场";
		System.out.println(name.substring(0, name.indexOf("办事处") + "办事处".length()));
		System.out.println("　　蕲春县路口村".replaceAll("　", ""));
		String result = getResult("http://api.map.baidu.com/place/v2/suggestion?output=json&ak=ITq54s4iM9rL1fTZCDw1GfG6VPIVpYsK&query=%E5%AE%8B%E5%BA%84%E6%9D%91&region=%E6%B9%96%E5%8C%97%E7%9C%81&city_limit=false");
		JSONArray jsonArray = JSON.parseArray(result);
		JSONArray newJsonArray = new JSONArray();
		System.out.println(newJsonArray.isEmpty());
		for (Object o : jsonArray) {
			JSONObject jsonObject = (JSONObject) o;
			System.out.println(jsonObject);
			JSONObject location = jsonObject.getJSONObject("location");
			if (null != location) {
				if ("湖北省".equals(jsonObject.getString("province"))) {
					newJsonArray.add(jsonObject);
				}
			}
		}
		System.out.println("=======================================================================================================================");
		for (Object o : newJsonArray) {
			JSONObject jsonObject = (JSONObject) o;
			System.out.println(jsonObject);
		}
	}

	public static String getResult(String url) {
		HttpClient httpClient = null;
		HttpGet httpGet = new HttpGet(url);
		String httpResult = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse != null) {
				HttpEntity resEntity = httpResponse.getEntity();
				if (resEntity != null) {
					httpResult = EntityUtils.toString(resEntity, "utf-8");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		JSONObject jsonObject = JSON.parseObject(httpResult);
		String result = jsonObject.getString("result");
		return result;
	}

	@Test
	public void testListFiles() {
		this.printFileInfo("E:\\笔记\\2.日常问题\\6.公司项目\\2017.7-12");
	}

	public void printFileInfo(String fileDir) {
		List<File> fileList = new ArrayList<File>();
		File file = new File(fileDir);
		File[] files = file.listFiles();// 获取目录下的所有文件或文件夹
		if (files == null) {// 如果目录为空，直接退出
			return;
		}
		// 遍历，目录下的所有文件
		for (File f : files) {
			if (f.isFile()) {
				fileList.add(f);
			} else if (f.isDirectory()) {
				// System.out.println(f.getAbsolutePath());
				printFileInfo(f.getAbsolutePath());
			}
		}
		for (File f1 : fileList) {
			System.out.println(f1.getName());
		}
	}

	@Test
	public void testDecode() {
		String orgIdStr = this.decodeData("b3JnSWQ9MTAwMDAxMjQ2NjAxODU=");
		// System.out.println(orgIdStr);
		// System.out.println(Long.valueOf(orgIdStr.substring(orgIdStr.lastIndexOf("=")
		// + 1, orgIdStr.length())));
		String str = "银行业存款类金融机构协助查冻扣账户数量[3041900001#01]<=银行业存款类金融机构协助查询账户数量[3041900001#b1]+银行业存款类金融机构协助冻结账户数量[3041900001#b2]+银行业存款类金融机构协助扣划账户数量[3041900001#b3]";
		String str1 = "1000.000000万元人民币";
		String reg = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(reg);
		Matcher mat = pat.matcher(str1);
		String repickStr = mat.replaceAll("");
		Double d = Double.valueOf(repickStr);
		DecimalFormat format = new DecimalFormat("0.0000");
		repickStr = format.format(d) + "万";
		System.out.println("去中文后:" + repickStr);
	}

	// base64解码
	private String decodeData(String inputData) {
		try {
			if (null == inputData) {
				return null;
			}
			return new String(org.apache.commons.codec.binary.Base64.decodeBase64(inputData.getBytes("UTF-8")), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	@Test
	public void testOCR() {
		// 设置APPID/AK/SK
		String APP_ID = "11415114";
		String API_KEY = "5FcoyUgIbZP30ChuHBVXR43i";
		String SECRET_KEY = "lAz9aq5nrsLtFCv70Sg9BiqhjaSHC5hn ";

		// 初始化一个AipOcr
		AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);

		// 可选：设置代理服务器地址, http和socket二选一，或者均不设置
		// client.setHttpProxy("proxy_host", proxy_port); // 设置http代理
		// client.setSocketProxy("proxy_host", proxy_port); // 设置socket代理

		// 可选：设置log4j日志输出格式，若不设置，则使用默认配置
		// 也可以直接通过jvm启动参数设置此环境变量
		// System.setProperty("aip.log4j.conf",
		// "path/to/your/log4j.properties");

		// 调用接口
		String path = "G:\\ocr\\香榭丽舍足浴城 001.jpg";
		org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
		System.out.println(res.toString(2));
	}

	@Test
	public void testJsonArray() {
		JSONArray jsonArray1 = new JSONArray();
		JSONArray jsonArray2 = new JSONArray();
		JSONArray jsonArray3 = new JSONArray();
		JSONObject j1 = new JSONObject();
		j1.put("add1", "sdfsdf");
		JSONObject j2 = new JSONObject();
		j2.put("add1", "ksdf");
		jsonArray1.add(j1);
		jsonArray2.add(j2);
		String a = "[{\"id\":\"152579059718742\",\"name\":\"2017-92侯以达.pdf\"}]";
		try {
			JSONArray jsonArray = JSONArray.parseArray(a);
			for (Object o : jsonArray) {
				JSONObject jsonObject = (JSONObject) o;
				System.out.println(jsonObject.get("id"));
				System.out.println(jsonObject.get("name"));
				System.out.println(o);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Test
	public void googleColTestMain() {
		/**
		 * softKeys weakValues 可以设置key跟value的strong，soft，weak属性。不错不错。
		 * expiration(3, TimeUnit.SECONDS)设置超时时间为3秒
		 *
		 */
		ConcurrentMap<String, String> testMap = new MapMaker().concurrencyLevel(32).softKeys().weakValues().expiration(3, TimeUnit.SECONDS).makeComputingMap(new Function<String, String>() {
			/**
			 * 这里就是绑定的根据key没找到value的时候触发的function， 可以将这里的返回值放到对应的key的value中！
			 *
			 * @param arg0
			 * @return
			 */
			@Override
			public String apply(String arg0) {
				return "create:" + arg0;
			}
		});
		testMap.put("a", "testa");
		testMap.put("b", "testb");
		System.out.println(testMap.get("a"));
		System.out.println(testMap.get("b"));
		System.out.println(testMap.get("c"));
		/**
		 * 这里sleep4秒钟过后， 缓存都失效，再get就会根据绑定的function去获得value放在map中了。
		 */
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/**
		 * 看看这里的再输出，是不是就是新的值了！~
		 */
		System.out.println(testMap.get("a"));
		System.out.println(testMap.get("b"));
		System.out.println(testMap.get("c"));
	}

	@Test
	public void testEncode() throws Exception {
		String a = "kbTRQoI/fSDF8I32kSLeQ/NfBXqYjZYZ9tMThIXJogM=";
		// System.out.println(EncryptUtil.decrypt(a));
		System.out.println(EncryptUtil.encryptSha256("000000"));
		File f = new File("D:\\attachment\\baoding\\pwlp\\ueditor\\20180711");
		System.out.println(f.getParent());
	}

	@Test
	public void testCompanyDomain() {
		String s = "食品反使武双急据情文;电委、通信图自动控制武双急据、尚君;技算机武双尚君、武双情文;农业反使急据和试者君活;畜牧业反使急据情文;水祖业反使急据情文;教使设起的急据尚君;农业武双钱广情文;农业武双尚君情文;农业武双咨询、交士情文;农业武双须科情文;生物武双钱广情文;生物武双尚君情文;生物武双咨询、交士情文;生物武双须科情文;信增电委武双情文;反武信增咨询情文;软示情文;软示批君;软示零售;软示尚君;实者找析仪器制时;农没牧渔机械配示制时;农没牧渔专用仪器仪表制时;药物检测仪器制时;通用和专用仪器仪表的元示、器示制时;仪器仪表修理;仪器仪表批君;信增武双咨询情文;集成电就设技;信增好统集成情文;商品批君贸点（斗可审批类商品花满）;贸点代理;化使试剂和助剂制时（监控化使品、危险化使品花满）;看座以下小轿车销售;食品检测情文;(依光让产批军的项天，产相响写乎批军法方可尚活产会金动)〓";
		System.out.println(ClueDomainJudgeJob.getCompanyClueDomain(s));
	}

	@Test
	public void testTableId() {
		// ParamEncoder paramEncoder = new ParamEncoder("caseJudgeItem");
		ParamEncoder paramEncoder = new ParamEncoder("publicOpionItem");
		String tableIdCode = paramEncoder.encodeParameterName("");
		System.out.println(tableIdCode);
	}

	@Test
	public void testPdfToPic() {
		try {
			Long begin = System.currentTimeMillis();
			// PdfToImg.pdfToimageOne(new File("G:\\ocr\\pdf\\国土-82.pdf"));
			String filepath = "G:\\ocr\\pdf\\国土-82.pdf";
			List<String> strings = PdfToImg.pdfToimageMore(filepath, 1);
			for (String string : strings) {
				System.out.println(string);
			}
			Long end = System.currentTimeMillis();
			System.out.println("convert done!All time:" + (end - begin));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSplit() {
		File file = new File("G:\\ocr\\pdf\\国土-82");
		File[] files = file.listFiles();
		for (File file1 : files) {
			System.out.println(file1);
		}
	}

	@Test
	public void testPdf() {
		PdfImpl pdf = new PdfImpl();
		pdf.setFileName("G:\\ocr\\pdf\\国土-82.pdf");
		System.out.println(pdf.extract());
	}

	@Test
	public void testPoi() throws Exception {
		String fullPath = "G:\\poi\\supervise\\2017.06\\2+宜昌市猇亭区检察院向猇亭区环保局\\审查报告.docx";
		String docText = "";
		FileInputStream fis = null;
		File file = new File(fullPath);
		if (file.exists()) {
			if (fullPath.endsWith(".doc")) {
				try {
					fis = new FileInputStream(file);
					HWPFDocument doc = new HWPFDocument(fis);
					Range rang = doc.getRange();
					docText = rang.text();
					readPictureByDoc(doc, fullPath);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					fis.close();
				}
			} else if (fullPath.endsWith(".docx")) {
				try {
					fis = new FileInputStream(file);
					XWPFDocument xdoc = new XWPFDocument(fis);
					XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
					docText = extractor.getText().trim();
					List<XWPFPictureData> picList = xdoc.getAllPictures();
					if (picList.size() > 0) {// 含有图片提取图片信息
						readPictureByDocx(picList, fullPath);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					fis.close();
				}
			}
			if (!"".equals(docText.trim())) {
				docText = docText.replaceAll(" +", "");
				docText = docText.replace("", "");
				Pattern p = Pattern.compile("(\r|\n){1,}");
				Matcher m = p.matcher(docText);
				docText = m.replaceAll("\r");
				if (docText.startsWith("\r")) {
					docText = docText.substring(1, docText.length());
				}
				if (docText.endsWith("\r")) {
					docText = docText.substring(0, docText.length() - 1);
				}

				int index = 0;
				for (int i = 0; i < 3; i++) {
					index = docText.indexOf("\r");
					if (index != -1) {
						docText = docText.substring(docText.indexOf("\r") + 1, docText.length());
					}
				}
				docText = docText.replaceAll("\r", "<br />");
			}
		}
		System.out.println(docText);
	}

	private void readPictureByDoc(HWPFDocument doc, String fullPath) throws Exception {
		int length = doc.characterLength();
		PicturesTable pTable = doc.getPicturesTable();
		for (int i = 0; i < length; i++) {
			Range range = new Range(i, i + 1, doc);
			CharacterRun cr = range.getCharacterRun(0);
			if (pTable.hasPicture(cr)) {
				Picture pic = pTable.extractPicture(cr, false);
				String afileName = pic.suggestFullFileName();
				OutputStream out = new FileOutputStream(new File("G:\\poi\\supervise\\2017.06\\2+宜昌市猇亭区检察院向猇亭区环保局\\docpic\\" + afileName));
				pic.writeImageContent(out);
			}
		}
	}

	private void readPictureByDocx(List<XWPFPictureData> picList, String fullPath) {
		try {
			for (XWPFPictureData pic : picList) {
				byte[] bytev = pic.getData();
				FileOutputStream fos = new FileOutputStream("G:\\poi\\supervise\\2017.06\\2+宜昌市猇亭区检察院向猇亭区环保局\\docxpic\\" + pic.getFileName());
				fos.write(bytev);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFileName() {
		Map<String, Object> paramMap = new HashMap<>();
		String filepath = "G:\\poi\\supervise\\2017.06\\2+宜昌市猇亭区检察院向猇亭区环保局\\docpic\\test.doc";
		String basePath = filepath.substring(0, filepath.lastIndexOf("\\") + 1);
		String fileName = filepath.substring(filepath.lastIndexOf("\\") + 1, filepath.lastIndexOf("."));
		String fileExt = filepath.substring(filepath.lastIndexOf("."));
		paramMap.put("basePath", basePath);
		paramMap.put("fileName", fileName);
		paramMap.put("fileExt", fileExt);
		System.out.println(basePath);
		System.out.println(fileName);
		System.out.println(fileExt);
	}

	@Test
	public void testFileName1() throws Exception {
		// E:\IDEAProject\new\pwlp-hubei\data\wordVec.txt
		WordVectorModel wordVectorModel = new WordVectorModel("E:\\IDEAProject\\new\\pwlp-hubei\\data\\wordVec.txt");
		DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
		float similarity = docVectorModel.similarity("湖北长舟盐化有限公司大气污染物排放浓度超标", "废气直排,二氧化硫排放浓度超标,大气污染物排放浓度超标 ");
		System.out.println(similarity);
		// System.out.println(getInfoSimilarity("销售, 食品, 未标注生产日期","未标注生产日期"));
	}

	@Test
	public void testPowerListContent() {
		long start = System.currentTimeMillis();
		String t = "<p style=\"text-indent: 2em;\"><span style=\"font-family: 微软雅黑, \" microsoft=\"\" font-size:=\"\">【法律】《中华人民共和国药品管理法》（2015年4月24日修订）第十六条药品经营企业必须按照国务院药品监督管理部门依据本法制定的《药品经营质量管理规范》经营药品。药品监督管理部门按照规定对药品经营企业是否符合《药品经营质量管理规范》的要求进行认证；对认证合格的，发给认证证书。《药品经营质量管理规范》的具体实施办法、实施步骤由国务院药品监督管理部门规定； 　 【法规】1.《中华人民共和国药品管理法实施条例》（2002年9月15日施行国务院令第360号）第十三条省、自治区、直辖市人民政府药品监督管理部门负责组织药品经营企业的认证工作。药品经营企业应当按照国务院药品监督管理部门规定的实施办法和实施步骤，通过省、自治区、直辖市人民政府药品监督管理部门组织的《药品经营质量管理规范》的认证，取得认证证书。《药品经营质量管理规范》认证证书的格式由国务院药品监督管理部门统一规定。新开办药品批发企业，应当自取得《药品经营许可证》之日起30日内，向发给其《药品经营许可证》的药品监督管理部门或者药品监督管理机构申请《药品经营质量管理规范》认证。省、自治区、直辖市人民政府药品监督管理部门应当自收到认证申请之日起3个月内，按照国务院药品监督管理部门的规定，组织对申请认证的药品批发企业是否符合《药品经营质量管理规范》进行认证；认证合格的，发给认证证书； 【规章】1.《药品经营质量管理规范》（2015年国家食品药品监督管理总局令第13号）； 【规范性文件】《药品经营质量管理规范认证管理办法》(国食药监市[2003]25号)第三十八条：《药品经营质量管理规范认证证书》有效期5年，有效期满前3个月内，由企业提出重新认证的申请。省、自治区、直辖市药品监督管理部门依照本办法的认证程序，对申请企业进行检查和复审，合格的换发证书。审查不合格以及认证证书期满但未重新申请认证的，应收回或撤销原认证证书，并按照本办法第三十七条的规定予以公布。</span></p>\n";
		long end = System.currentTimeMillis();
		System.out.println("*****知识库关联关系初始化完成,共耗时【" + (end - start) + "】*****");
		Set<String> words = this.getWords(t, 1);
		long start1 = System.currentTimeMillis();
		System.out.println(words.contains("《中华人民共和国药品管理法》"));
		long end1 = System.currentTimeMillis();
		System.out.println("*****知识库关联关系初始化完成,共耗时【" + (end1 - start1) + "】*****");
		for (String word : words) {
			System.out.println(word);
		}
	}

	/**
	 * 获取书名号关键字 如 《无照经营查处取缔办法》第三十九条第五款第四项
	 * ，《河北省大气污染防治条例》第二十五条、第二十六条第三、四、五、六项、第二十七条 默认只取书名号内容
	 *
	 * @param str
	 * @return
	 */
	private Set<String> getWords(String str, int type) {
		Set<String> set = new LinkedHashSet<String>();
		if (StringUtil.isEmpty(str)) {
			return set;
		}
		Pattern p = null;
		str = str.replace("《", "《 ");
		p = Pattern.compile("(《([^》]*)》)");
		Matcher m = p.matcher(str);
		StringBuffer s = new StringBuffer();
		while (m != null && m.find()) {
			String matchStr = m.group();
			set.add(matchStr.replace("《 ", "《"));
		}
		return set;
	}

	@Test
	public void testGetAll() {
		String d = "2018-08-29 06:53";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			System.out.println(sdf.parse(d));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWhileEach() {
		Set<Long> longTreeSet = new TreeSet<>();
		longTreeSet.add(1l);
		longTreeSet.add(1l);
		longTreeSet.add(2l);
		System.out.println(longTreeSet);
		System.out.println(longTreeSet.contains(1l));
		int i = 2;
		while (i > 0) {
			System.out.println(2);
			i--;
		}
	}

	/**
	 * 测试地名获取
	 */
	@Test
	public void testTrimePlace() throws Exception {
		System.out.println(HanlpPropertiesUitl.getPlaceExtractFilterWord());
	}

	/**
	 * 解析地址
	 * 
	 * @param address
	 * @return
	 */
	public List<Map<String, String>> addressResolution(String address) {
		// String
		// regex="((?<province>[^省]+省|.+自治区)|上海|北京|天津|重庆)(?<city>[^市]+市|.+自治州)(?<county>[^县]+县|.+区|.+镇|.+局)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
		String regex = "((?<province>[^省]+省|.+自治区)|上海|北京|天津|重庆)|(?<city>[^市]+市|.+自治州)|(?<county>[^县]+县|.+区|.+镇|.+局)|(?<town>[^区]+区|.+镇)|(?<village>.*)";
		Matcher m = Pattern.compile(regex).matcher(address);
		String province = null, city = null, county = null, town = null, village = null;
		List<Map<String, String>> table = new ArrayList<Map<String, String>>();
		Map<String, String> row = null;
		while (m.find()) {
			row = new LinkedHashMap<String, String>();
			province = m.group("province");
			row.put("province", province == null ? "" : province.trim());
			city = m.group("city");
			row.put("city", city == null ? "" : city.trim());
			county = m.group("county");
			row.put("county", county == null ? "" : county.trim());
			town = m.group("town");
			row.put("town", town == null ? "" : town.trim());
			village = m.group("village");
			row.put("village", village == null ? "" : village.trim());
			table.add(row);
		}
		return table;
	}

	@Test
	public void testPlace() {
		List<CsobjCoords> csobjCoordsList = new ArrayList<>();
		CsobjCoords c1 = new CsobjCoords();
		c1.setCsobjName("123456");
		c1.setCoordinate("ksd");
		CsobjCoords c2 = new CsobjCoords();
		c2.setCsobjName("12345678");
		CsobjCoords c3 = new CsobjCoords();
		c3.setCsobjName("123456789");
		c3.setCoordinate("ksdjlfls");
		CsobjCoords c4 = new CsobjCoords();
		c4.setCsobjName("12345678910");
		csobjCoordsList.add(c1);
		csobjCoordsList.add(c2);
		csobjCoordsList.add(c3);
		csobjCoordsList.add(c4);
		// 按照名字长短排序
		Collections.sort(csobjCoordsList, new Comparator<CsobjCoords>() {
			@Override
			public int compare(CsobjCoords o1, CsobjCoords o2) {
				if (StringUtil.isEmpty(o1.getCoordinate()) && StringUtil.isNotEmpty(o2.getCoordinate())) {
					return 1;
				}
				if (StringUtil.isNotEmpty(o1.getCoordinate()) && StringUtil.isEmpty(o2.getCoordinate())) {
					return -1;
				}
				return o2.getCsobjName().length() - o1.getCsobjName().length();
			}
		});
		System.out.println(csobjCoordsList.get(0));
		System.out.println(JSONObject.toJSONString(csobjCoordsList));
	}

	private Set<String> removeIllegalPlace(Set<String> x) {
		Set<String> result = new HashSet<>();
		for (String s : x) {
			Segment segment = HanLP.newSegment();
			List<Term> termList = segment.seg(s);
			boolean flag = false;
			for (Term term : termList) {
				try {
					if (Nature.ns.equals(term.nature)) {
						flag = true;
					}
					if (flag)
						result.add(s);
				} catch (Exception e) {
					result.add(s);
				}
			}
		}
		return result;
	}

	public static Segment placeSegment = null;

	public static Segment placeSegment() {
		if (placeSegment == null) {
			placeSegment = HanLP.newSegment().enablePlaceRecognize(true).enableOrganizationRecognize(true);
		}
		return placeSegment;
	}

	public static Map<String, String> initPlace(String caseInfo) {
		Map<String, String> placeMap = new HashMap<>();
		Segment segment = placeSegment();
		List<Term> termList = segment.seg(caseInfo);
		StringBuffer sb = new StringBuffer();
		for (Term term : termList) {
			if (Nature.ns.equals(term.nature) || Nature.nt.equals(term.nature) || Nature.ntc.equals(term.nature) || Nature.ntcb.equals(term.nature) || Nature.ntcf.equals(term.nature)
					|| Nature.ntch.equals(term.nature) || Nature.nth.equals(term.nature) || Nature.nto.equals(term.nature) || Nature.nts.equals(term.nature) || Nature.ni.equals(term.nature)
					|| Nature.nic.equals(term.nature) || Nature.nis.equals(term.nature) || Nature.nit.equals(term.nature) || Nature.f.equals(term.nature) || Nature.gg.equals(term.nature)
					|| Nature.ntu.equals(term.nature)) {
				sb.append("ns").append(term.word);
			} else if (Nature.w.equals(term.nature) || Nature.p.equals(term.nature) || Nature.v.equals(term.nature) || Nature.vn.equals(term.nature) || Nature.ude1.equals(term.nature)
					|| Nature.f.equals(term.nature) || Nature.nz.equals(term.nature) || Nature.vn.equals(term.nature) || Nature.d.equals(term.nature) || Nature.nnt.equals(term.nature)) {
				sb.append(",");
			} else {
				sb.append(term.word);
			}
		}
		String[] placeNames = sb.toString().split(",");
		for (String placeName : placeNames) {
			if (placeName.contains("ns")) {
				placeName = placeName.substring(placeName.indexOf("ns")).replace("ns", "");
				differNature(placeMap, placeName);
			}
		}
		return placeMap;
	}

	private static void differNature(Map<String, String> placeMap, String placeName) {
		Segment st = HanLP.newSegment();
		List<Term> tt = st.seg(placeName);
		// 区分机构
		if (tt.get(tt.size() - 1).nature.equals(Nature.nis)) {
			Segment st2 = HanLP.newSegment().enableOrganizationRecognize(true);
			List<Term> tt2 = st2.seg(placeName);
			if (tt2.get(tt2.size() - 1).nature.equals(Nature.nto)) {
				placeMap.put(placeName, "zfjg");
			} else {
				placeMap.put(placeName, "qtjg");
			}
		} else {
			placeMap.put(placeName, "dm");
		}
	}

	/**
	 * 比较案子是否相同
	 *
	 * @param caseInfoRep
	 * @param caseLibRep
	 */
	private void comareCaseEnd(CaseInfoRep caseInfoRep, CaseInfoRep caseLibRep, Set<Long> suspectEndCase) {
		String casePlace = caseInfoRep.getCasePlace();
		String caseCompany = caseInfoRep.getCaseCompany();
		String caseDate = caseInfoRep.getCaseDate();
		String casePeoplpe = caseInfoRep.getCasePeoplpe();
		String caseLibPlace = caseLibRep.getCasePlace();
		String caseLibCompany = caseLibRep.getCaseCompany();
		String caseLibDate = caseLibRep.getCaseDate();
		String caseLibPeoplpe = caseLibRep.getCasePeoplpe();
		if ("[]".equals(casePlace) || "[]".equals(caseDate) || ("[]".equals(casePeoplpe)) && "[]".equals(caseCompany)) {
			return;
		}
		boolean casePlaceFlag = false, caseDateFlag = false, caseCompanyFlag = false, casePeopleFlag = false;
		String[] casePlaceArray = casePlace.split(",");
		for (String s : casePlaceArray) {
			casePlaceFlag = caseLibPlace.contains(s);
		}
		String[] caseDateArray = caseDate.split(",");
		for (String s : caseDateArray) {
			caseDateFlag = caseLibDate.contains(s);
		}
		String[] caseCompanyArray = caseCompany.split(",");
		for (String s : caseCompanyArray) {
			caseCompanyFlag = caseLibCompany.contains(s);
		}
		String[] casePeoplpeArray = casePeoplpe.split(",");
		for (String s : casePeoplpeArray) {
			casePeopleFlag = caseLibPeoplpe.contains(s);
		}
		if ((casePlaceFlag && caseDateFlag) || caseCompanyFlag || casePeopleFlag) {
			suspectEndCase.add(caseLibRep.getCaseId());
		}
	}

	/**
	 * 测试地名获取
	 */
	@Test
	public void testTrimePlace1() {
		String address = "当事人从城北购进的“侏儒山牌黑芝麻油”，由于湖南省在经营过程中管理不到位，致使没能及时发现和处理未标明生产日期的食品。至本局查获时，当事人对外销售的“侏儒山牌黑芝麻油”外包装标签上未标明生产日期，其标注制造商：武汉市侏儒山食品有限公司，净含量：500毫升,数量4瓶。本局于2016年1月4日将上述未标明生产日期的食品依法予以扣押。现已查明，“侏儒山牌黑芝麻油”， 进价为14.00元/ 瓶，销价为18.00元/ 瓶，同时查明，已查实的涉案货值金额共计72.00元，由于当事人不能提供上述食品的进销货台账（或进销货记录），故违法所得无法计算。\n";
		String regex = "((?<province>[^省]+省|.+自治区)|上海|北京|天津|重庆)(?<city>[^市]+市|.+自治州)(?<county>[^县]+县|.+区|.+镇|.+局)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
		Matcher m = Pattern.compile(regex).matcher(address);
		String province = null, city = null, county = null, town = null, village = null;
		List<Map<String, String>> table = new ArrayList<>();
		Map<String, String> row = null;
		while (m.find()) {
			String orgName = m.group("province");
			System.out.println(orgName);
		}
	}

	@Test
	public void testOpenCV() {
		String beforeFilePath = "F:\\google\\opencv-processing-master\\examples\\ImageDiff\\450178.jpg";
		String afterFilePath = "F:\\google\\opencv-processing-master\\examples\\ImageDiff\\450178_copy.jpg";
		String resultFilePath = "F:\\google\\opencv-processing-master\\examples\\ImageDiff\\tresult1.jpg";
		ImgCompareUtil.compare(beforeFilePath, afterFilePath, resultFilePath);
		// ImageCompare imageCompare=new ImageCompare();
		// imageCompare.CompareAndMarkDiff(beforeFilePath,afterFilePath);
	}

	@Test
	public void tsetSolr() throws Exception {
		// 1.创建连接，路径最后一定不要忘记加上我们的new_core，它会默认访问index.html
		HttpSolrClient httpSolrClient = new HttpSolrClient.Builder("http://localhost:8080/solr/new_core").build();
		// 2.创建查询语句
		SolrQuery query = new SolrQuery();
		// 3.设置查询条件
		query.setQuery("title:*货车司机*");
		// 4、执行查询
		QueryResponse queryResponse = null;
		try {
			// 使用SolrServer对象的query()方法，传入一个SolrQuery类型的参数，执行语句，返回QueryResponse对象
			queryResponse = httpSolrClient.query(query);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		// 5.获取文档列表 使用QueryResponse对象的getResults()方法获取结果集
		SolrDocumentList documentList = queryResponse.getResults();
		// 6、遍历集合，打印查询到的数据
		for (SolrDocument solrDocument : documentList) {
			// 取各个文档信息
			System.out.print(solrDocument.get("title"));
			System.out.println();
		}
	}

	@Test
	public void testInitPlace() {
		Map<String, String> placeMap = new HashMap<>();
		// System.out.println(placeMap);
		Segment segment = HanLP.newSegment();
		List<Term> seg = segment.seg("sdfjsdkfl1000234890234.032498");
		// System.out.println(seg);
		System.out.println("ksldjfklsdf234324.23490".replaceAll("\\d+|[.]\\d+$", ""));
		System.out.println("士大夫撒地方北京市".replaceAll("[北京|市,上海,天津,重庆]+$", ""));
		String regex = "[北京,上海,天津,重庆]+|[市]+$";
		Matcher m = Pattern.compile(regex).matcher("士大夫撒地方北京市");
		while (m.find()) {
			System.out.println(m.group());
		}

	}

	@Test
	public void testNewInitPlace() {
		LexicalAnalyzer lexicalAnalyzer = FoolNLTK.getLSTMLexicalAnalyzer();
		String text = "2015年7月17日，郧西县食品药品监督管理局执法人员，在郧西县店子镇大坝塘村4组进行食品安全日常巡查时，\n" + "发现当事人经营场所内摆放于待售的标签标识不符合法律规定的食品“嘟嘟脆”，\n" + "由高唐县华美食品有限公司，规格：散装称重，生产日期：无，保质期8个月，共购进5斤，\n"
				+ "销售4.8斤，库存0.2斤，单价7.00元/斤，当事人经销的标签标识不符合法律规定的食品货值金额为1.4元。\n" + "报经本局领导批准，执法人员当场依法对当事人经营的标签标识不符合法律规定的食品实施了扣押的强制措施。\n" + "   相关证据：\n" + "   1、当事人提供的身份证复印件一份，证明了当事人的身份信息；\n"
				+ "   2、当事人提供的《个体工商户营业执照》、《食品流通许可证》复印件各一份，证明了当事人食品经营许可证登记信息；\n" + "   3、执法人员现场制作的《现场检查笔录》一份，证明了当事人经营标签标识不符合法律规定食品的事实；\n" + "   4、执法人员制作的《询问调查笔录》一份，证明了当事人经营的标签标识不符合法律规定的数量、价格情况;\n"
				+ "5、执法人员现场制作的《查封（扣押）决定书》及《物品清单》一份，证明了执法人员现场依法对当事人经营的标签标识不符合法律规定食品实施了扣押的强制措施；\n" + "6、执法人员现场拍摄的照片一张，证明了当事人标签标识不符合法律规定食品的事实。\n" + "7.当事人提供了进货单一张，证明了当事人经营标签标识不符合法律规定的食品进货情况。\n"
				+ "以上证据和笔录分别由当事人签名认可。";
		;
		List<List<Entity>> entitys = lexicalAnalyzer.ner(text);
		System.out.println(entitys);
	}

	public final static boolean isNumeric(String s) {
		if (s != null && !"".equals(s.trim()))
			return s.matches("^[0-9]*$");
		else
			return false;
	}

	@Test
	public void readCSV() {
		File csv = new File("G:\\hubei0.csv"); // CSV文件路径
		BufferedReader br = null;
		try {
			// br = new BufferedReader(new FileReader(csv));
			DataInputStream in = new DataInputStream(new FileInputStream(csv));
			br = new BufferedReader(new InputStreamReader(in, "GB2312"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String line = "";
		String everyLine = "";
		try {
			List<String> allString = new ArrayList<>();
			while ((line = br.readLine()) != null) // 读取到的内容给line变量
			{
				try {
					everyLine = line;
					String[] strings = everyLine.split(",");
					// 地名
					String address = strings[1];
					// 经度
					String longitude = strings[2];
					// 纬度
					String latitude = strings[3];
					System.out.println("地名：" + address + "，经度：" + longitude + "，纬度：" + latitude);
					allString.add(everyLine);
					address = StringUtil.removeUnableSymbol(address);
					String[] filterWord = { "号", "株", "区", "厅", "局", "站", "市区", "站", "大队", "支队", "省", "市", "县", "市政府", "区", "本院", "法院", "政府", "巡视组", "派出所", "检察院", "国", "检测中心", "鲟", "豚" };
					if (StringUtil.isNotEmpty(address)) {
						boolean flag = false;
						for (String s : filterWord) {
							if (address.endsWith(s) && !address.endsWith("超市")) {
								flag = true;
								break;
							}
						}
						if (flag) {
							continue;
						}
						this.writeContentToTxt(address.replaceAll("\\?", "") + " ns 1", "G:\\address.txt");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("=====================================================");
			}
			System.out.println("csv表格中所有行数：" + allString.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCustomDic() {
		String text = "大冶市大箕铺镇水南";
		AhoCorasickDoubleArrayTrieSegment pwlpSegment;
		pwlpSegment = new AhoCorasickDoubleArrayTrieSegment().loadDictionary("G:\\address.txt");
		// 相会副食店
		List<Term> seg = pwlpSegment.seg(text);
		for (Term term : seg) {
			System.out.println(term.nature.equals("fw"));
			System.out.println("fw".equals(term.nature.toString()));
		}
		if (seg.size() > 1) {
			String lastTermWord = seg.get(seg.size() - 2).word;
			int lastIndexOf = text.lastIndexOf(lastTermWord);
			int length = lastTermWord.length();
			String secondQuery = text.substring(0, lastIndexOf + length);
			System.out.println(secondQuery);
		}
		System.out.println(seg);
	}

	@Test
	public void testFileCode() {
		String path = "D:\\attachment\\pwlp\\hubei\\hanlp\\data\\dictionary\\addressFilterWord2.txt";
		FileUtil.writeContentToTxt("鄂州市实验小学 fw 1", path);
	}

	public static void filewrite(String str, String ResultfilePath) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(ResultfilePath, true);
			writer.write(str + System.getProperty("line.separator"));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddress() throws IOException {
		 AhoCorasickDoubleArrayTrieSegment addressSegment = new AhoCorasickDoubleArrayTrieSegment().loadDictionary("G:\\address2.txt");
		 List<Term> seg = HanLP.newSegment().enablePlaceRecognize(true).seg("环城街道办事处");
         System.out.println("梅南山社区居委会".substring(0,"梅南山社区居委会".indexOf("居委会")));
	}
	@Test
	public void readAreaCode(){
        File csv = new File("F:\\google\\china_area-master\\area_code_2018.csv"); // CSV文件路径
        BufferedReader br = null;
        try {
            // br = new BufferedReader(new FileReader(csv));
            DataInputStream in = new DataInputStream(new FileInputStream(csv));
            br = new BufferedReader(new InputStreamReader(in, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        try {
            List<String> allString = new ArrayList<>();
            while ((line = br.readLine()) != null) // 读取到的内容给line变量
            {
                try {
                    everyLine = line;
                    String[] strings = everyLine.split(",");
                    // 地名
                    String districtCode = strings[0];
                    // 经度
                    String districtName = strings[1];//new String(strings[1].getBytes("iso-8859-1"),"utf8");
                    // 纬度
                    String districtLevle = strings[2];
                    String districtParentCode = strings[3];
                    System.out.println(districtCode+":"+districtName+":"+districtLevle+":"+districtParentCode+":");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("=====================================================");
            }
            System.out.println("csv表格中所有行数：" + allString.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
