package io.basquiat.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 
 * BnbDateUtils
 * 
 * created by basquiat
 *
 */
public class BnbDateUtils {

	/**
	 * bnb timestamp를 Date객체로 변환한다.
	 * @param timeStamp
	 * @return Date
	 */
	public static Date convertDateFromBNBTimeStamp(String timeStamp) {
		Instant instant = Instant.parse(timeStamp);
		ZoneId zoneId = ZoneId.of("Asia/Seoul");
		ZonedDateTime zdt = ZonedDateTime.ofInstant( instant , zoneId );
		return Date.from(zdt.toInstant());
	}

}
