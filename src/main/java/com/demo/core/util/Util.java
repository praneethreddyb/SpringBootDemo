package com.demo.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

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

	
}
