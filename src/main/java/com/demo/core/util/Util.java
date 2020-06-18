package com.demo.core.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
	
	public static Connection getConnection(ZcMap connDetails) throws ClassNotFoundException, SQLException {
		return getConnection(connDetails.getS("host"), connDetails.getI("port"), connDetails.getS("schema"), 
				connDetails.getS("user"), connDetails.getS("password"));
	}
	
	public static String getSqlResourceFileAsString(String path) throws URISyntaxException, IOException {
		if(isBlank(path)) return null;
		return FileUtils.readFileToString(new File(ClassLoader.getSystemClassLoader()
						.getResource(path).getFile()), StandardCharsets.UTF_8);
	}
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		System.out.println(getSqlResourceFileAsString("script/dbscript.sql"));
	}
	
	public static Connection getConnection(String host,int port,String schema,String user,String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");  
		String url="jdbc:mysql://${host}:${port}/${db_name}?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&useLegacyDatetimeCode=false&allowPublicKeyRetrieval=true&serverTimezone="+Calendar.getInstance().getTimeZone().getID();
		url=url.replace("${host}",  host);
		url=url.replace("${db_name}", schema);
		url=url.replace("${port}",  port+"");
		Connection conn=DriverManager.getConnection(url,user,password);  
		return conn;
	}
	
	public static void closeConnection(Connection conn) {
		if(conn==null) return;
		try {
			if(!conn.getAutoCommit()) conn.commit();
		}catch (Exception e) {
//			e.printStackTrace();
		}
		try {
			if(!conn.isClosed()) {
				conn.close();
			}
		}catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public static Date addMinutesToDate(int minutes, Date beforeTime) {
		final long ONE_MINUTE_IN_MILLIS = 60000;// millisecs
		long curTimeInMs = beforeTime.getTime();
		Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
		return afterAddingMins;
	}

	public static String addMinutesToDate(int minutes) {
		Date date = addMinutesToDate(minutes, new Date());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = dateFormat.format(date);
		return strDate;
	}

	public static String generateSecretCode() {
		return UUID.randomUUID().toString() + "-" + Calendar.getInstance().getTimeInMillis();
	}

	public Integer generateRandomNumber(boolean mobileAccess) {
		return mobileAccess ? ((int) Math.random() * 999999 + 100000) : null;
	}

	public static int yearDiff(String fmVal, Date toDate) {
		if (isBlank(fmVal))
			return 0;
		Date fromDate = null;
		try {
			fromDate = getDateFromUtcString(fmVal.toString());
		} catch (Exception e) {
			return 0;
		}
		return getDiffYears(fromDate, toDate);
	}

	public static long getMinDiff(Date first, Date last) {
		long diff = last.getTime() - first.getTime();
		diff = diff / (1000 * 60);
		return diff < 0 ? diff * -1 : diff;
	}

	public static int getDiffDays(Date first, Date last) {
		long diff = last.getTime() - first.getTime();
		return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static int getDiffYears(Date first, Date last) {
		Calendar a = getCalendar(first);
		Calendar b = getCalendar(last);
		int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
		if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
				|| (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
			diff--;
		}
		return diff;
	}

	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static Date getCurrentDate() {
		return new Date();
	}
	
	public static String toJson(Object obj) {
		return toJson(obj, true);
	}

	public static String toYml(Object obj) {
		if (obj == null || isBlank(obj))
			return "";
		if (obj instanceof String) {
			return (new Yaml()).dumpAsMap(jsonToZcMap((String) obj));
		} else
			return (new Yaml()).dumpAsMap(jsonToZcMap(toJson(obj)));
	}

	public static String truncate(String s, int len) {
		return s == null ? null : (s.length() <= len ? s : s.substring(0, len));
	}
	
	/**
	 * Get question place holders by size
	 * 
	 * @param size
	 * @return String of questions marks separated by commas
	 * @author Jagadeesh.T
	 * @since 2018-07-20
	 */
	public static String getQuestionMarksByNos(int size) {
		String res="";
		for(int i=0;i<size;i++) {
			res+= (res.equals("")?"?":",?");
		}
		return res;
	}
	public static void buildProcessValidationMsg(ZcMap res,String msg) {
		if(msg!=null && msg.startsWith("ZcProcException::")) msg=msg.replace("ZcProcException::", "");
		else if(msg==null)	msg="Error occured";
		ZcMap  data=new ZcMap();
		data.put("msg", msg);//rule.getS("errorMsg." + p.accessKit.lang));
		res.getZcMapList("errors").add(data);
	}
	public static void buildProcessValidationMsg(ZcMap rule,ZcMap res,String lang) {
		ZcMap  data=new ZcMap();
		data.put("msg", rule.getS("errorMsg." + lang));
		res.getZcMapList("errors").add(data);
	}
	
	/**
	 * This method will do setup JqGrid data, defines total pages, page no., records
	 * per page, records and total no. of records
	 * 
	 * @author Mahesh.J
	 * @since 2018-02-14
	 * @param reqData
	 * @param map
	 * @param page
	 * @return JqGridData
	 */
	@SuppressWarnings({ "serial" })
	public static ZcMap getJqGridData(ZcMap reqData, ZcMap map, int page) {
		try {
			int size = parseInt(reqData.get("rows") + "");
			int totalNumberOfRecords = (Integer) map.get("totalCount");
			double totalPages = 1;
			if (size > 0)
				totalPages = (((double) totalNumberOfRecords) / ((double) size));
			else {
				totalPages = 1;
				size = totalNumberOfRecords;
			}
			totalPages = (totalPages == ((int) totalPages)) ? totalPages : (((int) totalPages) + 1);
			JqGridData jqGridData = new JqGridData(((int) totalPages), page - 1, size, map, totalNumberOfRecords);
			return new ZcMap() {
				{
					put("listData", jqGridData);
				}
			};
		} catch (NullPointerException e) {
			return new ZcMap() {
				{
					put("listData", null);
				}
			};
		}
	}
	
	
	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";

		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}
	
	
	public static boolean isValidPassword(String password) {
		String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#?!@$%^&*-])[A-Za-z\\d#?!@$%^&*-]{8,20}$";
		Pattern pat = Pattern.compile(passwordRegex);
		if (password == null)
			return false;
		return pat.matcher(password).matches();
	}
	


 

	/**
	 * 
	 * 
	 * @param srcImagePath
	 * @param dstImagePath
	 * @param paramHeight
	 * @param paramWidth
	 * @param extension
	 * @throws Exception
	 */
	public static void resizeImage(String srcImagePath, String dstImagePath, int paramHeight, int paramWidth,
			String extension) throws Exception {
		if (isBlank(srcImagePath) || isBlank(dstImagePath)) {
			return;
		}
		File srcFile = new File(srcImagePath);
		if (!srcFile.exists()) {
			return;
		}
		File file = new File(dstImagePath);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}

		Image img = null;
		BufferedImage tempBufFile = null;
		img = ImageIO.read(srcFile);
		tempBufFile = resizeImage(img, paramHeight, paramWidth);
		ImageIO.write(tempBufFile, extension, file);
	}

	/**
	 * This function resize the image file and returns the BufferedImage object that
	 * can be saved to file system.
	 * 
	 * @author Mahesh J
	 * @param image
	 * @param width
	 * @param height
	 * @return BufferedImage
	 */
	public static BufferedImage resizeImage(final Image image, int width, int height) {
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setComposite(AlphaComposite.Src);
		// below three lines are for RenderingHints for better image quality at cost of
		// higher processing time
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		graphics2D.dispose();
		return bufferedImage;
	}

	/**
	 * List's all the children nodes of the given map from the given list of ZcMap's
	 * and Arranges them in an order and inserts Children into the given map
	 * 
	 * @param map
	 * @param actList
	 * @author Jagadeesh.T
	 * @since 2018-07-20
	 */
	public static void arrangeTree(ZcMap map, List<ZcMap> actList, String uidKey, String parentKey) {
		List<ZcMap> childs = actList.stream()
				.filter(x -> map.hasData(uidKey) && map.getS(uidKey).equals(x.getS(parentKey)))
				.collect(Collectors.toList());
		childs.forEach(x -> {
			arrangeTree(x, actList, uidKey, parentKey);
		});
		map.put("childs", childs);
	}
	
	public static void arrangeTreeForAdmin(ZcMap map, List<ZcMap> actList, String uidKey, String parentKey) {
		List<ZcMap> childs = new ArrayList<ZcMap>();
		for (ZcMap data : actList) {
			if(map.equals(data))
				continue;
			if(map.getS("uid").equals(data.getS(parentKey))) 
				childs.add(data);
		}
		actList.removeAll(childs);
		childs.forEach(x -> {
			arrangeTreeForAdmin(x, actList, uidKey, parentKey);
		});
		map.put("childs", childs);
	}
	
	
	
	
	
	public static ZcMap jsonToZcMap(String s) {
		try {
			return new ObjectMapper().readValue(s == null ? null : s, ZcMap.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean isBlank(Object o) {
		if (o == null)
			return true;
		else if (o instanceof String) {
			if (((String) o).trim().equals(""))
				return true;
		} else if (o instanceof Collection<?>) {
			if (((Collection<?>) o).isEmpty())
				return true;
		} else if (o instanceof Integer) {
			if (((Integer) o) <= 0)
				return true;
		} else if (o instanceof Long) {
			if (((Long) o) <= 0)
				return true;
		} else if (o instanceof Short) {
			if (((Short) o) <= 0)
				return true;
		} else if (o instanceof Byte) {
			if (((Byte) o) <= 0)
				return true;
		} else if (o instanceof Double) {
			if (((Double) o) <= 0)
				return true;
		} else if (o instanceof Float) {
			if (((Float) o) <= 0)
				return true;
		} else if (o instanceof Map<?, ?>) {
			if (((Map<?, ?>) o).isEmpty())
				return true;
		} else if (o.getClass().isArray()) {
			return Array.getLength(o) == 0;
		} else {
			if (o.toString().trim().equals(""))
				return true;
		}
		return false;
	}
	
	public static String toJson(Object obj, boolean compress) {
		try {
			ObjectMapper om = new ObjectMapper();
			om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			if (compress) {
				return om.writer().writeValueAsString(obj);
			} else {
				return om.writer().withDefaultPrettyPrinter().writeValueAsString(obj);
			}
		} catch (JsonProcessingException e) {
			//log.error(e);
			return null;
		}
	}
	
	public static String getStackTrace(Exception e) {
		if(e==null) return "";
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
	
	public static String toTitleCase(String input) {
		if (input == null)
			return null;
		if (input.trim().equals(""))
			return "";
		input = input.replace("_", " ").toLowerCase();
		String result = "";
		char firstChar = input.charAt(0);
		result = result + Character.toUpperCase(firstChar);
		for (int i = 1; i < input.length(); i++) {
			char currentChar = input.charAt(i);
			char previousChar = input.charAt(i - 1);
			if (previousChar == ' ') {
				result = result + Character.toUpperCase(currentChar);
			} else {
				result = result + currentChar;
			}
		}
		return result;
	}

	/**
	 * Converts the given String to pascalCase
	 * 
	 * @param input
	 * @return String, Pascal case of the given input
	 * @author Jagadeesh.T
	 * @since 2018-07-20
	 */
	public static String pascalCase(String input) {
		if (input == null)
			return null;
		if (input.trim().equals(""))
			return "";
		input = toTitleCase(input);
		input = input.replaceAll("\\s+", "");
		char c = (input.charAt(0) + "").toLowerCase().charAt(0);
		return c + input.substring(1);
	}

	public static String toSentenceCase(String input) {
		if (input == null)
			return "";
		input = input.trim();
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;
		boolean isDot = false;
		for (char c : input.toCharArray()) {
			if (Character.isSpaceChar(c)) {
				nextTitleCase = isDot;
			} else if (c == '.') {
				nextTitleCase = isDot = true;
			} else if (nextTitleCase) {
				c = Character.toUpperCase(c);
				nextTitleCase = isDot = false;
			} else {
				c = Character.toLowerCase(c);
				isDot = false;
			}
			titleCase.append(c);
		}
		return titleCase.toString();
	}
	public static String formatData(String dateString,String format) {
		return new SimpleDateFormat(format).format(getDateFromUtcString(dateString));
	}
	public static String formatData(Date date,String format) {
		return new SimpleDateFormat(format).format(date);
	}
	public static String getDateTimeString(java.sql.Timestamp timeStamp) {
		if (timeStamp == null)	return null;
		SimpleDateFormat converter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//converter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return converter.format(timeStamp); 
	}
	public static String getDateString(java.sql.Date date) {
		if (date == null)	return null;
		SimpleDateFormat converter=new SimpleDateFormat("yyyy-MM-dd");
		//converter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return converter.format(date); 
	}
	public static String getTimeString(java.sql.Time time) {
		if (time == null)	return null;
		SimpleDateFormat converter=new SimpleDateFormat("HH:mm:ss");
		//converter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return converter.format(time); 
	}
	public static Date getDateTime(String date) {
		if (date == null)	return null;
		try {
			return simpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	public static boolean contains(String element,String[] elements) {
		return Arrays.asList(elements).contains(element);
	}
	
	public static Date getDateFromUtcString(String date) {
		if (date == null)
			return null;
		return Date.from(Instant.parse(date));
	}

	public static String getUtcStringFromDate(Date date) {
		if (date == null)
			return null;
		return date.toInstant().toString();
	}
	
	private static SimpleDateFormat simpleDateFormat(String format) {
		SimpleDateFormat sf=new SimpleDateFormat(format);
		sf.setLenient(false);
		return sf;
	}
	
	public static Date getDate(String date) {
		if (date == null)	return null;
		try {
			return simpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	public static Date getTime(String date) {
		if (date == null)	return null;
		try {
			return simpleDateFormat("HH:mm:ss").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Byte parseByte(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0;
		} else {
			try {
				return Byte.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	public static Short parseShort(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0;
		} else {
			try {
				return Short.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	public static Integer parseInt(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0;
		} else {
			try {
				return (int)Double.parseDouble(o.toString().trim());
			} catch (NumberFormatException e) {
				//log.info(ZcUtil.getStackTrace(e));
				return 0;
			}
		}
	}

	public static Long parseLong(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0l;
		} else {
			try {
				return Long.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0l;
			}
		}
	}

	public static Float parseFloat(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0f;
		} else {
			try {
				return Float.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0f;
			}
		}
	}

	public static Double parseDouble(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0d;
		} else {
			try {
				return Double.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0d;
			}
		}
	}

	public static Boolean parseBoolean(Object o) {
		if (o == null)
			return false;
		if (isBlank(o)) {
			return false;
		} else {
			try {
				return Boolean.valueOf(o.toString().trim()) || "1".equals(o.toString().trim());
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}

	public static ZcMap makeBody(Set<String> wildCards, ZcMap wildCardsData, String subject, String body, int i){
		ZcMap retData = new ZcMap();
		for (String eachWildCard : wildCards) {
			String[] wildCard = eachWildCard.split("\\(");
			String [] checkIfList = eachWildCard.split("\\[");
			Object _v = wildCardsData.get(wildCard[0].replace("[ind]", (wildCardsData.get(checkIfList[0]) instanceof List) ? "["+i+"]" : "").trim());
			_v = _v == null ? "" : _v;
			if (wildCard.length > 1 && hasData(wildCard[1])) {
				String type = wildCard[1].replace(")", "");
				if(type.equalsIgnoreCase("UPPER"))  _v =_v.toString().toUpperCase();
				else if (type.equalsIgnoreCase("LOWER")) _v =_v.toString().toLowerCase();
				else if (type.equalsIgnoreCase("TITLE"))  _v = convertToTitleCase(_v.toString());
				else if (type.equalsIgnoreCase("TOGGLE"))  _v = convertToToggleCase(_v.toString());
				else if (type.equalsIgnoreCase("CAMEL"))  _v = convertToCamelCase(_v.toString());
			}
			if(hasData(subject))subject = subject.replace("{{" + eachWildCard + "}}", hasData(_v) ? _v.toString(): "");
			if(hasData(body))body = body.replace("{{" + eachWildCard + "}}", hasData(_v) ? _v.toString(): "");
		}
		retData.put("body", body);
		retData.put("subject", subject);
		return retData;
	}
	
	public static boolean hasData(Object o) {
		return !isBlank(o);
	}
	
	
	public static String convertToTitleCase(String inputString) {
		String result = "";
	       if (isBlank(inputString) || inputString.trim().length() == 0)  return result;
	       result = result + Character.toUpperCase(inputString.charAt(0));
	       boolean terminalCharacterEncountered = false;
	       char[] terminalCharacters = {'.', '?', '!'};
	       for (int i = 1; i < inputString.length(); i++) {
	           char currentChar = inputString.charAt(i);
	           if (terminalCharacterEncountered) {
	               if (currentChar == ' ') {
	                   result = result + currentChar;
	               } else {
	                   result = result + Character.toUpperCase(currentChar);
	                   terminalCharacterEncountered = false;
	               }
	           } else 
	               result = result + Character.toLowerCase(currentChar);
	           for (int j = 0; j < terminalCharacters.length; j++) {
	               if (currentChar == terminalCharacters[j]) {
	                   terminalCharacterEncountered = true;
	                   break;
	               }
	           }
	       }
	       return result;
	}
	
	public static String convertToToggleCase(String inputString) {
		if (isBlank(inputString) || inputString.trim().length() == 0) return "";
		if (inputString.trim().length() == 1) return inputString.toUpperCase();
		String result = "";
		inputString = inputString.trim();
		for (char c : inputString.toCharArray()) {
			if (Character.isUpperCase(c))  result = result + Character.toLowerCase(c);
			else if (Character.isLowerCase(c)) result = result + Character.toUpperCase(c);
			else  result = result + c;
		}
		return result;
	}
	
	 public static String convertToCamelCase(String inputString) {
	       String result = "";
	       if (isBlank(inputString) || inputString.trim().length() == 0)  return result;
	       result = result + Character.toUpperCase(inputString.charAt(0));
	       for (int i = 1; i < inputString.length(); i++) {
	           char currentChar = inputString.charAt(i);
	           result = result + ((inputString.charAt(i - 1) == ' ') ?  Character.toUpperCase(currentChar) : Character.toLowerCase(currentChar));
	       }
	       return result;
	   }
}
