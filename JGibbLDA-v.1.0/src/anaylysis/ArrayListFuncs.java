package anaylysis;

import java.util.ArrayList;
import java.util.List;

public class ArrayListFuncs {
	public static List<Integer> intArray2List(int[] a) {
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < a.length; i++) {
			l.add(a[i]);
		}
		return l;
	}

	public static List<List<Integer>> intArray2List(int[][] a) {
		List<List<Integer>> l = new ArrayList<List<Integer>>();
		for (int i = 0; i < a.length; i++) {
			List<Integer> lIn = new ArrayList<Integer>();
			for (int j = 0; j < a[i].length; j++) {
				lIn.add(a[i][j]);
			}
			l.add(lIn);
		}
		return l;
	}

	public static List<Double> doubleArray2List(double[] d) {
		List<Double> l = new ArrayList<Double>();
		for (int i = 0; i < d.length; i++) {
			l.add(d[i]);
		}
		return l;
	}

	public static List<List<Double>> doubleArray2List(double[][] d) {
		List<List<Double>> l = new ArrayList<List<Double>>();
		for (int i = 0; i < d.length; i++) {
			List<Double> lIn = new ArrayList<Double>();
			for (int j = 0; j < d[i].length; j++) {
				lIn.add(d[i][j]);
			}
			l.add(lIn);
		}
		return l;
	}

	public static int[][] intList2Array(List<List<Integer>> l) {
		int[][] a = new int[l.size()][l.get(0).size()];
		for (int i = 0; i < l.size(); i++) {
			List<Integer> lIn = l.get(i);
			for (int j = 0; j < lIn.size(); j++) {
				a[i][j] = lIn.get(j);
			}

		}
		return a;
	}

	public static double[][] doubleList2Array(List<List<Double>> l) {
		double[][] a = new double[l.size()][l.get(0).size()];
		for (int i = 0; i < l.size(); i++) {
			List<Double> lIn = l.get(i);
			for (int j = 0; j < lIn.size(); j++) {
				a[i][j] = lIn.get(j);
			}

		}
		return a;
	}

	public static double[] doubleList2Array(List<Double> l) {
		double[] a = new double[l.size()];
		for (int i = 0; i < l.size(); i++) {

			a[i] = l.get(i);

		}
		return a;
	}
}
