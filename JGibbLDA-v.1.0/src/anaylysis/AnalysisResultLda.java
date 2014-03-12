package anaylysis;

import java.io.FileReader;
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
		double sumOfUser = FeatureMatrx.size();
		int correctUser = 0;
		List<List<Double>> phi = pMatrix.getMatrix();
		HashMap<String, Integer> version2topic = tMatrix.getVersion2Topic();
		// 遍历测试的每个用户的特征向量
		for (int i = 0; i < FeatureMatrx.size(); i++) {
			// 单独一个人的特征向量的哈希
			HashMap<Integer, Double> userFeature = FeatureMatrx.get(i);
			int maxCosIndex = 0;// 保存最大相似度的topic下标
			double maxCosValue = Double.NEGATIVE_INFINITY;// 最大相似度的值
			// 当前用户和每一个话题的host分布算相似度，保存最大的值到maxCosValue，下标到maxCosIndex
			for (int j = 0; j < phi.size(); j++) {
				// 得到j这个topic和userfeature这个用户的相似度
				double cosValue = getCosListHah(phi.get(j), userFeature);
				if (cosValue > maxCosValue) {
					maxCosIndex = j;
					maxCosValue = cosValue;
				}
			}
			// 获取这个用户的真实手机在训练模型中的topic和算出来的topic比较是否相等，相等就+1
			if (version2topic.get(uMobileUser.testVersion.get(i)) == maxCosIndex)
				correctUser++;
		}
		return correctUser / sumOfUser;
	}

	private double getCosListHah(List<Double> list,
			HashMap<Integer, Double> hash) {

		Iterator<Integer> itKey = hash.keySet().iterator();
		return 0;
	}

	public static void main(String arg[]) {
		AnalysisResultLda ar = new AnalysisResultLda("model-final.phi",
				"model-final.theta", "SameName_HostBehavior.txt");
		ar.getpMatrix().readMatrixFromPhi();
		ar.gettMatrix().readMatrixFromTheta();
		ar.gettMatrix().readLdaVersion();
		ar.getuMobileUser().readTestUserMobileFeature();

	}
}
