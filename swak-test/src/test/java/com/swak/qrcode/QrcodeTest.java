package com.swak.qrcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * 测试二维码生成
 * 
 * @author lifeng
 * @date 2020年12月8日 上午10:30:15
 */
public class QrcodeTest {

	public static void main(String[] args) throws IOException {
		InputStream input = new FileInputStream("E:\\测试生成图片\\微信图片_20201208113257.jpg");
		BufferedImage bg = ImageIO.read(input);
		File file = QrcodeGen.of("23").setBackground(bg).setBgW(1080).setBgH(1913)
				.setX(800).setY(1600).setOutFile("E:\\测试生成图片\\测试.png")
				.asFile();
		System.out.println(file.getAbsolutePath());
	}
}
