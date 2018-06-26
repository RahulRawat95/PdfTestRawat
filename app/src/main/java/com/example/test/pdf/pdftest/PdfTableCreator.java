package com.example.test.pdf.pdftest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.itextpdf.text.BaseColor.BLACK;

public class PdfTableCreator {

    public interface PdfProcessCompleted {
        void pdfCreatedSuccessfully(boolean completed);
    }

    public static void createPdf(final String title, final Date startDate, final Date endDate, final String[][] data, final String fileName, final Context context, final boolean shouldShare, final boolean shouldShowWatermark, final PdfProcessCompleted pdfProcessCompleted) {
        new AsyncTask<Void, Void, Void>() {
            boolean pdfCreatedSuccessfully = false;
            int[] maxLength;

            @Override
            protected Void doInBackground(Void... voids) {
                int maxCol = 0;
                for (int i = 0; i < data.length; i++) {
                    if (maxCol < data[i].length)
                        maxCol = data[i].length;
                }
                maxLength = new int[maxCol];
                for (int i = 0; i < maxCol; i++) {
                    maxLength[i] = 0;
                }
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < data[i].length; j++) {
                        if (maxLength[j] < data[i][j].length()) {
                            maxLength[j] = data[i][j].length();
                        }
                    }
                }

                float[] columnWeight = new float[maxCol];
                for (int i = 0; i < maxCol; i++) {
                    columnWeight[i] = 1F;
                }

                String filePath = Environment.getExternalStorageDirectory() + File.separator + (shouldShare ? "RecoShare" : "RecoDownload");
                File file = new File(filePath);
                if (!file.exists())
                    file.mkdir();
                filePath = filePath + File.separator + fileName + ".pdf";
                try {
                    File myFile = new File(filePath);
                    myFile.createNewFile();

                    //reading from the file
                    OutputStream outputStream = new FileOutputStream(myFile);

                    //creating new document
                    Document document = new Document(PageSize.A4);

                    //Creating pdfwriter instance to write in the document
                    PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                    writer.setPageEvent(new MyFooter(shouldShowWatermark));

                    //opening the document
                    document.open();

                    document.add(addNameAndAddress(title, startDate, endDate));

                    document.add(addMainContent(columnWeight, data));

                    //closing document
                    document.close();

                    if (myFile.exists()) {
                        if (shouldShare) {
                            Intent intentShareFile = new Intent(Intent.ACTION_VIEW);
                            intentShareFile.setDataAndType(Uri.fromFile(myFile), "application/pdf");
                            context.startActivity(intentShareFile);
                        } else {
                            Toast.makeText(context, "File Has Been downloaded to RecoDownload folder in your Internal Storage", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pdfCreatedSuccessfully = true;
                } catch (Exception e) {
                    pdfCreatedSuccessfully = false;
                    Log.d("deadpool", "doInBackground: ", e);
                } catch (Error e) {
                    pdfCreatedSuccessfully = false;
                    Log.d("deadpool", "doInBackground: ", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                pdfProcessCompleted.pdfCreatedSuccessfully(pdfCreatedSuccessfully);
            }
        }.execute();
    }

    private static PdfPTable addMainContent(float[] columnWeight, String data[][]) {
        PdfPTable pdfPTable = new PdfPTable(columnWeight);

        pdfPTable.setWidthPercentage(100);

        pdfPTable.setSpacingAfter(3f);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                pdfPTable.addCell(addCellContent(data[i][j], i == 0));
            }
        }

        return pdfPTable;
    }

    private static void addWatermark(PdfWriter writer) throws Exception {
        PdfContentByte canvas = writer.getDirectContentUnder();
        Image image = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "Lp.jpg");
        //image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        float x = (PageSize.A4.getWidth() - image.getScaledWidth()) / 2;
        float y = (PageSize.A4.getHeight() - image.getScaledHeight()) / 2;
        image.setAbsolutePosition(x, y);
        canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0.2f);
        canvas.setGState(state);
        canvas.addImage(image);
        canvas.restoreState();
    }

    private static PdfPTable addNameAndAddress(String title, Date startDate, Date endDate) {

        //Creating table with three column with there respective weight
        PdfPTable table = new PdfPTable(new float[]{0.15f, 0.85f});
        //giving 100% width to the table
        table.setWidthPercentage(100);

        //defining first cell
        PdfPCell pdfPCell = new PdfPCell();
        disableBoundary(pdfPCell);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseDescender(true);
        pdfPCell.setUseAscender(true);
        pdfPCell.setPaddingTop(3f);

        //Creating image variable for the logo
        Image image = null;
        try {
            //getting image
            //image = Image.getInstance(Constants.getTbl_m_company().getCOMPANY_LOGO_PATH().getPath());
            image = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "Lp.jpg");
            image.setAlignment(Element.ALIGN_LEFT);
            //defining width for the image
            image.setWidthPercentage(80f);

            //image added
            pdfPCell.addElement(image);
            pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setPaddingLeft(15f);
            table.addCell(pdfPCell);
        } catch (Exception ex) {
            pdfPCell.addElement(new Paragraph(""));
            table.addCell(pdfPCell);
        }


        pdfPCell = new PdfPCell();
        disableBoundary(pdfPCell);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);

        //pdfPCell.addElement(addAboutCompany(Constants.getTbl_m_company().getCOMPANY_NAME_VC(), true, false));
        pdfPCell.addElement(addAboutCompany("Company Name Comes Here", true, false));
        try {
            //pdfPCell.addElement(addAboutCompany(tbl_m_invoice_settings_header.get(0).getTITLE(), false, true));
            pdfPCell.addElement(addAboutCompany("Header 1 Comes Here", false, true));
            //pdfPCell.addElement(addAboutCompany(tbl_m_invoice_settings_header.get(1).getTITLE(), false, true));
            pdfPCell.addElement(addAboutCompany("Header 2 Comes Here", false, true));
        } catch (Exception e) {

        }

        //pdfPCell.addElement(addAboutCompany(Constants.getTbl_m_company().getADDRESS_VC(), false, false));
        pdfPCell.addElement(addAboutCompany("Address Comes Here", false, false));
        //pdfPCell.addElement(addAboutCompany(Constants.getTbl_m_company().getPHONE_NO_VC(), false, false));
        pdfPCell.addElement(addAboutCompany("Phone Number Comes Here", false, false));
        //pdfPCell.addElement(addAboutCompany(Constants.getTbl_m_company().getEMAIL_ID_VC(), false, false));
        pdfPCell.addElement(addAboutCompany("Email Id Here", false, false));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fromToDate = simpleDateFormat.format(startDate) + " to " + simpleDateFormat.format(endDate);

        pdfPCell.addElement(addAboutCompany(title, false, false));
        pdfPCell.addElement(addAboutCompany(fromToDate, false, false));

        pdfPCell.setPaddingRight(60f);
        table.addCell(pdfPCell);
        return table;
    }

    private static PdfPTable addAboutCompany(String content, boolean isCompanyName, boolean isHeader) {
        // passed boolean variable is to find out which is company name to give it different Font and which is header to give it different padding
        PdfPTable pdfPTable = new PdfPTable(1);
        pdfPTable.setWidthPercentage(100f);
        PdfPCell pdfPCell = new PdfPCell();
        disableBoundary(pdfPCell);
        pdfPCell.setPadding(0);
        if (!isHeader) {
            pdfPCell.setPaddingTop(3f);
            pdfPCell.setPaddingBottom(3f);
        } else {
            pdfPCell.setPaddingBottom(3f);
        }
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        Paragraph paragraph;
        //defining fonts for Name
        Font BLACK_BOLD = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, BLACK);
        //defining fonts for address
        Font BLACK_NORM = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BLACK);

        //checking if the content is company name then applying BLACK_BOLD font to it
        if (isCompanyName) {
            paragraph = new Paragraph(content, BLACK_BOLD);
            paragraph.setLeading(16f);
        } else {
            paragraph = new Paragraph(content, BLACK_NORM);
            paragraph.setLeading(9.5f);
        }
        paragraph.setAlignment(Element.ALIGN_CENTER);
        pdfPCell.addElement(paragraph);
        pdfPTable.addCell(pdfPCell);
        return pdfPTable;
    }

    private static void disableBoundary(PdfPCell pdfPCell) {
        pdfPCell.disableBorderSide(Rectangle.LEFT);
        pdfPCell.disableBorderSide(Rectangle.TOP);
        pdfPCell.disableBorderSide(Rectangle.RIGHT);
        pdfPCell.disableBorderSide(Rectangle.BOTTOM);
    }

    private static PdfPCell addCellContent(String content, boolean isHeading) {
        Font TAGS;
        if (isHeading) {
            TAGS = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BLACK);
        } else {
            TAGS = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BLACK);
        }

        PdfPCell pdfPCell = new PdfPCell();
        disableBoundary(pdfPCell);
        pdfPCell.setPadding(0);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        pdfPCell.setBorderWidthBottom(0.5f);
        pdfPCell.setBorderColorBottom(BaseColor.LIGHT_GRAY);

        if (isHeading)
            pdfPCell.setPaddingRight(5f);

        //creating paragraph
        Paragraph paragraph = new Paragraph(content, TAGS);
        pdfPCell.setPaddingLeft(3f);
        if (isHeading) {
            paragraph.setAlignment(Element.ALIGN_CENTER);
        } else {
            paragraph.setAlignment(Element.ALIGN_LEFT);
        }

        if (isHeading) {
            pdfPCell.setPaddingTop(6f);
            pdfPCell.setPaddingBottom(6f);
        } else {
            pdfPCell.setPaddingTop(5f);
            pdfPCell.setPaddingBottom(5f);
        }
        pdfPCell.addElement(paragraph);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        return pdfPCell;
    }

    static class MyFooter extends PdfPageEventHelper {
        int pagenumber;
        boolean shouldShowWatermark;

        public MyFooter(boolean shouldShowWatermark) {
            this.shouldShowWatermark = shouldShowWatermark;
        }

        @Override
        public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
            pagenumber = 1;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            pagenumber++;
            if (shouldShowWatermark)
                try {
                    addWatermark(writer);
                } catch (Exception e) {
                    Log.d("deadpool", "onStartPage: ", e);
                }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            float x = (PageSize.A4.getWidth()) / 2;
            float y = 10;
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_CENTER, new Phrase(String.format("page %d", pagenumber)),
                    x, y, 0);
        }
    }
}
