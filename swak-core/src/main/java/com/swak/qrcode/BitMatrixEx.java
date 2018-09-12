package com.swak.qrcode;

import com.google.zxing.common.BitMatrix;

/**
 * 扩展的二维码矩阵信息， 主要新增了三个位置探测图形的判定
 * @author lifeng
 */
public class BitMatrixEx {

	private final int width;
	private final int height;
	private final int rowSize;
	private final int[] bits;
	private int leftPadding; // 左白边大小
	private int topPadding; // 上白边大小
	private int multiple; // 矩阵信息缩放比例

	private BitMatrix bitMatrix;

	public BitMatrixEx(BitMatrix bitMatrix) {
		this(bitMatrix.getWidth(), bitMatrix.getHeight());
		this.bitMatrix = bitMatrix;

	}
	private BitMatrixEx(int width, int height) {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException("Both dimensions must be greater than 0");
		}

		this.width = width;
		this.height = height;
		this.rowSize = (width + 31) / 32;
		bits = new int[rowSize * height];
	}
	public void setRegion(int left, int top, int width, int height) {
		int right = left + width;
		int bottom = top + height;

		for (int y = top; y < bottom; y++) {
			int offset = y * rowSize;
			for (int x = left; x < right; x++) {
				bits[offset + (x / 32)] |= 1 << (x & 0x1f);
			}
		}
	}
	public boolean get(int x, int y) {
		return bitMatrix.get(x, y);
	}
	public boolean isDetectCorner(int x, int y) {
		int offset = y * rowSize + (x / 32);
		return ((bits[offset] >>> (x & 0x1f)) & 1) != 0;
	}
	public int getLeftPadding() {
		return leftPadding;
	}
	public void setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
	}
	public int getTopPadding() {
		return topPadding;
	}
	public void setTopPadding(int topPadding) {
		this.topPadding = topPadding;
	}
	public int getMultiple() {
		return multiple;
	}
	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}
	public BitMatrix getBitMatrix() {
		return bitMatrix;
	}
	public void setBitMatrix(BitMatrix bitMatrix) {
		this.bitMatrix = bitMatrix;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public int getRowSize() {
		return rowSize;
	}
	public int[] getBits() {
		return bits;
	}
}