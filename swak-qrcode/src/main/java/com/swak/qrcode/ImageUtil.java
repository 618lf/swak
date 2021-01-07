package com.swak.qrcode;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.swak.utils.FileUtils;
import com.swak.utils.IOUtils;

/**
 * Created by yihui on 2017/4/7.
 */
public class ImageUtil {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	/**
	 * <p>
	 * 生成圆角图片 & 圆角边框
	 *
	 * @param image     原图
	 * @param logoStyle 圆角的角度
	 * @param size      边框的边距
	 * @param color     边框的颜色
	 * @return 返回带边框的圆角图
	 */
	public static BufferedImage makeRoundBorder(BufferedImage image, QrcodeOptions.LogoStyle logoStyle, int size,
			Color color) {
		// 将图片变成圆角
		int cornerRadius = 0;
		if (logoStyle == QrcodeOptions.LogoStyle.ROUND) {
			cornerRadius = image.getWidth() / 4;
			image = makeRoundedCorner(image, cornerRadius);
		}

		int w = image.getWidth() + size;
		int h = image.getHeight() + size;
		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = output.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(color == null ? Color.WHITE : color);
		g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

		// ... then compositing the image on top,
		// using the white shape from above as alpha source
		// g2.setComposite(AlphaComposite.SrcAtop);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
		g2.drawImage(image, size / 2, size / 2, null);
		g2.dispose();

		return output;
	}

	/**
	 * 生成圆角图片
	 *
	 * @param image        原始图片
	 * @param cornerRadius 圆角的弧度
	 * @return 返回圆角图
	 */
	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = output.createGraphics();

		// This is what we want, but it only does hard-clipping, i.e. aliasing
		// g2.setClip(new RoundRectangle2D ...)

		// so instead fake soft-clipping by first drawing the desired clip shape
		// in fully opaque white with antialiasing enabled...
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

