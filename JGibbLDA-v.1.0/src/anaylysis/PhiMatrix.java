package anaylysis;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class PhiMatrix {
	String PhiFileName;
	List<List<Double>> Matrix;

	public PhiMatrix(String phiFileName) {
		this.setPhiFileName(phiFileName);
	}

	/**
	 * @return the phiFileName
	 */
	public String getPhiFileName() {
		return PhiFileName;
	}

	/**
	 * @param phiFileName
	 *            the phiFileName to set
	 */
	public void setPhiFileName(String phiFileName) {
		PhiFileName = phiFileName;
	}

	/**
	 * @return the matrix
	 */
	public List<List<Double>> getMatrix() {
		return Matrix;
	}

	public void readMatrixFromPhi() {
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(
					this.getPhiFileName()));
			String line = lnr.readLine();
			Matrix = new ArrayList<List<Double>>();
			while (line != null) {
				String[] topicRatios = line.split(" ");
				List<Double> row = new ArrayList<Double>();
				// System.out.print(topicRatios.length + "  ");
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
}
