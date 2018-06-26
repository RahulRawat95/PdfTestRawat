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
import java.util.List;

import static com.itextpdf.text.BaseColor.BLACK;

public class ProfitAndLossPdfCreator {

    private static final int TOP = 0;
    private static final int BOTTOM = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private static Font BLACK_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BLACK);
    private static Font BLACK_NORMAL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BLACK);

    private List<ProfitAndLossDatabaseModel> openingStocks, inDirectExpenses, closingStocks, inDirectIncomes, directExpense, directIncome;//, expensesDirect, incomesDirect;
    private List<SalePurchaseSum> salePurchaseSums;
    private double openingStockTotalDouble = 0D, closingStockTotalDouble = 0D, indirectExpenseTotalDouble = 0D, indirectIncomeTotalDouble = 0D, directExpenseTotalDouble = 0D, directIncomeTotalDouble = 0D;
    private double gross, net;
    private double inDirectIncomeGroupTotal = 0D, inDirectExpenseGroupTotal = 0D;

    public ProfitAndLossPdfCreator setLists(List<ProfitAndLossDatabaseModel> openingStocks, List<ProfitAndLossDatabaseModel> inDirectExpenses, List<ProfitAndLossDatabaseModel> closingStocks, List<ProfitAndLossDatabaseModel> inDirectIncomes, List<ProfitAndLossDatabaseModel> directExpense, List<ProfitAndLossDatabaseModel> directIncome, List<SalePurchaseSum> salePurchaseSums) {
        this.openingStocks = openingStocks;
        this.inDirectExpenses = inDirectExpenses;
        this.closingStocks = closingStocks;
        this.inDirectIncomes = inDirectIncomes;
        this.directExpense = directExpense;
        this.directIncome = directIncome;
        this.salePurchaseSums = salePurchaseSums;
        return this;
    }

    public ProfitAndLossPdfCreator setSums(double openingStockTotalDouble, double closingStockTotalDouble, double indirectExpenseTotalDouble, double indirectIncomeTotalDouble, double directExpenseTotalDouble, double directIncomeTotalDouble, double inDirectIncomeGroupTotal, double inDirectExpenseGroupTotal) {
        this.openingStockTotalDouble = openingStockTotalDouble;
        this.closingStockTotalDouble = closingStockTotalDouble;
        this.indirectExpenseTotalDouble = indirectExpenseTotalDouble;
        this.indirectIncomeTotalDouble = indirectIncomeTotalDouble;
        this.directExpenseTotalDouble = directExpenseTotalDouble;
        this.directIncomeTotalDouble = directIncomeTotalDouble;
        this.inDirectIncomeGroupTotal = inDirectIncomeGroupTotal;
        this.inDirectExpenseGroupTotal = inDirectExpenseGroupTotal;
        return this;
    }

    public ProfitAndLossPdfCreator setGrossAndNet(double gross, double net) {
        this.gross = gross;
        this.net = net;
        return this;
    }

    public interface PdfProcessCompleted {
        void pdfCreatedSuccessfully(boolean completed);
    }

    public void createPdf(final Date startDate, final Date endDate, final String fileName, final Context context
            , final boolean shouldShare, final boolean shouldShowWatermark, final PdfProcessCompleted pdfProcessCompleted) {
        new AsyncTask<Void, Void, Void>() {
            boolean pdfCreatedSuccessfully = false;

            @Override
            protected Void doInBackground(Void... voids) {

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
                    writer.setPageEvent(new PdfTableCreator.MyFooter(shouldShowWatermark));

                    //opening the document
                    document.open();

                    document.add(addNameAndAddress("Profit and Loss", startDate, endDate));

                    document.add(addTopRowParticular(new float[]{0.5f, 0.5f}, startDate, endDate));

                    document.add(addMainContent(new float[]{0.5f, 0.5f}));

                    document.add(addEmptyCellsForPadding(new float[]{0.5f, 0.5f}));

                    document.add(addGrossLines(new float[]{0.5f, 0.5f}, gross > 0D));

                    document.add(addIndirectContent(new float[]{0.5f, 0.5f}));

                    document.add(addNetLine(new float[]{0.5f, 0.5f}, net > 0));

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

    private PdfPTable addEmptyCellsForPadding(float[] columnWeight) {
        PdfPTable pdfPTable = new PdfPTable(columnWeight);

        pdfPTable.setWidthPercentage(100);

        PdfPCell pdfPCell = null;

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, RIGHT, LEFT);

        pdfPTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, RIGHT);

        pdfPTable.addCell(pdfPCell);

        return pdfPTable;
    }

    private PdfPTable addNetLine(float[] columnWeight, boolean isProfit) {
        PdfPTable pdfPTable = new PdfPTable(columnWeight);

        pdfPTable.setWidthPercentage(100);

        PdfPCell pdfPCell = null;

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, RIGHT, BOTTOM, LEFT);
        pdfPCell.addElement(addPLTable(true, isProfit ? "Net Profit" : "  ", isProfit ? String.format("%.2f", net) : "  "));

        pdfPTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, BOTTOM, RIGHT);
        pdfPCell.addElement(addPLTable(true, !isProfit ? "Net Loss" : "  ", !isProfit ? String.format("%.2f", net) : "  "));

        pdfPTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, RIGHT, LEFT, BOTTOM);
        pdfPCell.addElement(addPLTable(true, "Total", String.format("%.2f", inDirectExpenseGroupTotal)));

        pdfPTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, RIGHT, BOTTOM);
        pdfPCell.addElement(addPLTable(true, "Total", String.format("%.2f", inDirectIncomeGroupTotal)));

        pdfPTable.addCell(pdfPCell);

        return pdfPTable;
    }

    private PdfPTable addGrossLines(float[] columnWeight, boolean isProfit) {
        PdfPTable pdfPTable = new PdfPTable(columnWeight);

        pdfPTable.setWidthPercentage(100);

        PdfPCell pdfPCell = null;

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, RIGHT, LEFT);
        pdfPCell.addElement(addPLTable(true, isProfit ? "Gross Profit c/o" : "  ", isProfit ? String.format("%.2f", gross) : "  "));
        pdfPCell.addElement(addPLTable(true, "  ", String.format("%.2f", gross)));
        pdfPCell.addElement(addPLTable(true, !isProfit ? "Gross Loss b/f" : "  ", !isProfit ? String.format("%.2f", gross) : "  "));

        pdfPTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        enableBoundary(pdfPCell, RIGHT);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        pdfPCell.addElement(addPLTable(true, !isProfit ? "Gross Loss c/o" : "  ", !isProfit ? String.format("%.2f", gross) : "  "));
        pdfPCell.addElement(addPLTable(true, "  ", String.format("%.2f", gross)));
        pdfPCell.addElement(addPLTable(true, isProfit ? "Gross Profit b/f" : "  ", isProfit ? String.format("%.2f", gross) : "  "));

        pdfPTable.addCell(pdfPCell);

        return pdfPTable;
    }

    private PdfPTable addIndirectContent(float[] columnWeight) {
        PdfPTable pdfPTable = new PdfPTable(columnWeight);

        pdfPTable.setWidthPercentage(100);

        PdfPCell pdfPCell = null;

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, RIGHT, LEFT);
        pdfPCell.addElement(addPLTable(true, "Indirect Expense", String.format("%.2f", indirectExpenseTotalDouble)));
        for (int i = 0; i < inDirectExpenses.size(); i++) {
            pdfPCell.addElement(addPLTable(false, inDirectExpenses.get(i).getParticular(), String.format("%.2f", inDirectExpenses.get(i).getEXPENSE())));
        }

        pdfPTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        enableBoundary(pdfPCell, RIGHT);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        pdfPCell.addElement(addPLTable(true, "Indirect Incomes", String.format("%.2f", indirectIncomeTotalDouble)));
        for (int i = 0; i < inDirectIncomes.size(); i++) {
            pdfPCell.addElement(addPLTable(false, inDirectIncomes.get(i).getParticular(), String.format("%.2f", inDirectIncomes.get(i).getEXPENSE())));
        }

        pdfPTable.addCell(pdfPCell);

        return pdfPTable;
    }

    private PdfPTable addMainContent(float[] columnWeight) {
        PdfPTable pdfPTable = new PdfPTable(columnWeight);

        pdfPTable.setWidthPercentage(100);

        PdfPCell pdfPCell = null;

        pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        enableBoundary(pdfPCell, RIGHT, LEFT);

        pdfPCell.addElement(addPLTable(true, "Opening Stock", String.format("%.2f", openingStockTotalDouble)));
        for (int i = 0; i < openingStocks.size(); i++) {
            pdfPCell.addElement(addPLTable(false, openingStocks.get(i).getParticular(), String.format("%.2f", openingStocks.get(i).getEXPENSE())));
        }

        pdfPCell.addElement(addPLTable(true, "Purchase Account", String.format("%.2f", salePurchaseSums.get(0).getPURCHASE())));
        pdfPCell.addElement(addPLTable(false, "Purchase", String.format("%.2f", salePurchaseSums.get(0).getPURCHASE())));

        pdfPCell.addElement(addPLTable(true, "Direct Expense", String.format("%.2f", directExpenseTotalDouble)));
        for (int i = 0; i < directExpense.size(); i++) {
            pdfPCell.addElement(addPLTable(false, directExpense.get(i).getParticular(), String.format("%.2f", directExpense.get(i).getEXPENSE())));
        }

        pdfPTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        enableBoundary(pdfPCell, RIGHT);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);

        pdfPCell.addElement(addPLTable(true, "Sales Accounts", String.format("%.2f", salePurchaseSums.get(0).getSALES())));
        pdfPCell.addElement(addPLTable(false, "Sales", String.format("%.2f", salePurchaseSums.get(0).getSALES())));

        pdfPCell.addElement(addPLTable(true, "Closing Stock", String.format("%.2f", closingStockTotalDouble)));
        for (int i = 0; i < closingStocks.size(); i++) {
            pdfPCell.addElement(addPLTable(false, closingStocks.get(i).getParticular(), String.format("%.2f", closingStocks.get(i).getEXPENSE())));
        }

        pdfPCell.addElement(addPLTable(true, "Direct Incomes", String.format("%.2f", directIncomeTotalDouble)));
        for (int i = 0; i < directIncome.size(); i++) {
            pdfPCell.addElement(addPLTable(false, directIncome.get(i).getParticular(), String.format("%.2f", directIncome.get(i).getEXPENSE())));
        }

        pdfPTable.addCell(pdfPCell);

        return pdfPTable;
    }

    private PdfPTable addPLTable(boolean isGroupName, String leftData, String rightData) {
        PdfPTable pdfPTable = new PdfPTable(new float[]{0.5f, 0.25f, 0.25f});

        PdfPCell cell = new PdfPCell();
        disableBoundary(cell);
        cell.setPadding(0f);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setPaddingTop(3f);

        if (!isGroupName)
            cell.setPaddingLeft(5f);

        cell.addElement(new Paragraph(leftData, isGroupName ? BLACK_BOLD : BLACK_NORMAL));

        pdfPTable.addCell(cell);

        cell = new PdfPCell();
        disableBoundary(cell);
        cell.setPadding(0f);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setPaddingTop(3f);

        cell.addElement(new Paragraph(rightData, isGroupName ? BLACK_BOLD : BLACK_NORMAL));

        PdfPCell emptyCell = new PdfPCell();
        disableBoundary(emptyCell);
        emptyCell.setPadding(0f);
        emptyCell.setUseAscender(true);
        emptyCell.setUseDescender(true);
        emptyCell.setPaddingTop(3f);
        emptyCell.addElement(new Paragraph("  "));

        if (!isGroupName) {
            pdfPTable.addCell(cell);
            pdfPTable.addCell(emptyCell);
        } else {
            pdfPTable.addCell(emptyCell);
            pdfPTable.addCell(cell);
        }

        return pdfPTable;
    }

    private PdfPTable addTopRowParticular(float[] columnWeight, Date startDate, Date endDate) {
        PdfPTable pdfPTable = new PdfPTable(columnWeight);

        pdfPTable.setPaddingTop(4f);
        pdfPTable.setWidthPercentage(100);

        PdfPTable leftTable, rightTable;

        leftTable = new PdfPTable(new float[]{0.5f, 0.5f});
        rightTable = new PdfPTable(new float[]{0.5f, 0.5f});

        Font BLACK_BOLD = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BLACK);

        PdfPCell pdfPCell = new PdfPCell();
        disableBoundary(pdfPCell);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        pdfPCell.setPaddingTop(4f);
        pdfPCell.setPaddingBottom(4f);
        pdfPCell.addElement(new Paragraph("Particulars", BLACK_BOLD));

        leftTable.addCell(pdfPCell);
        rightTable.addCell(pdfPCell);

        String fromToDate = simpleDateFormat.format(startDate) + " to " + simpleDateFormat.format(endDate);

        Font BLACK_NORMAL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BLACK);

        pdfPCell = new PdfPCell();
        disableBoundary(pdfPCell);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        pdfPCell.setPaddingTop(4f);
        pdfPCell.setPaddingBottom(4f);
        pdfPCell.addElement(new Paragraph(fromToDate, BLACK_NORMAL));

        leftTable.addCell(pdfPCell);
        rightTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        enableBoundary(pdfPCell, TOP, BOTTOM, LEFT);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        pdfPCell.addElement(leftTable);

        pdfPTable.addCell(pdfPCell);

        pdfPCell = new PdfPCell();
        enableBoundary(pdfPCell, TOP, BOTTOM, RIGHT);
        pdfPCell.setPadding(0f);
        pdfPCell.setUseAscender(true);
        pdfPCell.setUseDescender(true);
        pdfPCell.addElement(rightTable);

        pdfPTable.addCell(pdfPCell);

        return pdfPTable;
    }

    private void addWatermark(PdfWriter writer) throws Exception {
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

    private PdfPTable addNameAndAddress(String title, Date startDate, Date endDate) {

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

        String fromToDate = simpleDateFormat.format(startDate) + " to " + simpleDateFormat.format(endDate);

        pdfPCell.addElement(addAboutCompany(title, false, false));
        pdfPCell.addElement(addAboutCompany(fromToDate, false, false));

        pdfPCell.setPaddingRight(60f);
        table.addCell(pdfPCell);
        return table;
    }

    private PdfPTable addAboutCompany(String content, boolean isCompanyName, boolean isHeader) {
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

    private void disableBoundary(PdfPCell pdfPCell) {
        pdfPCell.disableBorderSide(Rectangle.LEFT);
        pdfPCell.disableBorderSide(Rectangle.TOP);
        pdfPCell.disableBorderSide(Rectangle.RIGHT);
        pdfPCell.disableBorderSide(Rectangle.BOTTOM);
    }

    private void enableBoundary(PdfPCell pdfPCell, int... side) {
        disableBoundary(pdfPCell);
        for (int i = 0; i < side.length; i++) {
            switch (side[i]) {
                case TOP:
                    pdfPCell.enableBorderSide(Rectangle.TOP);
                    break;
                case BOTTOM:
                    pdfPCell.enableBorderSide(Rectangle.BOTTOM);
                    break;
                case LEFT:
                    pdfPCell.enableBorderSide(Rectangle.LEFT);
                    break;
                case RIGHT:
                    pdfPCell.enableBorderSide(Rectangle.RIGHT);
                    break;
            }
        }
    }

    private PdfPCell addCellContent(String content, boolean isHeading) {
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

    class MyFooter extends PdfPageEventHelper {
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