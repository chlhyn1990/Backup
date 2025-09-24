package ncp_auth;


import java.util.Base64;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class NcpAuth {
	public static void main(String args[]) {
		
		Instant now = Instant.now();
		LocalDateTime koreaNow = LocalDateTime.ofInstant(now, ZoneId.of("Asia/Seoul"));
		String timestamp = String.valueOf(koreaNow.toInstant(ZoneOffset.UTC).toEpochMilli());
		
		String startTime = getStartOfDay(now);
		String endTime = getEndOfDay(now);
		
		String method = "GET";
		String accessKey = "{accessKey}";			
		String secretKey = "{secretKey}";
		String url = "/sms/v2/services/{serviceId}/unsubscribes?startTime="+startTime+"&endTime="+endTime;
		
		String signature = null;
		NcpAuth ncpAuth = new NcpAuth();
		
		try {
			
			signature = ncpAuth.makeSignature(timestamp, method, accessKey, secretKey, url);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(signature);
	}
	
	//signature 생성
	private String makeSignature(String timestamp, String method, String accessKey, String secretKey, String url) {
		String space = " ";					
		String newLine = "\n";
		String encodeBase64String = null;
		String message = new StringBuilder()
			.append(method)
			.append(space)
			.append(url)
			.append(newLine)
			.append(timestamp)
			.append(newLine)
			.append(accessKey)
			.toString();

		try {
			SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
			encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return encodeBase64String;
	}
	
	// 하루의 시작 (00:00:00)
    public static String getStartOfDay(Instant instant) {
        LocalDateTime startOfDay = instant.atZone(ZoneId.of("Asia/Seoul"))
                                         .toLocalDate()
                                         .atStartOfDay();
        return String.valueOf(startOfDay.toInstant(ZoneOffset.UTC).toEpochMilli());
    }
    
    // 하루의 끝 (23:59:59.999999999)
    public static String getEndOfDay(Instant instant) {
        LocalDateTime endOfDay = instant.atZone(ZoneId.of("Asia/Seoul"))
                                       .toLocalDate()
                                       .atTime(23, 59, 59, 999999999);
        return String.valueOf(endOfDay.toInstant(ZoneOffset.UTC).toEpochMilli());
    }
    
}
