package io.basquiat.utils;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.basquiat.common.code.CommonCode;

/**
 * Common Utils
 * 
 * created by basquiat
 *
 */
public class CommonUtils {

	/**
	 * Object convert to json String
	 * 
	 * @param object
	 * @return String
	 * @throws JsonProcessingException
	 */
	public static String convertJsonStringFromObject(Object object) {
		String result = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return result;
	}
	
	/**
	 * json String convert Object
	 * 
	 * @param content
	 * @param clazz
	 * @return T
	 * @throws Exception
	 */
	public static <T> T convertObjectFromJsonString(String content, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		T object = null;
		try {
			object = (T) mapper.readValue(content, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return object;
	}
	
	/**
	 * Generic Collection Type covert method
	 * @param content
	 * @param clazz
	 * @return T
	 * @throws Exception
	 */
	public static <T> T convertObjectFromJsonStringByTypeRef(String content, TypeReference<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		T object = null;
		try {
			object = mapper.readValue(content, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return object;
	}

	/**
	 * convert string to JsonObject using Gson
	 * @param content
	 * @return JsonObject
	 */
	public static JsonObject convertObjectFromStringUsingGson(String content) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(content).getAsJsonObject();
		return jsonObject;
	}
	
	/**
	 * 수수료는 고정 0.000375 <--- 테스트넷은 이렇게 고정인데 메인넷은 확인해 봐야함.
	 * @param balance
	 * @return String
	 */
	public static String calculateAmount(String balance) {
		// 일단 바이낸스 블록체인 내부적으로는 BigDecimal의 스케일 8까 사용하고 있다.
		// 스트링으로 넘어오는 발란스를 BigDecimal로 변환해서 계산하고 다시 스트링으로 변환해서 반환한다.
		BigDecimal totalBalance = new BigDecimal(balance);
		BigDecimal fee = new BigDecimal(CommonCode.TRANSACTIONFEE.value);
		return totalBalance.subtract(fee).toString();
		
	}
	
}
