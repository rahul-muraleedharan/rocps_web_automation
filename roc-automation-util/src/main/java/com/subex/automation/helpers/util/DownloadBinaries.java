package com.subex.automation.helpers.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;

import com.subex.automation.helpers.data.ValidationHelper;
import com.subex.automation.helpers.report.Log4jHelper;
import com.subex.automation.helpers.selenium.AcceptanceTest;

public class DownloadBinaries extends AcceptanceTest{

	public DownloadBinaries() throws Exception {
		try {
			
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	public void download(String downloadURL, String destination) throws Exception {
		InputStream in = null;
		FileOutputStream fout = null;
		
		try{
			in = getConnection(downloadURL, destination);
			fout = new FileOutputStream(destination);
	
			byte buffer1[] = new byte[1024 * 170];
			int k = 0;
			long total = 0;
			long nextLogAt = 25L * 1024 * 1024;
			Log4jHelper.logInfo("Download URL: " + downloadURL + "\nDownloading  ..");
			while ((k = in.read(buffer1)) != -1) {
				fout.write(buffer1, 0, k);
				total += k;
				if (total >= nextLogAt) {
					Log4jHelper.logInfo("Downloaded " + (total / (1024 * 1024)) + " MB ..");
					nextLogAt += 25L * 1024 * 1024;
				}
			}
			Log4jHelper.logInfo("\nDownload Done!! (" + total + " bytes)");
		}
		catch(FileNotFoundException e) {
			FailureHelper.setError("Specified location '" + downloadURL + "' not found. May be build is in-progress or failed");
			FailureHelper.setErrorMessage(e);
			throw e;
		}
		catch(Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		} finally {
			if (fout != null)
				fout.close();
			if (in != null)
				in.close();
		}
	}
	
	public InputStream getConnection(String downloadURL, String destination) throws Exception {
		try {
			InputStream in = null;
			URL url=new URL(downloadURL);
			HttpURLConnection httpCon = getURLConnection(url, destination, null);
			int code = httpCon.getResponseCode();
			
			if (!(code == 200)) {
				httpCon.disconnect();
				
				String authStringEnc = getAuthentication();
				httpCon = getURLConnection(url, destination, authStringEnc);
				code = httpCon.getResponseCode();
			}
			
			if (code == 404) {
				FailureHelper.failTest("URL '" + downloadURL + "' does not exist. This may be due to build not available.");
			}
			else if (!(code == 200)) {
				FailureHelper.failTest("Connecting to url '" + downloadURL + "' failed due to error code '" + code + "'");
			}
			else
				in = httpCon.getInputStream();
			
			return in;
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private HttpURLConnection getURLConnection(URL url, String destination, String authStringEnc) throws Exception {
		try {
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

			if (ValidationHelper.isNotEmpty(authStringEnc))
				httpCon.setRequestProperty("Authorization", "Basic " + authStringEnc);
//			httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
			// GET (not POST): setDoOutput(true) forces POST, which the Jenkins
			// lastSuccessfulBuild permalink does not 302-follow and which stalls.
			httpCon.setRequestMethod("GET");
			httpCon.setInstanceFollowRedirects(true);
			httpCon.setDoInput(true);
			httpCon.setConnectTimeout(30000);
			httpCon.setReadTimeout(300000);
			if (ValidationHelper.isNotEmpty(destination))
				httpCon.setRequestProperty("file-name", destination);
			
			return httpCon;
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private String getAuthentication() throws Exception {
		try {
			String authStringEnc = null;
			String userName = configProp.getJenkinsUsername();
			String password = configProp.getJenkinsPassword();
			
			if (ValidationHelper.isNotEmpty(userName) || ValidationHelper.isNotEmpty(password)) {
				String authString = userName + ":" + password;
				byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
				authStringEnc = new String(authEncBytes);
			}
			
			return authStringEnc;
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
	
	private int headStatus(URL url, String authStringEnc) throws Exception {
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setRequestMethod("GET");
		httpCon.setInstanceFollowRedirects(true);
		httpCon.setConnectTimeout(30000);
		httpCon.setReadTimeout(30000);
		if (ValidationHelper.isNotEmpty(authStringEnc))
			httpCon.setRequestProperty("Authorization", "Basic " + authStringEnc);
		try {
			return httpCon.getResponseCode();
		}
		finally {
			httpCon.disconnect();
		}
	}

	public boolean isURLAvailable(String downloadURL) throws Exception {
		try {
			URL url = new URL(downloadURL);
			int code = headStatus(url, null);

			if (code != 200) {
				String authStringEnc = getAuthentication();
				if (ValidationHelper.isNotEmpty(authStringEnc))
					code = headStatus(url, authStringEnc);
			}

			return code == 200;
		} catch (Exception e) {
			FailureHelper.setErrorMessage(e);
			throw e;
		}
	}
}