package anaylysis;

import java.util.ArrayList;
import java.util.List;

public class Tools {

	public static void quickSortIndex(List<Integer> index, List<Double> list,
			int start, int end) {
		if (start >= end)
			return;
		int mid = partitionLargeToSmallIndex(index, list, start, end);
		quickSortIndex(index, list, start, mid - 1);
		quickSortIndex(index, list, mid + 1, end);

	}

	private static int partitionLargeToSmallIndex(List<Integer> index,
			List<Double> list, int start, int end) {
		double x = list.get(end);
		int i = start - 1;
		for (int j = start; j < end; j++) {
			if (list.get(j) >= x) {
				i++;
				double temp = list.get(i);
				list.set(i, list.get(j));
				list.set(j, temp);
				int indexTemp = index.get(i);
				index.set(i, index.get(j));
				index.set(j, indexTemp);
			}
		}
		double temp = list.get(i + 1);
		list.set(i + 1, list.get(end));
		list.set(end, temp);

		int indexTemp = index.get(i + 1);
		index.set(i + 1, index.get(end));
		index.set(end, indexTemp);
		return i + 1;
	}

	public static void quickSort(List<Double> list, int start, int end) {
		if (start >= end)
			return;
		int mid = partitionLargeToSmall(list, start, end);
		quickSort(list, start, mid - 1);
		quickSort(list, mid + 1, end);

	}

	private static int partitionSmallToLarge(List<Double> list, int start,
			int end) {
		double x = list.get(end);
		int i = start - 1;
		for (int j = start; j < end; j++) {
			if (list.get(j) <= x) {
				i++;
				double temp = list.get(i);
				list.set(i, list.get(j));
				list.set(j, temp);
			}
		}
		double temp = list.get(i + 1);
		list.set(i + 1, list.get(end));
		list.set(end, temp);
		return i + 1;
	}

	private static int partitionLargeToSmall(List<Double> list, int start,
			int end) {
		double x = list.get(end);
		int i = start - 1;
		for (int j = start; j < end; j++) {
			if (list.get(j) >= x) {
				i++;
				double temp = list.get(i);
				list.set(i, list.get(j));
				list.set(j, temp);
			}
		}
		double temp = list.get(i + 1);
		list.set(i + 1, list.get(end));
		list.set(end, temp);
		return i + 1;
	}

	public static void main(String arg[]) {
		List<Double> list = new ArrayList<Double>();
		List<Integer> index = new ArrayList<Integer>();
		list.add(2.0);
		list.add(7.0);
		list.add(3d);
		list.add(6d);
		list.add(4d);
		list.add(3d);
		for (int i = 0; i < list.size(); i++) {
			index.add(i);
		}
		Tools.quickSortIndex(index, list, 0, list.size() - 1);
		System.out.println(list);
		System.out.println(index);
	}
}
