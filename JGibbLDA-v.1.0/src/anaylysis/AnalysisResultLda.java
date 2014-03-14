package anaylysis;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AnalysisResultLda {
	PhiMatrix pMatrix = null;
	ThetaMatrix tMatrix = null;
	UserMobileUser uMobileUser = null;

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

	public AnalysisResultLda(String phiFileName, String thetaFileName,
			String userMobileFileName) {
		pMatrix = new PhiMatrix(phiFileName);
		tMatrix = new ThetaMatrix(thetaFileName);
		uMobileUser = new UserMobileUser(userMobileFileName);
	}

	public double getPercision() {

		List<HashMap<Integer, Double>> FeatureMatrx = uMobileUser
				.getFeatureMatrx();
		int sumOfUser = 0;
		int correctUser = 0;
		List<List<Double>> phi = pMatrix.getMatrix();
		int[] topicSum = new int[phi.size()];
		int[] topicCorrect = new int[phi.size()];
		HashMap<String, Integer> version2topic = tMatrix.getVersion2Topic();
		double phiMol[] = new double[phi.size()];
		// 算出每个phi向量的摩尔
		for (int i = 0; i < phi.size(); i++) {
			phiMol[i] = getMol(phi.get(i));
		}
		try {
			FileWriter fw = new FileWriter("result.csv");
			// 遍历测试的每个用户的特征向量
			for (int i = 0; i < FeatureMatrx.size(); i++) {
				// 单独一个人的特征向量的哈希
				HashMap<Integer, Double> userFeature = FeatureMatrx.get(i);
				int maxCosIndex = -1;// 保存最大相似度的topic下标
				int secCosIndex = -1;
				double maxCosValue = Double.NEGATIVE_INFINITY;// 最大相似度的值
				double secCosValue = Double.NEGATIVE_INFINITY;
				// 当前用户和每一个话题的host分布算相似度，保存最大的值到maxCosValue，下标到maxCosIndex
				for (int j = 0; j < phi.size(); j++) {
					// 得到j这个topic和userfeature这个用户的相似度
					double cosValue;
					cosValue = getCosListHah(phi.get(j), phiMol[j], userFeature);
					// cosValue = -getEuroDisListHash(phi.get(j), userFeature);
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
				// if
				// (!version2topic.containsKey(uMobileUser.testVersion.get(i)))
				// continue;
				int UserIndex = version2topic.get(uMobileUser.testVersion
						.get(i));
				//该手机训练数据集没有
				if (UserIndex == -1)
					continue;
				topicSum[UserIndex]++;
				sumOfUser++;
				if (UserIndex == maxCosIndex 
//						|| UserIndex == secCosIndex
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
		double re = correctUser / (double) sumOfUser;
		System.out.println(correctUser + "/" + sumOfUser);
		for (int i = 0; i < topicSum.length; i++)

		{
			int rate = (int) ((topicCorrect[i] / (double) topicSum[i]) * 100);
			System.out.print(i + ":" + topicCorrect[i] + "/" + topicSum[i]
					+ "=" + rate + "% ");
		}
		System.out.println("\nPercision=" + re);
		return re;
	}

	// 计算两个向量的相似度，cos,两个向量的点乘，除以各自的mol的乘积
	private double getCosListHah(List<Double> list, double listMol,
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
			squrSum = squrSum + Math.pow(itValue.next(), 2.0);
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

	public static void main(String arg[]) {
		AnalysisResultLda ar = new AnalysisResultLda("model-final.phi",
				"model-final.theta", "SameName_HostBehavior.txt");
		ar.getpMatrix().readMatrixFromPhi();// 读取phi矩阵
		ar.gettMatrix().readMatrixFromTheta();// 读取theta矩阵
		ar.gettMatrix().readLdaVersion("LdaUsersVersion_noInternet.txt", 0.8);// 生成手机型号对应topic的哈希，以便根据用户手机得知应该属于哪个话题
		ar.gettMatrix().outputLdaVersionTopics();// 输出手机型号对应什么话题的文件
		ar.getuMobileUser().readTestUserMobileFeature();// 读取用户的上网行为的特征文件
		ar.getPercision();// 计算正确率

	}
}
