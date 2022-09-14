package com.dummy.code.general.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class DataTableUtil {
	public static JSONObject convertDTDataToJSON(String dtData) {
		JSONObject convertedObj = new JSONObject();
		JSONArray columnList = new JSONArray();
		JSONArray orderList = new JSONArray();
		JSONObject searchObject = new JSONObject();

		MultiValueMap<String, String> dtParam = UriComponentsBuilder.fromUriString("?" + dtData).build()
				.getQueryParams();
		dtParam.forEach((key, list) -> {
			try {
				String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8.toString());
				String decodedData = URLDecoder.decode(list.get(0), StandardCharsets.UTF_8.toString());

				if (decodedKey.startsWith("columns[")) {
					int index = Integer
							.parseInt(decodedKey.substring(decodedKey.indexOf("[") + 1, decodedKey.indexOf("]")));
					String varName = decodedKey.substring(decodedKey.lastIndexOf("[") + 1, decodedKey.lastIndexOf("]"));

					if (decodedKey.contains("[search]")) {
						varName = "search_" + varName;
					}

					if (columnList.length() >= index + 1) {
						columnList.put(index, columnList.getJSONObject(index).put(varName, decodedData));
					} else {
						columnList.put(index, new JSONObject().put(varName, decodedData));
					}
				} else if (decodedKey.startsWith("order[")) {
					int index = Integer
							.parseInt(decodedKey.substring(decodedKey.indexOf("[") + 1, decodedKey.indexOf("]")));
					String varName = decodedKey.substring(decodedKey.lastIndexOf("[") + 1, decodedKey.lastIndexOf("]"));

					if (orderList.length() >= index + 1) {
						orderList.put(index, orderList.getJSONObject(index).put(varName, decodedData));
					} else {
						orderList.put(index, new JSONObject().put(varName, decodedData));
					}
				} else if (decodedKey.startsWith("search[")) {
					searchObject.put(decodedKey.substring(decodedKey.indexOf("[") + 1, decodedKey.indexOf("]")),
							decodedData);
				} else {
					convertedObj.put(decodedKey, decodedData);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		convertedObj.put("columns", columnList);
		convertedObj.put("order", orderList);
		convertedObj.put("search", searchObject);

		return convertedObj;
	}

	public static int getSQLLikeCount(JSONArray columnData, String[] checkColumnArr, String[] columnArr) {
		int totalCount = 0;

		List<String> columnNameArrList = Arrays.asList(checkColumnArr);

		for (int x = 0; x < columnData.length(); x++) {
			JSONObject columnObj = columnData.getJSONObject(x);
			if (columnObj.getBoolean("searchable")) {
				String columnNameCheck = columnObj.getString("data");
				int colIndex = columnNameArrList.indexOf(columnNameCheck);
				if (colIndex >= 0) {
					totalCount++;
				}
			}
		}

		return totalCount;
	}

	public static String generateSQLLikeStr(JSONArray columnData, String[] checkColumnArr, String[] columnArr) {
		StringBuilder sb = new StringBuilder();

		List<String> columnNameArrList = Arrays.asList(checkColumnArr);

		for (int x = 0; x < columnData.length(); x++) {
			JSONObject columnObj = columnData.getJSONObject(x);
			if (columnObj.getBoolean("searchable")) {
				String columnNameCheck = columnObj.getString("data");
				int colIndex = columnNameArrList.indexOf(columnNameCheck);
				if (colIndex >= 0) {
					if (sb.length() > 0) {
						sb.append(" OR ");
					} else {
						sb.append("(");
					}
					sb.append("LOWER(" + columnArr[colIndex] + "::VARCHAR) LIKE LOWER(?)");
				}
			}
		}

		if (sb.length() > 0) {
			sb.append(")");
		}

		return sb.toString();
	}

	public static String generateSQLOrderStr(JSONArray columnData, String[] checkColumnArr, String[] columnArr,
			JSONArray orderData) {
		StringBuilder sb = new StringBuilder();

		List<String> columnNameArrList = Arrays.asList(checkColumnArr);

		for (int x = 0; x < orderData.length(); x++) {
			JSONObject currentOrderObj = orderData.getJSONObject(x);

			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(columnArr[columnNameArrList.indexOf(
					columnData.getJSONObject(Integer.parseInt(currentOrderObj.getString("column"))).getString("data"))]
					+ " " + currentOrderObj.getString("dir"));
		}

		return sb.toString();
	}

	public static String generateSQLExtFilterStr(JSONArray columnData, String[] checkColumnArr, String[] columnArr) {
		StringBuilder sb = new StringBuilder();

		List<String> columnNameArrList = Arrays.asList(checkColumnArr);

		for (int x = 0; x < columnData.length(); x++) {
			JSONObject columnObj = columnData.getJSONObject(x);
			if (columnObj.getBoolean("searchable")) {
				String filterValue = columnObj.getString("search_value");
				if (!filterValue.isEmpty()) {
					boolean isFilterRegex = columnObj.getBoolean("search_regex");

					String columnNameCheck = columnObj.getString("data");
					int colIndex = columnNameArrList.indexOf(columnNameCheck);
					if (colIndex >= 0) {
						if (sb.length() > 0) {
							sb.append(" AND ");
						} else {
							sb.append("(");
						}

						if (isFilterRegex) {
							sb.append(columnArr[colIndex] + "::VARCHAR ~ ?");
						} else {
							sb.append(columnArr[colIndex] + "::VARCHAR = ?");
						}
					}
				}
			}
		}

		if (sb.length() > 0) {
			sb.append(")");
		}

		return sb.toString();
	}

	public static int fillSQLExtFilter(JSONArray columnData, String[] checkColumnArr, String[] columnArr,
			int startPsIndex, PreparedStatement ps) throws Exception {
		List<String> columnNameArrList = Arrays.asList(checkColumnArr);

		for (int x = 0; x < columnData.length(); x++) {
			JSONObject columnObj = columnData.getJSONObject(x);
			if (columnObj.getBoolean("searchable")) {
				String filterValue = columnObj.getString("search_value");
				if (!filterValue.isEmpty()) {
					String columnNameCheck = columnObj.getString("data");
					int colIndex = columnNameArrList.indexOf(columnNameCheck);
					if (colIndex >= 0) {
						ps.setString(startPsIndex++, filterValue);
					}
				}
			}
		}

		return startPsIndex;
	}
}
