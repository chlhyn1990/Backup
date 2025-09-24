package ncp_auth;


import java.util.Base64;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class NcpAuth {
	private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
	
	public static void main(String args[]) {
		
		Instant now = Instant.now();
		//System.out.println("now:"+now + "\n");
		String timestamp = String.valueOf(now.atZone(KOREA_ZONE).toInstant().toEpochMilli());
		String startTime = getStartOfDay(now);
		String endTime = getEndOfDay(now);
		
		String method = "GET";
		String accessKey = "";			
		String secretKey = "";
		String serviceId = "";
		String url = "/sms/v2/services/" + serviceId + "/unsubscribes?startTime="+startTime+"&endTime="+endTime;
		String fullUrl = "https://sens.apigw.ntruss.com" + url;
		
		String signature = null;
		NcpAuth ncpAuth = new NcpAuth();
		
		try {
			signature = ncpAuth.makeSignature(timestamp, method, accessKey, secretKey, url);
			
			
			URL sendUrl = new URL(fullUrl);
			HttpURLConnection conn = (HttpURLConnection)sendUrl.openConnection();
			conn.setRequestMethod(method);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
			conn.setRequestProperty("x-ncp-iam-access-key", accessKey);
			conn.setRequestProperty("x-ncp-apigw-signature-v2", signature);
			
			int responseCode = conn.getResponseCode();
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response);
                
            } else {
                System.out.println(responseCode + " GET request failed");
            }
			
			conn.disconnect();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
    public static String getStartOfDay(Instant now) {
    	Instant startOfDay = now.atZone(KOREA_ZONE)
					                .toLocalDate()
					                .atStartOfDay(KOREA_ZONE)
					                .toInstant();
    	//System.out.println("startOfDay:"+startOfDay.atZone(KOREA_ZONE).toLocalDateTime());
        return String.valueOf(startOfDay.toEpochMilli());
    }
    
    // 하루의 끝 (23:59:59.999999999)
    public static String getEndOfDay(Instant now) {
    	Instant endOfDay = now.atZone(KOREA_ZONE)
						                .toLocalDate()
						                .atTime(23, 59, 59, 999000000)
						                .atZone(KOREA_ZONE)
						                .toInstant();
    	//System.out.println("endOfDay:"+endOfDay.atZone(KOREA_ZONE).toLocalDateTime());
        return String.valueOf(endOfDay.toEpochMilli());
    }
    
}
