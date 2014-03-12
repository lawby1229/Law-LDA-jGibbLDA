package anaylysis;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ThetaMatrix {
	private String thetaFileName;
	private String ldaVersionFileName;
	List<List<Double>> Matrix;
	List<String> ldaVersion;
	List<Integer> ldaVersionTopic;
	HashMap<String, Integer> Version2Topic;

	public ThetaMatrix(String thetaFile) {
		this.setThetaFileName(thetaFile);

	}

	public ThetaMatrix(String thetaFile, String ldaVersionFile) {
		this.setThetaFileName(thetaFile);
		this.setLdaVersionFileName(ldaVersionFile);

	}

	/**
	 * @return the matrix
	 */
	public List<List<Double>> getMatrix() {
		return Matrix;
	}

	/**
	 * @param matrix
	 *            the matrix to set
	 */
	private void setMatrix(List<List<Double>> matrix) {
		Matrix = matrix;
	}

	/**
	 * @return the thetaFileName
	 */
	public String getThetaFileName() {
		return thetaFileName;
	}

	/**
	 * @param thetaFileName
	 *            the thetaFileName to set
	 */
	private void setThetaFileName(String thetaFileName) {
		this.thetaFileName = thetaFileName;
	}

	/**
	 * @return the ldaVersionFileName
	 */
	public String getLdaVersionFileName() {
		return ldaVersionFileName;
	}

	/**
	 * @param ldaVersionFileName
	 *            the ldaVersionFileName to set
	 */
	private void setLdaVersionFileName(String ldaVersionFileName) {
		this.ldaVersionFileName = ldaVersionFileName;
	}

	/**
	 * @return the ldaVersion
	 */
	public List<String> getLdaVersion() {
		return ldaVersion;
	}

	/**
	 * @param ldaVersion
	 *            the ldaVersion to set
	 */
	private void setLdaVersion(List<String> ldaVersion) {
		this.ldaVersion = ldaVersion;
	}

	/**
	 * 从theta文件中读取二维矩阵
	 */
	public void readMatrixFromTheta() {
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(
					this.getThetaFileName()));
			String line = lnr.readLine();
			Matrix = new ArrayList<List<Double>>();
			while (line != null) {
				String[] topicRatios = line.split(" ");
				List row = new ArrayList<Double>();
				for (int i = 0; i < topicRatios.length; i++) {
					row.add(Double.parseDouble(topicRatios[i]));
				}
				Matrix.add(row);
				line = lnr.readLine();
			}
			lnr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取每行theta中matrix对应的手机型号,保存到versions中
	 */
	public void readLdaVersion() {
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(
					this.getLdaVersionFileName()));
			List matrix = this.getMatrix();
			ldaVersion = new ArrayList<String>();
			String line = lnr.readLine();
			while (line != null) {
				ldaVersion.add(line.split(",")[0]);
				line = lnr.readLine();
			}
			lnr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ldaVersionTopic = new ArrayList<Integer>();
		for (int i = 0; i < Matrix.size(); i++) {
			List row = Matrix.get(i);

			// 得到該行最大的概率值，返回index下标
			int index = getMaxIndex(row);
			Version2Topic.put(ldaVersion.get(i), index);
			ldaVersionTopic.add(index);
		}
	}

	/**
	 * @return the version2Topic
	 */
	public HashMap<String, Integer> getVersion2Topic() {
		return Version2Topic;
	}

	public void outputLdaVersionTopics() {

		FileWriter fw = null;
		try {
			fw = new FileWriter("ldaVersion-Topic.csv");
			for (int i = 0; i < Matrix.size(); i++) {
				List row = Matrix.get(i);
				// 得到該行最大的概率值，返回index下标
				int index = ldaVersionTopic.get(i);
				fw.write(ldaVersion.get(i) + "," + index + "," + row.get(index)
						+ "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// <T extends Comparable<? super T>>
	private int getMaxIndex(List<Comparable> T) {
		Object obj = T.get(0);
		int index = 0;
		for (int i = 0; i < T.size(); i++) {
			if (T.get(i).compareTo(T.get(index)) > 0)
				index = i;
		}
		return index;
	}

	public static void main(String arg[]) {
		ThetaMatrix tm = new ThetaMatrix("model-final.theta",
				"LDA_3_Version.txt");
		tm.readMatrixFromTheta();
		tm.readLdaVersion();
		tm.outputLdaVersionTopics();
	}
}
