package anaylysis;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

public class ThetaMatrix {
	private String thetaFileName;
	private String ldaVersionFileName;
	List<List<Double>> Matrix;
	List<List<Double>> MatrixVertical;
	List<String> ldaVersion;// 存储每一个手机型号的
	List<Integer> ldaVersionTopic;// 存储每一个手机型号对应的topic的标示符的
	// List<List<Double>> clusterCenter; // 存储每一个类的中心点的
	List<Integer> ldaVersionCluster;// 存储每一个手机型号对应的topic的类别的
	HashMap<String, Integer> Version2Topic;
	KmeansAdapter kmean;

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

	public void getVerticalMatrix() {
		MatrixVertical = new ArrayList<List<Double>>();
		for (int i = 0; i < Matrix.get(0).size(); i++) {
			List<Double> line = new ArrayList<Double>();
			for (int j = 0; j < Matrix.size(); j++) {
				line.add(Matrix.get(j).get(i));
			}
			MatrixVertical.add(line);
		}
	}

	/**
	 * 获取每行theta中matrix对应的手机型号,保存到versions中
	 */
	public void readLdaVersion(String LdaVersionFileName, double ramainRate,
			int k) {
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(
					LdaVersionFileName));
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
		if (k > 0)
			setVersion2TopicFromCCenter(k);
		else
			setVersion2TopicFromThetaMatrix(ramainRate);
		// 利用kmeans分类

	}

	private void setVersion2TopicFromThetaMatrix(double ramainRate) {
		ldaVersionTopic = new ArrayList<Integer>();
		Version2Topic = new HashMap<String, Integer>();
		int trainSet = 0;
		for (int i = 0; i < Matrix.size(); i++) {
			List<Double> row = Matrix.get(i);
			trainSet++;
			// 得到該行最大的概率值，返回index下标
			int index = getMaxIndex(row);

			// 训练后该手机概率小于指标的话，就否定该手机属于该话题
			if (row.get(index) < ramainRate) {
				Version2Topic.put(ldaVersion.get(i), -1);
				ldaVersionTopic.add(-1);
			} else {
				Version2Topic.put(ldaVersion.get(i), index);
				ldaVersionTopic.add(index);
			}

		}
		System.out.println("theta matrix row：" + trainSet);
	}

	private void setVersion2TopicFromCCenter(int k) {
		double[][] a = ArrayListFuncs.doubleList2Array(Matrix);
		kmean = new KmeansAdapter(k, a);
		kmean.Kmeans();
		ldaVersionTopic = new ArrayList<Integer>();
		Version2Topic = new HashMap<String, Integer>();
		for (int i = 0; i < kmean.dataCenterlabel.length; i++) {
			Version2Topic.put(ldaVersion.get(i), kmean.dataCenterlabel[i]);
			ldaVersionTopic.add(kmean.dataCenterlabel[i]);
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
			fw = new FileWriter("3_Theta_ldaVersion-Topic.csv");
			for (int i = 0; i < Matrix.size(); i++) {
				List row = Matrix.get(i);
				// 得到該行最大的概率值，返回index下标
				int index = ldaVersionTopic.get(i);
				fw.write(ldaVersion.get(i) + "," + index
				// + "," + ((index == -1) ? "none" : row.get(index))
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
	private int getMaxIndex(List<Double> T) {
		Double obj = T.get(0);
		int index = 0;
		for (int i = 0; i < T.size(); i++) {
			if (T.get(i).compareTo(T.get(index)) > 0)
				index = i;
		}
		return index;
	}

	public static void main(String arg[]) {
		ThetaMatrix tm = new ThetaMatrix("model-final.theta");
		tm.readMatrixFromTheta();
		tm.readLdaVersion("LdaUsersVersion_noInternet.txt", 0.8, -1);
		tm.outputLdaVersionTopics();
	}
}
