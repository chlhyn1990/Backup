import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class MySftp {

	private String host;
	private Integer port;
	private String user;
	private String password;

	private JSch jsch;
	private Session session;
	private Channel channel;
	private ChannelSftp sftpChannel;

	public MySftp(String host, Integer port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public int connect() {

		System.out.println("connecting..." + host);
		try {
			jsch = new JSch();
			session = jsch.getSession(user, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();
			sftpChannel = (ChannelSftp) channel;
			System.out.println("connected" + host);
            return 1;
		} catch (JSchException e) {
        	System.out.println("fail to connect");
			e.printStackTrace();
            return 0;
		}

	}

	public void disconnect() {
		System.out.println("disconnecting...");
		sftpChannel.disconnect();
		channel.disconnect();
		session.disconnect();
        System.out.println("disconnected");
	}

	public void upload(String fileName, String remoteDir) {
    
        FileInputStream fis = null;
        
		try {
			// Change to output directory
			sftpChannel.cd(remoteDir);

			// Upload file
			File file = new File(fileName);
			fis = new FileInputStream(file);
			sftpChannel.put(fis, file.getName());

			fis.close();
			System.out.println("File uploaded successfully - "
					+ file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void download(String fileName, String localDir) {
        
        byte[] buffer = new byte[1024];
		BufferedInputStream bis;
        
		try {
			// Change to output directory
			String cdDir = fileName.substring(0, fileName.lastIndexOf("/") + 1);
			sftpChannel.cd(cdDir);

			File file = new File(fileName);
			bis = new BufferedInputStream(sftpChannel.get(file.getName()));

			File newFile = new File(localDir + "/" + file.getName());

			// Download file
			OutputStream os = new FileOutputStream(newFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bis.close();
			bos.close();
			System.out.println("File downloaded successfully - "
					+ file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void filecopy(String path, String yesterday) {
		try {
			Runtime runtime = Runtime.getRuntime();
			
			String s_exe = "cmd /c md \\\\192.168.0.76\\sftp\\" + yesterday;
			runtime.exec(s_exe);
			
			s_exe = "cmd /c move \\\\192.168.0.76\\sftp\\*." + yesterday.substring(2) 
                    + " \\\\192.168.0.76\\sftp\\" + yesterday;
			runtime.exec(s_exe);
			
			s_exe = "xcopy /Y " + path + " \\\\192.168.0.76\\sftp\\";
			runtime.exec(s_exe);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		Date date = new Date();
		Date yesterday = new Date();
		
		SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat now = new SimpleDateFormat("hh:mm:ss");
        String today_yymmdd = today.format(date).substring(2); 
		
		yesterday.setTime(date.getTime() - ((long) 1000 * 60 * 60 *24));
        String strYesterday	 = today.format(yesterday);
		
		String localPath = "C:\\sftp\\" + today.format(date);
		String remotePath = "/home/jboss/sftp/";

		File folder = new File(localPath);
		folder.mkdirs();

		MySftp sftp = new MySftp("192.168.110.10", 22, "ftpuser", "ftppw");
        
        if(!sftp.connect())
        	return;
        
        sftp.upload(localPath+"filetoupload.txt", remotePath);
		sftp.download("/home/jboss/natgerp." + today_yymmdd, localPath);
        sftp.disconnect();
		filecopy(localPath, strYesterday);
	}

}
