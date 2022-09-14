package com.dummy.code.general.util;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;

public class ExportDataListUtil {

	private static final int CHARACTER_WIDTH = 256;
	private static final int HEADER_FONT_SIZE = 11;
	private static final int DEFAULT_FONT_SIZE = 11;

	public static byte[] generateDataFile(String exportType, String[] headerData, String[] mData,
			int[] displayColumnLengthData, JSONArray rowData) throws Exception {
		return generateDataFile(exportType, headerData, mData, displayColumnLengthData, rowData, null, null);
	}

	public static byte[] generateDataFile(String exportType, String[] headerData, String[] mData,
			int[] displayColumnLengthData, JSONArray rowData, Map<String, String> extraData, byte[] logoImageData)
			throws Exception {
		byte[] fileData = null;

		if (exportType.equalsIgnoreCase("excel")) {
			fileData = generateWorkbook(headerData, mData, displayColumnLengthData, rowData, extraData);
		} else if (exportType.equalsIgnoreCase("pdf")) {
			fileData = generatePDF(headerData, mData, displayColumnLengthData, rowData, extraData, logoImageData);
		} else {
			throw new Exception("Unhandled Export Type");
		}

		return fileData;
	}

	private static byte[] generateWorkbook(String[] headerData, String[] mData, int[] displayColumnLengthData,
			JSONArray rowData, Map<String, String> extraData) throws Exception {
		Workbook workbook = new XSSFWorkbook();

		try {
			// Create New Sheet
			Sheet sheet = workbook.createSheet();
			// Set Column Width
			for (int x = 0; x < displayColumnLengthData.length; x++) {
				sheet.setColumnWidth(x, displayColumnLengthData[x]
						* (int) ((double) CHARACTER_WIDTH * ((double) HEADER_FONT_SIZE / (double) DEFAULT_FONT_SIZE)));
			}

			// Create Header Style
			CellStyle headerStyle = workbook.createCellStyle();
			XSSFFont headerFont = ((XSSFWorkbook) workbook).createFont();
			headerFont.setFontName("Arial");
			headerFont.setFontHeightInPoints((short) HEADER_FONT_SIZE);
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);

			// Create Normal Style
			CellStyle dataStyle = workbook.createCellStyle();
			XSSFFont dataFont = ((XSSFWorkbook) workbook).createFont();
			dataFont.setFontName("Arial");
			dataFont.setFontHeightInPoints((short) 12);
			dataStyle.setWrapText(true);

			int rowIndex = 0;

			// Process Extra Data
			if (extraData != null && extraData.size() > 0) {
				Iterator<Entry<String, String>> extraDataIT = extraData.entrySet().iterator();
				while (extraDataIT.hasNext()) {
					Map.Entry<String, String> extraDataPair = (Map.Entry<String, String>) extraDataIT.next();

					Row row = sheet.createRow(rowIndex++);
					Cell cell = row.createCell(0);
					cell.setCellValue(extraDataPair.getKey());
					cell.setCellStyle(headerStyle);

					Cell cell2 = row.createCell(1);
					cell2.setCellValue(extraDataPair.getValue());
					cell2.setCellStyle(dataStyle);
				}
				// Add Extra Empty Space
				rowIndex++;
			}

			// Fill In Header Data
			Row header = sheet.createRow(rowIndex++);
			for (int x = 0; x < headerData.length; x++) {
				Cell headerCell = header.createCell(x);
				headerCell.setCellValue(headerData[x]);
				headerCell.setCellStyle(headerStyle);
			}

			for (int x = 0; x < rowData.length(); x++) {
				if (mData == null) {
					JSONArray singleRowData = rowData.getJSONArray(x);
					Row row = sheet.createRow(rowIndex++);
					for (int y = 0; y < singleRowData.length(); y++) {
						Cell cell = row.createCell(y);
						if (singleRowData.getString(y).isEmpty()) {
							cell.setBlank();
						} else {
							if (singleRowData.getString(y) instanceof String) {
								cell.setCellValue(singleRowData.getString(y));
							} else {
								try {
									if (!singleRowData.getString(y).toString().startsWith("+")) {
										Double doubleCellValue = Double.parseDouble(singleRowData.getString(y));
										cell.setCellValue(doubleCellValue);
									}
								} catch (Exception ex) {
									cell.setCellValue(singleRowData.getString(y));
								}
							}
						}
						cell.setCellStyle(dataStyle);
					}
				} else {
					JSONObject singleRowData = rowData.getJSONObject(x);
					Row row = sheet.createRow(rowIndex++);
					for (int y = 0; y < mData.length; y++) {
						Cell cell = row.createCell(y);
						if (!singleRowData.has(mData[y]) || singleRowData.get(mData[y]) == null
								|| singleRowData.get(mData[y]).toString().isEmpty()) {
							cell.setBlank();
						} else {
							if (singleRowData.get(mData[y]) instanceof String) {
								cell.setCellValue(singleRowData.get(mData[y]).toString());
							} else {
								try {
									if (!singleRowData.get(mData[y]).toString().startsWith("+")) {
										Double doubleCellValue = Double
												.parseDouble(singleRowData.get(mData[y]).toString());
										cell.setCellValue(doubleCellValue);
									}
								} catch (Exception ex) {
									cell.setCellValue(singleRowData.get(mData[y]).toString());
								}
							}
						}
						cell.setCellStyle(dataStyle);
					}
				}
			}
		} catch (Exception ex) {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (Exception ex2) {
			}

			ex.printStackTrace();
			throw ex;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			workbook.write(baos);
		} finally {
			baos.close();
		}

