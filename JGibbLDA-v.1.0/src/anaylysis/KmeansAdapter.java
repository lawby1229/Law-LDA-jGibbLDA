package anaylysis;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import sun.security.util.Length;

public class KmeansAdapter {
	int k = 0;
	double[][] data;
	int[] dataCenterlabel;
	double[][] centers;
	int repeat = 0;

	public KmeansAdapter(int k, double[][] data) {
		this.k = k;
		this.data = data;
		centers = new double[k][data[0].length];
		dataCenterlabel = new int[data.length];

	}

	public void Kmeans() {
		double[][] centersNew;
		double centerDis = 0;
		initCenters();
		while (centerDis < 10E-6) {
			partitionLabels();// 计算每个data属于哪个类别
			centersNew = getNewCenters();// 重新计算中心点
			centerDis = centerDis(centersNew, centers);// 得到新旧中心点的距离
			repeat++;
		}
		printInfo();
	}

	public void printInfo() {
		System.out.println("迭代：" + repeat);
		System.out.println("各个属于类别：" + dataCenterlabel);
	}

	/**
	 * 初始化各个中心点
	 */
	public void initCenters() {
		int dataRowNum = 0;
		Random rand = new Random();
		for (int i = 0; i < centers.length; i++) {
			dataRowNum = rand.nextInt(centers[0].length);
			for (int j = 0; j < centers[i].length; j++) {
				centers[i][j] = data[dataRowNum][j];
			}
		}
	}

	/**
	 * 計算每個点属于的类号，就是离哪个中心点进
	 */
	public void partitionLabels() {
		for (int i = 0; i < data.length; i++) {
			double[] dataLine = data[i];
			int mincenterIndex = -1;
			double mincenterValue = Double.POSITIVE_INFINITY;
			for (int j = 0; j < centers.length; j++) {
				double centerValue = caculateDis(dataLine, centers[j]);
				if (centerValue < mincenterValue) {
					mincenterValue = centerValue;
					mincenterIndex = j;
				}
			}
			dataCenterlabel[i] = mincenterIndex;
		}
	}

	/**
	 * 得到新的中心点集合
	 * 
	 * @return
	 */
	public double[][] getNewCenters() {
		double[][] centersNew = new double[k][data[0].length];
		int[] centersCnt = new int[k];
		for (int i = 0; i < data.length; i++) {
			centersCnt[dataCenterlabel[i]]++;
			for (int j = 0; j < data[i].length; j++)
				centersNew[dataCenterlabel[i]][j] = data[i][j];
		}
		for (int i = 0; i < centersNew.length; i++) {
			for (int j = 0; j < centersNew[i].length; j++) {
				centersNew[i][j] = centersNew[i][j] / centersCnt[i];
			}
		}
		return centersNew;
	}

	/**
	 * 计算old中心点和新中心点的距离和
	 * 
	 * @param centersNew
	 * @param centersOld
	 * @return
	 */
	public double centerDis(double[][] centersNew, double[][] centersOld) {
		double sum = 0;
		for (int i = 0; i < centersNew.length; i++) {
			sum = sum + caculateDis(centersNew[i], centersOld[i]);
		}
		return sum;
	}

	/**
	 * 计算两个向量的距离
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public double caculateDis(double[] a, double[] b) {
		double sumDis = 0;
		for (int i = 0; i < a.length; i++) {
			sumDis = sumDis + Math.pow(a[i] - b[i], 2);
		}
		return Math.sqrt(sumDis);
	}
}
