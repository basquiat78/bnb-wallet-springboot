package io.basquiat.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.basquiat.bnb.domain.LastBlockHeight;
import lombok.extern.slf4j.Slf4j;

/**
 * nio file을 이용한 파일 입출력 유틸
 * created by basquiat
 *
 */
@Slf4j
@Component
public class FileIOUtils {

	static String FILE_PATH;
	
	@Value("${file.path}")
	private void setWalletPath(String filePath) {
		FILE_PATH = filePath;
    }
	
	/**
	 * write lastBlockHeight Json file
	 * @param lastBlockHeight
	 */
	public static void writeFile(LastBlockHeight lastBlockHeight) {
		Path path = Paths.get(FILE_PATH);
		try(FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
			ByteBuffer byteBuffer = Charset.defaultCharset().encode(CommonUtils.convertJsonStringFromObject(lastBlockHeight));
			channel.write(byteBuffer);
		} catch(IOException e) {
			log.error("NIO File Write Error : " + e.getMessage());
		}
	}
	
	/**
	 * read json info.txt file and convert to object
	 * @return LastBlockHeight
	 */
	public static LastBlockHeight readFile() {
		Path path = Paths.get(FILE_PATH);
		LastBlockHeight lastBlockHeight = null;
		try(FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) Files.size(path));
			channel.read(byteBuffer);
			byteBuffer.flip();
			
			String fileConents = Charset.defaultCharset().decode(byteBuffer).toString();
			lastBlockHeight = CommonUtils.convertObjectFromJsonString(fileConents, LastBlockHeight.class);
		} catch(IOException e) {
			log.error("NIO File Read Error : " + e.getMessage());
		}
		return lastBlockHeight;
	}

}
