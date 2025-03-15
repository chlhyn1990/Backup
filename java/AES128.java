package com.erp.common;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
 
public class AES128 {
		
	//IV
	private String ips;
	//키 클래스
    private Key keySpec;
	
    //생성자 test1
	public AES128() {
		//암호화키를 암/복호화하는 암호화키 16byte
		String key = "test1234test1234";
        try { 
        	//암호화키 저장할 byte[] 변수
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            System.arraycopy(b, 0, keyBytes, 0, keyBytes.length);
            //비밀키 클래스 선언
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            //운영중인 오라클 패키지 함수에서 IV를 사용하지 않기 때문에  null 16bytes
            this.ips = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
            //AES방식과 암호화 키 값을  Key 클래스에 저장
            this.keySpec = keySpec;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	//생성자 오버로딩
	public AES128(String key) {
        try {
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            System.arraycopy(b, 0, keyBytes, 0, keyBytes.length);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            this.ips = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0"; //null 16bytes
            this.keySpec = keySpec;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
	//암호화
    public String encrypt(String plainText) {
    	//암/복호화 암호 기능 제공 클래스 선언
        Cipher cipher;
        try {
        	//암호방식, 체인방식, 패딩방식 선언 및 인스턴스 득
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //인스턴스 얻은 후 암호화키와 IV로 암호 초기화
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ips.getBytes()) );
            //암호화 실행1
            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
            //오라클 DBMS_CRYPTO 함수에서 HEX String을 반환하기 떄문에  byte[]->HEX 변환
            String encryptStr = new String(byteArrayToHex(encrypted).toUpperCase());

            //암호화된 문자열 반환
            return encryptStr;
        } catch (Exception e) {
        	return null;
        }
    }
 
    //복호화
    public String decrypt(String encryptStr) {
        try {
        	//암호방식, 체인방식, 패딩방식 선언 및 인스턴스 득
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //인스턴스 얻은 후 암호화키와 IV로 암호 초기화
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ips.getBytes("UTF-8")));
            //HEX String->byte[] 로 변환
            byte[] byteStr = hexToByteArray(encryptStr);
            //복호화 실행
            String decryptStr = new String(cipher.doFinal(byteStr), "UTF-8");

            return decryptStr;
        } catch (Exception e) {
            return null;
        }
    }
    
    //byte[]->hex String
    private String byteArrayToHex(byte[] encrypted) {
        
        if(encrypted == null || encrypted.length == 0){
            return null;
        }
        
        StringBuffer sb = new StringBuffer();
        for (byte b : encrypted) {
        	//앞 빈자리 0으로 채우는 16진수 (16진수는 2자리)
        	sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    //hex String->byte[]
    private byte[] hexToByteArray(String hex) {
        
        if(hex == null || hex.length() == 0){
            return null;
        }
        
        //16진수는 2bytes
        byte[] byteArray = new byte[hex.length() / 2 ];
         
        for(int i = 0; i<byteArray.length; i++){
        	//2자리씩 16진수로 변환
            byteArray[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        
        return byteArray;
    }
}
