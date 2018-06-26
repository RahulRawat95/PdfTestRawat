package com.example.test.pdf.pdftest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[][] test = new String[150][];

        Random random = new Random();

        boolean hasInfinity = false;
        double number;
        for (int i = 0; i < 150; i++) {
            test[i] = new String[10];
            for (int j = 0; j < 10; j++) {
                number = Math.pow(2, random.nextInt(i * j + 10));
                if (!hasInfinity)
                    hasInfinity = Double.isInfinite(number);
                test[i][j] = String.valueOf(number);
            }
        }

        /*PdfTableCreator.createPdf("Title Comes Here", new Date(), new Date(), test, "test", this, true, hasInfinity, new PdfTableCreator.PdfProcessCompleted() {
            @Override
            public void pdfCreatedSuccessfully(boolean completed) {

            }
        });*/

        /*Intent printIntent = new Intent(this, PrintDialogActivity.class);
        printIntent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "test.pdf")), "application/pdf");
        printIntent.putExtra("title", "Nuthin' But a G Thang");
        startActivity(printIntent);*/

        /*if (Build.VERSION.SDK_INT >= 19) {

            PrintManager printManager = (PrintManager) this
                    .getSystemService(Context.PRINT_SERVICE);

            String jobName = this.getString(R.string.app_name) + " Day Book";


            printManager.print(jobName, new MyPrintDocumentAdapter(this, new File(Environment.getExternalStorageDirectory() + File.separator + "test.pdf")),
                    null);
        }*/

        /*List<ProfitAndLossDatabaseModel> profitAndLossDatabaseModels = new ArrayList<>();
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));
        profitAndLossDatabaseModels.add(new ProfitAndLossDatabaseModel("asd", 1241235, -1));

        List<SalePurchaseSum> salePurchaseSums = new ArrayList<SalePurchaseSum>();
        salePurchaseSums.add(new SalePurchaseSum(1234, 4321, 9876));

        new ProfitAndLossPdfCreator()
                .setLists(profitAndLossDatabaseModels, profitAndLossDatabaseModels.subList(1, 9)
                        , profitAndLossDatabaseModels.subList(1, 19), profitAndLossDatabaseModels.subList(1, 3)
                        , profitAndLossDatabaseModels.subList(6, 8), profitAndLossDatabaseModels.subList(1, 20)
                        , salePurchaseSums)
                .setSums(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000)
                .setGrossAndNet(-5890, 5890)
                .createPdf(new Date(), new Date(), "RecoTest", this, true, hasInfinity, new ProfitAndLossPdfCreator.PdfProcessCompleted() {
                    @Override
                    public void pdfCreatedSuccessfully(boolean completed) {

                    }
                });*/

        BalanceSheet balanceSheet = new BalanceSheet(100, 1002, "GroupType", "GroupName");
        ArrayList<ArrayList<BalanceSheet>> listOfList = new ArrayList<>();
        ArrayList<BalanceSheet> list = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            list.add(balanceSheet);

        ArrayList<ArrayList<BalanceSheet>> listOfList2 = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            listOfList2.add(list);

        for (int i = 0; i < 10; i++)
            listOfList.add(list);

        List<Double> sums = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            sums.add(1290D);

        new BalanceSheetPdfCreator()
                .setList(listOfList, listOfList2)
                .setListSums(1234, 4321)
                .setGroupSums(sums, sums.subList(0, 4))
                .createPdf(new Date(), new Date(), "RecoTest", this, true, false, new BalanceSheetPdfCreator.PdfProcessCompleted() {
                    @Override
                    public void pdfCreatedSuccessfully(boolean completed) {

                    }
                });

    }
}