		// ... then compositing the image on top,
		// using the white shape from above as alpha source
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);

		g2.dispose();

		return output;
	}

	/**
	 * 在图片中间,插入圆角的logo
	 *
	 * @param qrCode      原图
	 * @param logo        logo地址
	 * @param logoStyle   logo 的样式 （圆角， 直角）
	 * @param logoBgColor logo的背景色
	 * @throws IOException
	 */
	public static BufferedImage drawLogo(BufferedImage qrCode, BufferedImage logo, QrcodeOptions.LogoStyle logoStyle,
			Color logoBgColor) throws IOException {
		int QRCODE_WIDTH = qrCode.getWidth();
		int QRCODE_HEIGHT = qrCode.getHeight();

		// 获取logo图片
		BufferedImage bf = logo;
		int size = bf.getWidth() / 15;
		bf = ImageUtil.makeRoundBorder(bf, logoStyle, size, logoBgColor); // 边距为logo的1/15

		// logo的宽高
		int logoRate = 12;
		int w = bf.getWidth() > QRCODE_WIDTH * 2 / logoRate ? QRCODE_WIDTH * 2 / logoRate : bf.getWidth();
		int h = bf.getHeight() > QRCODE_HEIGHT * 2 / logoRate ? QRCODE_HEIGHT * 2 / logoRate : bf.getHeight();

		int x = (QRCODE_WIDTH - w) >> 1;
		int y = (QRCODE_HEIGHT - h) >> 1;

		return drawImage(qrCode, bf, w, h, x, y);
	}

	/**
	 * 两张图片绘制在一起， 制在正中间
	 *
	 * @param background 背景图
	 * @param image      需要绘制的图
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage drawImage(BufferedImage background, BufferedImage image) throws IOException {
		int bgW = background.getWidth();
		int bgH = background.getHeight();
		int imgW = image.getWidth();
		int imgH = image.getHeight();

		// 图片的大小不应大于原图
		if (imgW > bgW) {
			imgW = bgW;
		}

		if (imgH > bgH) {
			imgH = bgH;
		}

		// 绘制图片
		int imgX = (bgW - imgW) >> 1;
		int imgY = (bgH - imgH) >> 1;
		return drawImage(background, image, imgW, imgH, imgX, imgY);
	}

	/**
	 * 两张图片绘制在一起
	 *
	 * @param background 背景图
	 * @param image      需要绘制的图
	 * @param imgW       图片图宽
	 * @param imgH       图片图高
	 * @param imgX       图片X坐标
	 * @param imgY       图片Y坐标
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage drawImage(BufferedImage background, BufferedImage image, int imgW, int imgH, int imgX,
			int imgY) throws IOException {
		int bgW = background.getWidth();
		int bgH = background.getHeight();

		// 图片的大小不应大于原图
		if (imgW > bgW) {
			imgW = bgW;
		}

		if (imgH > bgH) {
			imgH = bgH;
		}

		// 绘制图片 -- 透明度， 避免看不到背景
		Graphics2D g2d = background.createGraphics();
		if (imgW == bgW) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.85f));
		}
		g2d.drawImage(image, imgX, imgY, imgW, imgH, null);
		if (imgW == bgW) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
		}
		g2d.dispose();
		background.flush();
		return background;
	}

	/**
	 * 绘制背景图
	 *
	 * @param background 背景图
	 * @param bgW        背景图宽
	 * @param bgH        背景图高
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage drawImage(BufferedImage background, int bgW, int bgH) throws IOException {

		// 获取背景图
		BufferedImage bg = background;
		if (bg.getWidth() != bgW || bg.getHeight() != bgH) { // 需要缩放
			BufferedImage temp = new BufferedImage(bgW, bgH, BufferedImage.TYPE_INT_ARGB);
			temp.getGraphics().drawImage(bg.getScaledInstance(bgW, bgH, Image.SCALE_SMOOTH), 0, 0, null);
			bg = temp;
		}
		bg.flush();
		return bg;
	}

	/**
	 * 基本的二维码图片
	 *
	 * @return
	 * @throws WriterException
	 */
	public static BufferedImage drawQrcode(BitMatrixEx bitMatrix) {
		int qrCodeWidth = bitMatrix.getWidth();
		int qrCodeHeight = bitMatrix.getHeight();
		BufferedImage qrCode = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < qrCodeWidth; x++) {
			for (int y = 0; y < qrCodeHeight; y++) {
				qrCode.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
			}
		}
		qrCode.flush();
		return qrCode;
	}

	/**
	 * 基本的二维码图片
	 *
	 * @return
	 * @throws WriterException
	 */
	public static BufferedImage drawQrcode(String message, Map<EncodeHintType, Object> hints, int weight, int height)
			throws WriterException {
		BitMatrixEx bitMatrix = buildBitMatrixEx(message, hints, weight, height);
		int qrCodeWidth = bitMatrix.getWidth();
		int qrCodeHeight = bitMatrix.getHeight();
		BufferedImage qrCode = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < qrCodeWidth; x++) {
			for (int y = 0; y < qrCodeHeight; y++) {
				qrCode.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
			}
		}
		qrCode.flush();
		return qrCode;
	}

	private static BitMatrixEx buildBitMatrixEx(String message, Map<EncodeHintType, Object> hints, int weight,
			int height) throws WriterException {
		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		int quietZone = 1;
		if (hints != null) {
			if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
				errorCorrectionLevel = ErrorCorrectionLevel
						.valueOf(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
			}
			if (hints.containsKey(EncodeHintType.MARGIN)) {
				quietZone = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
			}

			if (quietZone > QUIET_ZONE_SIZE) {
				quietZone = QUIET_ZONE_SIZE;
			} else if (quietZone < 0) {
				quietZone = 0;
			}
		}

		QRCode code = Encoder.encode(message, errorCorrectionLevel, hints);
		return renderResult(code, weight, height, quietZone);
	}

	private static BitMatrixEx renderResult(QRCode code, int width, int height, int quietZone) {
		ByteMatrix input = code.getMatrix();
		if (input == null) {
			throw new IllegalStateException();
		}

		// xxx 二维码宽高相等, 即 qrWidth == qrHeight
		int inputWidth = input.getWidth();
		int inputHeight = input.getHeight();
		int qrWidth = inputWidth + (quietZone * 2);
		int qrHeight = inputHeight + (quietZone * 2);

		// 白边过多时, 缩放
		int minSize = Math.min(width, height);
		int scale = calculateScale(qrWidth, minSize);
		if (scale > 0) {
			int padding, tmpValue;
			// 计算边框留白
			padding = (minSize - qrWidth * scale) / QUIET_ZONE_SIZE * quietZone;
			tmpValue = qrWidth * scale + padding;
			if (width == height) {
				width = tmpValue;
				height = tmpValue;
			} else if (width > height) {
				width = width * tmpValue / height;
				height = tmpValue;
			} else {
				height = height * tmpValue / width;
				width = tmpValue;
			}
		}

		int outputWidth = Math.max(width, qrWidth);
		int outputHeight = Math.max(height, qrHeight);

		int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
		int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

		BitMatrix output = new BitMatrix(outputWidth, outputHeight);

		BitMatrixEx res = new BitMatrixEx(output);
		res.setLeftPadding(leftPadding);
		res.setTopPadding(topPadding);
		res.setMultiple(multiple);

		// 获取位置探测图形的size，根据源码分析，有两种size的可能
		// {@link
		// com.google.zxing.qrcode.encoder.MatrixUtil.embedPositionDetectionPatternsAndSeparators}
		int detectCornerSize = input.get(0, 5) == 1 ? 7 : 5;

		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
			// Write the contents of this row of the barcode
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
				if (input.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, multiple, multiple);
				}

				// 设置三个位置探测图形
				if (inputX < detectCornerSize && inputY < detectCornerSize // 左上角
						|| (inputX < detectCornerSize && inputY >= inputHeight - detectCornerSize) // 左下脚
						|| (inputX >= inputWidth - detectCornerSize && inputY < detectCornerSize)) { // 右上角
					res.setRegion(outputX, outputY, multiple, multiple);
				}
			}
		}

		return res;
	}

	private static int calculateScale(int qrCodeSize, int expectSize) {
		if (qrCodeSize >= expectSize) {
			return 0;
		}

		int scale = expectSize / qrCodeSize;
		int abs = expectSize - scale * qrCodeSize;
		if (abs < expectSize * 0.15) {
			return 0;
		}

		return scale;
	}

	private static final int QUIET_ZONE_SIZE = 4;

	/**
	 * 加载图片
	 * 
	 * @param url
	 * @return
	 */
	public static BufferedImage loadRemoteImage(String imageUrl) {
		DataInputStream dataInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			URL url = new URL(imageUrl);
			dataInputStream = new DataInputStream(url.openStream());
			File headFile = FileUtils.tempFile("head.png");
			fileOutputStream = new FileOutputStream(headFile);
			byte[] buffer = new byte[512];
			int length = 0;
			while ((length = dataInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, length);
			}
			return ImageIO.read(headFile);
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeQuietly(dataInputStream);
			IOUtils.closeQuietly(fileOutputStream);
		}
	}
}