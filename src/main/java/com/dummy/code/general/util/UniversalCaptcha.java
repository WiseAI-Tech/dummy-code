package com.dummy.code.general.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

import javax.imageio.ImageIO;

import java.util.Base64;

public class UniversalCaptcha {

	private boolean isProcessed = false;

	private int fontSize = 50;
	private int codeLength = 5;
	private int noise = 5;
	private String saltText = "";
	private String saltPassword = "";
	private String generatedCaptcha = "";

	public static boolean isCaptchaMatch(String inputText, String text, String password, int codeLength)
			throws Exception {
		return inputText.equals(getProcessedCaptchaCode(text, password, codeLength));
	}

	public void setCaptchaData(String text, String password, int codeLength, int fontSize, int noise) throws Exception {
		isProcessed = false;

		if (codeLength <= 0) {
			throw new Exception("Code Length Too Short. Expected 0 < codeLength < 10");
		} else if (codeLength > 10) {
			throw new Exception("Code Length Too Large. Expected 0 < codeLength < 10");
		}

		if (fontSize < 10) {
			throw new Exception("Font Size Too Small. Expected 10 <= fontSize <= 100");
		} else if (codeLength > 100) {
			throw new Exception("Font Size Too Large. Expected 0 <= fontSize <= 100");
		}

		if (text == null || text.isEmpty()) {
			throw new Exception("Expected text but NULL or EMPTY detected.");
		}

		if (password == null || password.isEmpty()) {
			throw new Exception("Expected password but NULL or EMPTY detected.");
		}

		if (noise < 0) {
			throw new Exception("Noise cannot be less than 0.");
		}

		this.saltText = text;
		this.saltPassword = password;
		this.codeLength = codeLength;
		this.fontSize = fontSize;
		this.noise = noise;
	}

	public void processCaptcha() throws Exception {
		generatedCaptcha = getProcessedCaptchaCode(saltText, saltPassword, codeLength);

		isProcessed = true;
	}

