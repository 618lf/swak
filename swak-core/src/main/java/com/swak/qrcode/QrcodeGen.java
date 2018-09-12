package com.swak.qrcode;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.swak.codec.Encodes;

/**
 * 二维码生成服务
 * @author lifeng
 */
public class QrcodeGen {
	
	private static final int QUIET_ZONE_SIZE = 4;
	
	/**
	 * 入口
	 * @param content
	 * @return
	 */
    public static Builder of(String content) {
        return new Builder().setMsg(content);
    }
	
	private static String asString(QrcodeOptions qrCodeOptions) throws WriterException, IOException {
		BitMatrixEx bitMatrix = encode(qrCodeOptions);
		BufferedImage bufferedImage = toBufferedImage(qrCodeOptions, bitMatrix);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, qrCodeOptions.getPicType(), outputStream);
        return Encodes.encodeBase64(outputStream.toByteArray());
    }
	
	private static BufferedImage toBufferedImage(QrcodeOptions qrCodeConfig, BitMatrixEx bitMatrix) throws IOException {
        int qrCodeWidth = bitMatrix.getWidth();
        int qrCodeHeight = bitMatrix.getHeight();
        BufferedImage qrCode = ImageUtil.drawQrcode(qrCodeConfig, bitMatrix);


        // 若二维码的实际宽高和预期的宽高不一致, 则缩放
        int realQrCodeWidth = qrCodeConfig.getW();
        int realQrCodeHeight = qrCodeConfig.getH();
        if (qrCodeWidth != realQrCodeWidth || qrCodeHeight != realQrCodeHeight) {
            BufferedImage tmp = new BufferedImage(realQrCodeWidth, realQrCodeHeight, BufferedImage.TYPE_INT_RGB);
            tmp.getGraphics().drawImage(
                    qrCode.getScaledInstance(realQrCodeWidth, realQrCodeHeight,
                            Image.SCALE_SMOOTH), 0, 0, null);
            qrCode = tmp;
        }


        // 绘制背景图
        if (qrCodeConfig.getBackground() != null) {
            qrCode = ImageUtil.drawBackground(qrCode,
                    qrCodeConfig.getBackground(),
                    qrCodeConfig.getBgW(),
                    qrCodeConfig.getBgH());
        }


        // 插入logo
        if (qrCodeConfig.getLogo() != null) {
            ImageUtil.insertLogo(qrCode,
                    qrCodeConfig.getLogo(),
                    qrCodeConfig.getLogoStyle(),
                    qrCodeConfig.getLogoBgColor());
        }

        return qrCode;
    }
    
	private static BitMatrixEx encode(QrcodeOptions qrcodeOptions) throws WriterException {
		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		int quietZone = 1;
		if (qrcodeOptions.getHints() != null) {
            if (qrcodeOptions.getHints().containsKey(EncodeHintType.ERROR_CORRECTION)) {
                errorCorrectionLevel = ErrorCorrectionLevel.valueOf(qrcodeOptions.getHints().get(EncodeHintType.ERROR_CORRECTION).toString());
            }
            if (qrcodeOptions.getHints().containsKey(EncodeHintType.MARGIN)) {
                quietZone = Integer.parseInt(qrcodeOptions.getHints().get(EncodeHintType.MARGIN).toString());
            }

            if (quietZone > QUIET_ZONE_SIZE) {
                quietZone = QUIET_ZONE_SIZE;
            } else if (quietZone < 0) {
                quietZone = 0;
            }
        }
		
		QRCode code = Encoder.encode(qrcodeOptions.getMsg(), errorCorrectionLevel, qrcodeOptions.getHints());
	    return renderResult(code, qrcodeOptions.getW(), qrcodeOptions.getH(), quietZone);
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
        // {@link com.google.zxing.qrcode.encoder.MatrixUtil.embedPositionDetectionPatternsAndSeparators}
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
    
    /**
     * 
     * @author lifeng
     */
    public static class Builder {
    	
    	/**
         * The message to put into QrCode
         */
        private String msg;

        /**
         * background image
         */
        private BufferedImage background;
        
        /**
         * background image width
         */
        private Integer bgW;


        /**
         * background image height
         */
        private Integer bgH;

        /**
         * qrcode center logo
         */
        private BufferedImage logo;

        /**
         * logo的样式
         */
        private QrcodeOptions.LogoStyle logoStyle = QrcodeOptions.LogoStyle.NORMAL;

        /**
         * logo的边框背景色
         */
        private Color logoBgColor = Color.WHITE;

        /**
         * qrcode image width
         */
        private Integer w;

        /**
         * qrcode image height
         */
        private Integer h;

        /**
         * qrcode message's code, default UTF-8
         */
        private String code = "utf-8";

        /**
         * 0 - 4
         */
        private Integer padding;

        /**
         * error level, default H
         */
        private ErrorCorrectionLevel errorCorrection = ErrorCorrectionLevel.H;

        /**
         * output qrcode image type, default png
         */
        private String picType = "png";

        /**
         * {@link QrCodeOptions.DrawStyle#name}
         * draw qrcode msg info style
         * 绘制二维码信息的样式
         */
        private String drawStyle;

        /**
         * draw qrcode msg info img
         * 代表二维码信息的图片
         */
        private String drawImg;
        
        private QrcodeOptions build() {

        	QrcodeOptions qrCodeConfig = new QrcodeOptions();
            qrCodeConfig.setMsg(this.msg);
            qrCodeConfig.setH(this.getH());
            qrCodeConfig.setW(this.getW());


            // 设置背景图信息
            qrCodeConfig.setBackground(this.background);
            qrCodeConfig.setBgW(this.getBgW());
            qrCodeConfig.setBgH(this.getBgH());

            qrCodeConfig.setLogo(logo);
            qrCodeConfig.setLogoStyle(logoStyle);
            qrCodeConfig.setLogoBgColor(logoBgColor);
            qrCodeConfig.setPicType(picType);

            Map<EncodeHintType, Object> hints = new HashMap<>(3);
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrection);
            hints.put(EncodeHintType.CHARACTER_SET, code);
            hints.put(EncodeHintType.MARGIN, this.getPadding());
            qrCodeConfig.setHints(hints);

            // 设置绘制二维码信息的style
            qrCodeConfig.setDrawStyle(QrcodeOptions.DrawStyle.getDrawStyle(drawStyle));
            qrCodeConfig.setDrawImg(drawImg);
            return qrCodeConfig;
        }
        
        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setBackground(BufferedImage background) {
            this.background = background;
            return this;
        }

        public Builder setLogo(BufferedImage logo) {
            this.logo = logo;
            return this;
        }

        public Builder setLogoStyle(QrcodeOptions.LogoStyle logoStyle) {
            this.logoStyle = logoStyle;
            return this;
        }

        public Builder setLogoBgColor(int color) {
            this.logoBgColor = ColorUtil.int2color(color);
            return this;
        }

        public Builder setW(Integer w) {
            this.w = w;
            return this;
        }

        public Builder setH(Integer h) {
            this.h = h;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setPadding(Integer padding) {
            this.padding = padding;
            return this;
        }

        public Builder setPicType(String picType) {
            this.picType = picType;
            return this;
        }

        public Builder setErrorCorrection(ErrorCorrectionLevel errorCorrection) {
            this.errorCorrection = errorCorrection;
            return this;
        }

        public Builder setDrawStyle(String drawStyle) {
            this.drawStyle = drawStyle;
            return this;
        }

        public Builder setDrawImg(String drawImg) {
            this.drawImg = drawImg;
            return this;
        }
        
		public void setBgW(Integer bgW) {
			this.bgW = bgW;
		}

		public void setBgH(Integer bgH) {
			this.bgH = bgH;
		}

		// 提供的初始方法
        public Integer getW() {
            return w == null ? (h == null ? 200 : h) : w;
        }
        public Integer getH() {
            return h == null ? (w == null ? 200 : w) : h;
        }
        public Integer getBgW() {
        	return bgW == null ? getW() : bgW;
		}
        public Integer getBgH() {
        	return bgH == null ? getH() : bgH;
		}
        public Integer getPadding() {
            if (padding == null) {
                return 1;
            }

            if (padding < 0) {
                return 0;
            }

            if (padding > 4) {
                return 4;
            }

            return padding;
        }
        
        /**
         * 生成图片
         * @return
         * @throws IOException
         * @throws WriterException
         */
        public String asString(){
            try {
				return QrcodeGen.asString(build());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
        }
    }
}
