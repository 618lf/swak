package com.swak.qrcode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.swak.codec.Encodes;
import com.swak.utils.FileUtils;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 创建图片
 * 
 * @author lifeng
 * @date 2021年1月5日 下午3:22:02
 */
@Getter
@Setter
@Accessors(chain = true)
public class ImageGen {

	/**
	 * 创建为base64
	 * 
	 * @param qrCodeOptions
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	private static String asString(Builder builder) throws WriterException, IOException {
		return Encodes.encodeBase64(asBytes(builder));
	}

	/**
	 * 写入文件
	 * 
	 * @param qrCodeOptions
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	private static File asFile(Builder builder) throws WriterException, IOException {
		BufferedImage bufferedImage = as(builder);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, builder.getPicType(), outputStream);
		File dest = builder.getOutFile();
		if (dest == null) {
			dest = FileUtils.tempFile(builder.getPicType());
		} else {
			dest = FileUtils.newFile(builder.getOutFile().getAbsolutePath());
		}
		FileUtils.write(dest, outputStream.toByteArray());
		return dest;
	}

	/**
	 * 字节流
	 * 
	 * @param qrCodeOptions
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	private static byte[] asBytes(Builder builder) throws WriterException, IOException {
		BufferedImage bufferedImage = as(builder);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, builder.getPicType(), outputStream);
		return outputStream.toByteArray();
	}

	/**
	 * 创建图片
	 * 
	 * @param builder
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	private static BufferedImage as(Builder builder) throws WriterException, IOException {

		// 最终生成的图片
		BufferedImage genImage = null;

		// 所有的元素
		List<Image> images = builder.images;

		// 循环的创建元素
		for (Image image : images) {

			// 生成的图片
			genImage = draw(genImage, image);

		}

		// 返回生成的图片
		return genImage;
	}

	/**
	 * 生成图片
	 * 
	 * @param image
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	private static BufferedImage draw(BufferedImage background, Image image) throws WriterException, IOException {
		BufferedImage genImage = null;
		if (image.getUseImage() != null) {
			genImage = ImageUtil.drawImage(image.getUseImage(), image.getWidth(), image.getHeight());
		} else if (StringUtils.isNotBlank(image.getUseUrl())) {
			genImage = ImageUtil.loadRemoteImage(image.getUseUrl());
		} else if (StringUtils.isNotBlank(image.getUseText())) {
			genImage = ImageUtil.drawQrcode(image.getUseText(), image.getHints(), image.getWidth(), image.getHeight());
		}

		if (genImage == null) {
			throw new RuntimeException("Please Set UseImage Or UseUrl Or UseText!");
		}

		if (background != null) {
			genImage = ImageUtil.drawImage(background, genImage, image.getWidth(), image.getHeight(), image.getX(),
					image.getY());
		}
		return genImage;
	}

	/**
	 * 创建构建器
	 * 
	 * @return
	 */
	public static Builder Builder() {
		return new Builder();
	}

	/**
	 * 图片构建器
	 * 
	 * @author lifeng
	 * @date 2021年1月5日 下午3:28:47
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Builder {

		/**
		 * 使用的图片
		 */
		private List<Image> images = Lists.newArrayList();

		/**
		 * 输出类型
		 */
		private String picType = "jpg";

		/**
		 * 输出文件
		 */
		private File outFile;

		/**
		 * 添加图片
		 * 
		 * @param image
		 * @return
		 */
		public Builder addImage(Image image) {
			assert image != null;
			images.add(image);
			return this;
		}

		/**
		 * 生成图片
		 * 
		 * @return 文件
		 */
		public byte[] asBytes() {
			try {
				return ImageGen.asBytes(this);
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * 生成图片
		 * 
		 * @return 文件
		 */
		public String asString() {
			try {
				return ImageGen.asString(this);
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * 生成图片
		 * 
		 * @return 文件
		 */
		public File asFile() {
			try {
				return ImageGen.asFile(this);
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * 构建器
	 * 
	 * @author lifeng
	 * @date 2021年1月5日 下午3:22:47
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Image {

		/**
		 * 使用本地的图片
		 */
		private BufferedImage useImage;

		/**
		 * 使用网络地址
		 */
		private String useUrl;

		/**
		 * 使用文本： 针对二维码
		 */
		private String useText;

		/**
		 * 生成二维码的宽
		 */
		private Integer width;

		/**
		 * 生成二维码的高
		 */
		private Integer height;

		/**
		 * 在背景图片的位置
		 */
		private Integer x;

		/**
		 * 在背景图片的位置
		 */
		private Integer y;

		/**
		 * 绘制二维码的样式
		 */
		private ImageStyle imageStyle;

		/**
		 * Qrcode 才有效
		 */
		private Map<EncodeHintType, Object> hints;

		/**
		 * Qrcode 才有效
		 */
		private Integer padding;

		/**
		 * 使用本地图片
		 * 
		 * @param useImage
		 * @return
		 */
		public static Image useImage(BufferedImage useImage) {
			return new Image().setUseImage(useImage);
		}

		/**
		 * 使用网络图片
		 * 
		 * @param useImage
		 * @return
		 */
		public static Image useUrl(String useUrl) {
			return new Image().setUseUrl(useUrl);
		}

		/**
		 * 使用图片
		 * 
		 * @param useImage
		 * @return
		 */
		public static Image useText(String useText) {
			Image image = new Image();
			Map<EncodeHintType, Object> hints = new HashMap<>(3);
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			hints.put(EncodeHintType.MARGIN, image.getPadding());
			image.setHints(hints);
			return image;
		}
	}
}