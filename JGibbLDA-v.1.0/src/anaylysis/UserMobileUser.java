package anaylysis;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserMobileUser {
	private String userMobileFileName = "";// 保存用户特征的文件名
	List<String> testVersion;// 保存每个用户的手机型号
	List<HashMap<Integer, Double>> FeatureMatrx = null;// 保存用户的上网特征的哈希表

	/**
	 * @return the userMobileFileName
	 */
	public String getUserMobileFileName() {
		return userMobileFileName;
	}

	/**
	 * @param userMobileFileName
	 *            the userMobileFileName to set
	 */
	private void setUserMobileFileName(String userMobileFileName) {
		this.userMobileFileName = userMobileFileName;
	}

	/**
	 * @return the testVersion
	 */
	public List<String> getTestVersion() {
		return testVersion;
	}

	/**
	 * @return the featureMatrx
	 */
	public List<HashMap<Integer, Double>> getFeatureMatrx() {
		return FeatureMatrx;
	}

	public UserMobileUser(String userMobileFileName) {
		this.setUserMobileFileName(userMobileFileName);
	}

	/**
	 * 读取用户的特征文件保存到一个List<HashMap<Integer, Double>>中， 并获取的相应手机型号保存到testVersion中
	 */
	public void readTestUserMobileFeature() {
		LineNumberReader lnr = null;
		FeatureMatrx = new ArrayList();
		try {
			lnr = new LineNumberReader(new FileReader(userMobileFileName));
			String line = lnr.readLine();
			HashMap<Integer, Double> row = new HashMap<Integer, Double>();
			while (line != null) {
				String[] linePair = line.split(",");
				testVersion.add(linePair[0]);
				for (int i = 1; i < linePair.length; i++) {
					row.put(Integer.parseInt(linePair[i].split(":")[0]),
							Double.parseDouble(linePair[i].split(":")[1]));
				}
				FeatureMatrx.add(row);
				line = lnr.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