	private static String getProcessedCaptchaCode(String text, String password, int codeLength) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hash.length; i++) {
			sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
		}

		String firstHash = sb.toString() + password;

		byte[] hash2 = digest.digest(firstHash.getBytes(StandardCharsets.UTF_8));

		StringBuilder sb2 = new StringBuilder();
		for (int i = 0; i < hash2.length; i++) {
			sb2.append(Integer.toString((hash2[i] & 0xff) + 0x100, 16).substring(1));
		}

		String finalHash = sb2.toString().toUpperCase();
		String generatedCaptcha = "";
		for (int x = 0; x < codeLength; x++) {
			int charSum = finalHash.charAt(x) + finalHash.charAt(x + 1);

			while (charSum >= 91) {
				charSum = charSum % 91;
				if (charSum < 49) {
					charSum += 49;
				}
			}

			switch (charSum) {
			case 58:
				charSum = 65;
				break;
			case 59:
				charSum = 66;
				break;
			case 60:
				charSum = 67;
				break;
			case 61:
				charSum = 68;
				break;
			case 62:
				charSum = 69;
				break;
			case 63:
				charSum = 70;
				break;
			case 64:
				charSum = 71;
				break;
			case 73:
				charSum = 74;
				break;
			case 76:
				charSum = 77;
				break;
			case 79:
				charSum = 80;
				break;
			default:
				break;
			}

			generatedCaptcha += (char) charSum;
		}

		return generatedCaptcha;
	}

	public String getCaptchaCode() throws Exception {
		if (!isProcessed) {
			throw new Exception("Captcha Not Processed!");
		}
		return generatedCaptcha;
	}

	public BufferedImage getCaptchaImage() throws Exception {
		if (!isProcessed) {
			throw new Exception("Captcha Not Processed!");
		}

		Font font = new Font("Arial", Font.BOLD, fontSize);

		String captchaText = getSpacedCaptcha();

		int canvasWidth = getCanvasWidth(captchaText, font);
		int canvasHeight = getCanvasHeight(captchaText, font);

		BufferedImage bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, canvasWidth, canvasHeight);

		g2d.setFont(font);
		double initialRot = Math.toRadians(new Random().nextInt(6) - 3);
		g2d.rotate(initialRot);
		String[] splitCaptcha = captchaText.split("\\s+");
		int startWidthIndex = getCenterWidthPoint(g2d, captchaText, canvasWidth, font);
		int heightIndex = getCenterHeightPoint(g2d, canvasHeight, font);
		for (int x = 0; x < splitCaptcha.length; x++) {
			String captchaLetter = splitCaptcha[x];
			if (x != splitCaptcha.length - 1) {
				captchaLetter += " ";
			}
			double letterRot = Math.toRadians(new Random().nextInt(6) - 3);
			g2d.rotate(letterRot);
			g2d.setColor(generateRandomDarkColour());
			g2d.drawString(captchaLetter, startWidthIndex, heightIndex);
			g2d.rotate(-letterRot);

			startWidthIndex += getTextWidth(g2d, captchaLetter, font);
		}

		g2d.rotate(Math.toRadians(-initialRot));
		generateRandomNoise(g2d, canvasWidth, canvasHeight, noise);

		g2d.dispose();

		return bufferedImage;
	}

	public String getCaptchaBase64PNG() throws Exception {
		if (!isProcessed) {
			throw new Exception("Captcha Not Processed!");
		}

		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ImageIO.write(getCaptchaImage(), "png", bos);
		byte[] imageBytes = bos.toByteArray();

		imageString = Base64.getEncoder().encodeToString(imageBytes);

		bos.close();

		return imageString;
	}

	private int getCanvasWidth(String text, Font font) {
		BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
		Graphics g2d = bufferedImage.createGraphics();
		FontMetrics metrics = g2d.getFontMetrics(font);
		return metrics.stringWidth(text) + 25 * 2;
	}

	private int getCanvasHeight(String text, Font font) {
		BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
		Graphics g2d = bufferedImage.createGraphics();
		FontMetrics metrics = g2d.getFontMetrics(font);
		g2d.dispose();
		return metrics.getHeight() + 10 * 2;
	}

	private int getTextWidth(Graphics2D g2d, String text, Font font) {
		FontMetrics metrics = g2d.getFontMetrics(font);
		return metrics.stringWidth(text);
	}

	private int getCenterWidthPoint(Graphics2D g2d, String text, int canvasWidth, Font font) {
		FontMetrics metrics = g2d.getFontMetrics(font);
		return (canvasWidth - metrics.stringWidth(text)) / 2;
	}

	private int getCenterHeightPoint(Graphics2D g2d, int canvasHeight, Font font) {
		FontMetrics metrics = g2d.getFontMetrics(font);
		return ((canvasHeight - metrics.getHeight()) / 2) + metrics.getAscent();
	}

	private Color generateRandomDarkColour() {
		return Color.getHSBColor(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()).darker();
	}

	private void generateRandomNoise(Graphics2D g2d, int canvasWidth, int canvasHeight, int noiseCount) {
		if (noise == 0) {
			return;
		}

		// Generate Main Noise
		g2d.setColor(generateRandomDarkColour());
		g2d.setStroke(new BasicStroke(new Random().nextInt(2) + 1));
		g2d.drawLine(new Random().nextInt((canvasWidth * 20 / 100)), new Random().nextInt((canvasHeight * 20 / 100)),
				new Random().nextInt((canvasWidth * 20 / 100)) + (canvasWidth * 80 / 100),
				new Random().nextInt((canvasHeight * 20 / 100)) + (canvasHeight * 80 / 100));

		// Generate Subsequent Noise
		for (int x = 0; x < noiseCount - 1; x++) {
			g2d.setColor(generateRandomDarkColour());
			g2d.setStroke(new BasicStroke(new Random().nextInt(2) + 1));
			g2d.drawLine(new Random().nextInt(canvasWidth), new Random().nextInt(canvasHeight),
					new Random().nextInt(canvasWidth), new Random().nextInt(canvasHeight));
		}
	}

	private String getSpacedCaptcha() {
		String spacedCaptcha = "";
		for (int x = 0; x < generatedCaptcha.length(); x++) {
			spacedCaptcha += generatedCaptcha.charAt(x);
			if (x != generatedCaptcha.length() - 1) {
				spacedCaptcha += " ";
			}
		}

		return spacedCaptcha;
	}
}