		if (workbook != null) {
			workbook.close();
		}

		return baos.toByteArray();
	}

	private static byte[] generatePDF(String[] headerData, String[] mData, int[] displayColumnLengthData,
			JSONArray rowData, Map<String, String> extraData, byte[] logoImageData) throws Exception {
		Document pdfDoc = new Document();
		pdfDoc.setPageSize(PageSize.A4.rotate());
		pdfDoc.setMargins(20, 20, 20, 20);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, baos);
		pdfDoc.open();

		generatePDFData(pdfDoc, headerData, mData, displayColumnLengthData, rowData, extraData, logoImageData);

		int totalPageNumber = pdfWriter.getPageNumber();
		pdfDoc.close();
		baos.close();
		baos.reset();

		Document pdfDoc2 = new Document();
		pdfDoc2.setPageSize(PageSize.A4.rotate());
		pdfDoc2.setMargins(20, 20, 20, 20);
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		PdfWriter pdfWriter2 = PdfWriter.getInstance(pdfDoc2, baos2);
		pdfWriter2.setPageEvent(new PdfPageEvent() {
			@Override
			public void onOpenDocument(PdfWriter writer, Document document) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartPage(PdfWriter writer, Document document) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEndPage(PdfWriter writer, Document document) {
				// TODO Auto-generated method stub
				PdfPTable footer = new PdfPTable(1);
				try {
					footer.setWidths(new int[] { 1 });
					// Page Size - Margin
					footer.setTotalWidth(writer.getPageSize().getWidth() - 20 - 20);
					footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);

					footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
					footer.addCell(new Phrase(String.format("Page %d of %d", document.getPageNumber(), totalPageNumber),
							new Font(Font.FontFamily.HELVETICA, 8)));

					PdfContentByte canvas = writer.getDirectContent();
					canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
					// YPos
					footer.writeSelectedRows(0, -1, 34, 25, canvas);
					canvas.endMarkedContentSequence();
				} catch (DocumentException de) {
					throw new ExceptionConverter(de);
				}
			}

			@Override
			public void onCloseDocument(PdfWriter writer, Document document) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChapterEnd(PdfWriter writer, Document document, float paragraphPosition) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth,
					Paragraph title) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSectionEnd(PdfWriter writer, Document document, float paragraphPosition) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
				// TODO Auto-generated method stub

			}
		});
		pdfDoc2.open();
		generatePDFData(pdfDoc2, headerData, mData, displayColumnLengthData, rowData, extraData, logoImageData);
		pdfDoc2.close();
		baos2.close();

		return baos2.toByteArray();
	}

	private static void generatePDFData(Document pdfDoc, String[] headerData, String[] mData,
			int[] displayColumnLengthData, JSONArray rowData, Map<String, String> extraData, byte[] logoImageData)
			throws Exception {
		Font regularFont = new Font(FontFamily.HELVETICA, 12);
		Font boldFont = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

		// Process Image Data
		if (logoImageData != null) {
			Image pdfImageData = Image.getInstance(logoImageData);
			float pdfImageHeight = pdfImageData.getHeight();
			float pdfImageWidth = pdfImageData.getWidth();
			if (pdfImageHeight > 100f) {
				pdfImageHeight = 100f;
			}
			if (pdfImageWidth > 100f) {
				pdfImageWidth = 100f;
			}
			pdfImageData.scaleToFit(pdfImageWidth, pdfImageHeight);

			PdfPTable pdfTable = new PdfPTable(1);
			pdfTable.setWidthPercentage(100);
			PdfPCell pdfTableCell = new PdfPCell(pdfImageData);
			pdfTableCell.setBorder(Rectangle.NO_BORDER);
			pdfTableCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			pdfTableCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			pdfTable.addCell(pdfTableCell);

			pdfDoc.add(pdfTable);

			// Add Extra Empty Space
			pdfDoc.add(new Paragraph(new Phrase("\n")));
		}

		// Process Extra Data
		if (extraData != null && extraData.size() > 0) {
			PdfPTable pdfTable = new PdfPTable(2);
			pdfTable.setWidthPercentage(50);
			PdfPCell pdfTableCell;

			Iterator<Entry<String, String>> extraDataIT = extraData.entrySet().iterator();
			while (extraDataIT.hasNext()) {
				Map.Entry<String, String> extraDataPair = (Map.Entry<String, String>) extraDataIT.next();

				pdfTableCell = new PdfPCell(new Phrase(extraDataPair.getKey(), boldFont));
				pdfTable.addCell(pdfTableCell);

				pdfTableCell = new PdfPCell(new Phrase(extraDataPair.getValue(), regularFont));
				pdfTable.addCell(pdfTableCell);
			}
			pdfDoc.add(pdfTable);

			// Add Extra Empty Space
			pdfDoc.add(new Paragraph(new Phrase("\n")));
		}

		PdfPTable pdfTable = new PdfPTable(headerData.length);
		PdfPCell pdfTableCell;

		// Header Data
		for (int x = 0; x < headerData.length; x++) {
			pdfTableCell = new PdfPCell(new Phrase(headerData[x], boldFont));
			pdfTable.addCell(pdfTableCell);
		}

		// Body Data
		for (int x = 0; x < rowData.length(); x++) {
			if (mData == null) {
				JSONArray singleRowData = rowData.getJSONArray(x);
				for (int y = 0; y < singleRowData.length(); y++) {
					if (singleRowData.getString(y).isEmpty()) {
						pdfTableCell = new PdfPCell(new Phrase("", regularFont));
						pdfTable.addCell(pdfTableCell);
					} else {
						pdfTableCell = new PdfPCell(new Phrase(singleRowData.getString(y), regularFont));
						pdfTable.addCell(pdfTableCell);
					}
				}
			} else {
				JSONObject singleRowData = rowData.getJSONObject(x);
				for (int y = 0; y < mData.length; y++) {
					if (!singleRowData.has(mData[y]) || singleRowData.get(mData[y]) == null
							|| singleRowData.get(mData[y]).toString().isEmpty()) {
						pdfTableCell = new PdfPCell(new Phrase("", regularFont));
						pdfTable.addCell(pdfTableCell);
					} else {
						pdfTableCell = new PdfPCell(new Phrase(singleRowData.get(mData[y]).toString(), regularFont));
						pdfTable.addCell(pdfTableCell);
					}
				}
			}
		}

		pdfDoc.add(pdfTable);
	}
}
