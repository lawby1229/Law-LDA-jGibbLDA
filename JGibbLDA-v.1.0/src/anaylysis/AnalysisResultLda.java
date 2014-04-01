package anaylysis;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AnalysisResultLda {
	PhiMatrix pMatrix = null;
	ThetaMatrix tMatrix = null;
	UserMobileUser uMobileUser = null;
	FileWriter fwResult = null;

	private void writeToFile(String str) {
		try {
			fwResult.append(str);
			fwResult.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the pMatrix
	 */
	public PhiMatrix getpMatrix() {
		return pMatrix;
	}

	/**
	 * @return the tMatrix
	 */
	public ThetaMatrix gettMatrix() {
		return tMatrix;
	}

	/**
	 * @return the uMobileUser
	 */
	public UserMobileUser getuMobileUser() {
		return uMobileUser;
	}

	public AnalysisResultLda(String suff, String phiFileName,
			String thetaFileName, String userMobileFileName) {
		pMatrix = new PhiMatrix(suff + phiFileName);
		tMatrix = new ThetaMatrix(suff + thetaFileName);
		uMobileUser = new UserMobileUser(suff + userMobileFileName);
		try {
			fwResult = new FileWriter(new File(suff + "Persicion.txt"), false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 根据lda的inf的测试结果的theta矩阵求出预测
	 * 
	 * @param infThetaFileName
	 * @return
	 */
	public double getLdaInfPercision(String infThetaFileName,
			String infLdaVersionFileName) {
		ThetaMatrix infMatrix = new ThetaMatrix(infThetaFileName);
		infMatrix.readMatrixFromTheta();
		infMatrix.readLdaVersion(infLdaVersionFileName, 0.5, 0);
		int sum = 0;
		int count = 0;
		double Precision = 0;
		int[] topicPrecisionSum = new int[infMatrix.Matrix.get(0).size()];
		int[] topicRecallSum = new int[infMatrix.Matrix.get(0).size()];
		int[] topicCorrect = new int[infMatrix.Matrix.get(0).size()];
		// 算处理Precision和Recall
		for (int i = 0; i < infMatrix.Matrix.size(); i++) {
			// 小于域值就是-1都不要
			int indexCorrect = this.tMatrix.Version2Topic
					.get(infMatrix.ldaVersion.get(i));
			if (indexCorrect == -1 || infMatrix.ldaVersionTopic.get(i) == -1)
				continue;
			if (indexCorrect == infMatrix.ldaVersionTopic.get(i)) {
				count++;
				topicCorrect[indexCorrect]++;
			}
			topicPrecisionSum[indexCorrect]++;
			topicRecallSum[infMatrix.ldaVersionTopic.get(i)]++;
			sum++;
		}
		// MAP Mean Avarange Precision
		writeToFile("SMAP Inf\n");
		System.out.print("SMAP Inf\n");
		infMatrix.getVerticalMatrix();
		List<Integer> indexs = new ArrayList<Integer>();// 每一个测试结果的排名
		double Map = 0;// 保存MAP
		for (int i = 0; i < infMatrix.MatrixVertical.size(); i++) {
			for (int j = 0; j < infMatrix.MatrixVertical.get(i).size(); j++)
				indexs.add(j);
			// 测试矩阵按照概率高到低排名，得到indexs的列表
			Tools.quickSortIndex(indexs, infMatrix.MatrixVertical.get(i), 0,
					infMatrix.MatrixVertical.get(i).size() - 1);
			int rank = 1;//排名，前10个中是该topic的样本
			double AP[] = new double[infMatrix.MatrixVertical.size()];
			for (int j = 1; j <= 10; j++) {//选取前10个样本测试排名
				if (infMatrix.ldaVersionTopic.get(indexs.get(j - 1)) == i) {
					AP[i] += rank / j;
					rank++;
				}
			}
			AP[i] = AP[i] / (rank - 1);
			Map += AP[i];
			writeToFile(AP[i] + " ");
			System.out.print(AP[i] + " ");
		}
		Map = Map / infMatrix.MatrixVertical.size();
		writeToFile("MAP=" + Map + "\n");
		System.out.print("MAP=" + Map + "\n");

		// writeToFile("Percision Inf\n");

		// System.out.println(count + "/" + sum);
		// writeToFile(count + "/" + sum);
		for (int i = 0; i < topicPrecisionSum.length; i++) {
			int subPrecision = (int) ((topicCorrect[i] / (double) topicPrecisionSum[i]) * 100);
			System.out.print(i + ":" + topicCorrect[i] + "/"
					+ topicPrecisionSum[i] + "=" + subPrecision + "% ");
			writeToFile(i + ":" + topicCorrect[i] + "/" + topicPrecisionSum[i]
					+ "=" + subPrecision + "% ");
			int subRecall = (int) ((topicCorrect[i] / (double) topicRecallSum[i]) * 100);
			System.out.print(i + ":" + topicCorrect[i] + "/"
					+ topicRecallSum[i] + "=" + subRecall + "% ");
			writeToFile(i + ":" + topicCorrect[i] + "/" + topicRecallSum[i]
					+ "=" + subRecall + "% ");
		}
		Precision = count / (double) sum;
		System.out.print("\nPercision=" + Precision + "\n");
		writeToFile("\nPercision=" + Precision + "\n");
		return Precision;
	}

	public double getPercision(int k) {

		List<HashMap<Integer, Double>> FeatureMatrx = uMobileUser
				.getFeatureMatrx();
		int sumOfUser = 0;
		int correctUser = 0;
		List<List<Double>> phi = pMatrix.getMatrix();
		int[] topicSum;
		int[] topicCorrect;
		if (k > 0) {
			topicSum = new int[k];
			topicCorrect = new int[k];
		} else {
			topicSum = new int[phi.size()];
			topicCorrect = new int[phi.size()];
		}
		HashMap<String, Integer> version2topic = tMatrix.getVersion2Topic();
		double phiMol[] = new double[phi.size()];
		// 算出每个phi向量的摩尔
		for (int i = 0; i < phi.size(); i++) {
			phiMol[i] = getMol(phi.get(i));
		}
		try {
			FileWriter fw = new FileWriter("3_ANA_result.csv");
			// 遍历测试的每个用户的特征向量
			for (int i = 0; i < FeatureMatrx.size(); i++) {
				// 单独一个人的特征向量的哈希
				HashMap<Integer, Double> userFeature = FeatureMatrx.get(i);
				int maxCosIndex = -1;// 保存最大相似度的topic下标
				int secCosIndex = -1;
				double maxCosValue = Double.NEGATIVE_INFINITY;// 最大相似度的值
				double secCosValue = Double.NEGATIVE_INFINITY;
				// 当前用户和每一个话题的host分布算相似度，保存最大的值到maxCosValue，下标到maxCosIndex
				List<List<Double>> centerHostMatrix = phi;
				// 需要根据词的分布来判断哪些文章会出现该话题
				// 需要通过userFeature找到topic的k个维度的系数作为
				// *******************用kmeans就不注释，不用就注释
				if (k > 0)
					centerHostMatrix = multiMatrix(tMatrix.kmean.centers, phi);
				// 得到j这个topic和userfeature这个用户的相似度
				for (int j = 0; j < centerHostMatrix.size(); j++) {
					double cosValue;
					cosValue = getCosListHash(centerHostMatrix.get(j),
							getMol(centerHostMatrix.get(j)),
							normalization(userFeature));
					if (cosValue > maxCosValue) {
						secCosIndex = maxCosIndex;
						secCosValue = maxCosValue;
						maxCosIndex = j;
						maxCosValue = cosValue;
					} else if (cosValue > secCosValue) {
						secCosIndex = j;
						secCosValue = cosValue;
					}

				}
				// 获取这个用户的真实手机在训练模型中的topic和算出来的topic比较是否相等，相等就+1
				// if (i < 30)

				fw.write(uMobileUser.testVersion.get(i) + ","
						+ version2topic.get(uMobileUser.testVersion.get(i))
						+ "," + maxCosIndex + "," + maxCosValue + ","
						+ secCosIndex + "," + secCosValue
						// + ","+ userFeature
						+ "\n");
				if (!version2topic.containsKey(uMobileUser.testVersion.get(i))) {
					System.out.println(uMobileUser.testVersion.get(i));
					continue;
				}
				int UserIndex = version2topic.get(uMobileUser.testVersion
						.get(i));
				// 该手机训练数据集没有
				if (UserIndex == -1)
					continue;
				topicSum[UserIndex]++;
				sumOfUser++;
				if (UserIndex == maxCosIndex
				// || UserIndex == secCosIndex
				) {
					correctUser++;
					topicCorrect[UserIndex]++;
					// System.out.println(uMobileUser.testVersion.get(i));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeToFile("Percision Statistic\n");
		double re = correctUser / (double) sumOfUser;
		System.out.println(correctUser + "/" + sumOfUser);
		writeToFile(correctUser + "/" + sumOfUser);
		for (int i = 0; i < topicSum.length; i++)

		{
			int rate = (int) ((topicCorrect[i] / (double) topicSum[i]) * 100);
			System.out.print(i + ":" + topicCorrect[i] + "/" + topicSum[i]
					+ "=" + rate + "% ");
			writeToFile(i + ":" + topicCorrect[i] + "/" + topicSum[i] + "="
					+ rate + "% ");
		}
		System.out.println("\nPercision=" + re);
		writeToFile("\nPercision=" + re + "\n");
		return re;
	}

	// 计算两个向量的相似度，cos,两个向量的点乘，除以各自的mol的乘积
	private double getCosListHash(List<Double> list, double listMol,
			HashMap<Integer, Double> hash) {
		double re = 0;
		re = getPointMulti(list, hash) / (listMol * getMol(hash));
		return re;
	}

	private double getEuroDisListHash(List<Double> list,
			HashMap<Integer, Double> hash) {
		Iterator<Integer> itKey = hash.keySet().iterator();
		double re = 0;
		while (itKey.hasNext()) {
			int key = itKey.next();
			re = re + Math.pow(list.get(key) - hash.get(key), 2);
		}
		return Math.sqrt(re);
	}

	private double getMol(HashMap<Integer, Double> hash) {
		Iterator<Double> itValue = hash.values().iterator();
		double squrSum = 0;
		while (itValue.hasNext())
			squrSum = squrSum + Math.pow(itValue.next(), 2);
		return Math.sqrt(squrSum);
	}

	private double getMol(List<Double> list) {
		double squrSum = 0;
		for (int i = 0; i < list.size(); i++)
			squrSum = squrSum + Math.pow(list.get(i), 2);
		return Math.sqrt(squrSum);
	}

	private double getPointMulti(List<Double> list,
			HashMap<Integer, Double> hash) {
		Iterator<Integer> itKey = hash.keySet().iterator();
		double re = 0;
		while (itKey.hasNext()) {
			int key = itKey.next();
			re = re + list.get(key) * hash.get(key);
		}
		return re;
	}

	private List<List<Double>> multiMatrix(double[][] d, List<List<Double>> l) {
		double[][] re = multiMatrix(d, ArrayListFuncs.doubleList2Array(l));
		return ArrayListFuncs.doubleArray2List(re);
	}

	private double[][] multiMatrix(double[][] m, double[][] n) {
		double[][] result = new double[m.length][n[0].length];
		for (int i = 0; i < m.length; i++) {
			for (int k = 0; k < n[0].length; k++) {
				double sum = 0;
				for (int j = 0; j < m[0].length; j++) {
					sum += m[i][j] * n[j][k];
				}
				result[i][k] = sum;
			}
		}

		return result;
	}

	public List<Double> normalization(List<Double> l) {
		List<Double> lre = new ArrayList<Double>();
		double sum = 0;
		for (int i = 0; i < l.size(); i++) {
			sum += l.get(i);
		}
		for (int i = 0; i < l.size(); i++) {
			lre.add(l.get(i) / sum);
		}
		return lre;
	}

	public HashMap<Integer, Double> normalization(HashMap<Integer, Double> hash) {
		HashMap<Integer, Double> hashRe = new HashMap<Integer, Double>();
		double sum = 0;
		Iterator<Double> iValue = hash.values().iterator();
		while (iValue.hasNext()) {
			sum += iValue.next();
		}
		Iterator<Integer> iKey = hash.keySet().iterator();
		while (iKey.hasNext()) {
			int key = iKey.next();
			hashRe.put(key, hash.get(key) / sum);
		}
		return hashRe;
	}

	public static void main(String arg[]) {
		String suff = "./152_lda_t2k_top10_70%/";
		AnalysisResultLda ar = new AnalysisResultLda(suff, "model-final.phi",
				"model-final.theta", "20_SameName_HostBehavior");
		int k = 0;
		ar.getpMatrix().readMatrixFromPhi();// 读取phi矩阵
		ar.gettMatrix().readMatrixFromTheta();// 读取theta矩阵
		ar.gettMatrix().readLdaVersion(
				suff + "1_LdaUsersVersion_noInternet_2k", 0.8, k);// 生成手机型号对应topic的哈希，以便根据用户手机得知应该属于哪个话题
		ar.gettMatrix().outputLdaVersionTopics();// 输出手机型号对应什么话题的文件
		ar.getuMobileUser().readTestUserMobileFeature();// 读取用户的上网行为的特征文件
		ar.getPercision(k);// 计算正确率
		ar.getLdaInfPercision(suff
				+ "211_SameName_HostBehavior_lda-data.model-final.theta", suff
				+ "210_SameName_HostBehavior_lda-version");

	}
}
