package com.example.test.pdf.pdftest;

/**
 * Created by Admin on 1/31/2018.
 */
public class SalePurchaseSum {
    private double SALES;
    private double PURCHASE;
    private double CLOSINGSTOCK;

    public SalePurchaseSum() {
    }

    public SalePurchaseSum(double SALES, double PURCHASE, double CLOSINGSTOCK) {
        this.SALES = SALES;
        this.PURCHASE = PURCHASE;
        this.CLOSINGSTOCK = CLOSINGSTOCK;
    }

    public double getCLOSINGSTOCK() {
        return (CLOSINGSTOCK);
    }

    public void setCLOSINGSTOCK(double CLOSINGSTOCK) {
        this.CLOSINGSTOCK = CLOSINGSTOCK;
    }

    public double getSALES() {
        return (SALES);
    }

    public void setSALES(double SALES) {
        this.SALES = SALES;
    }

    public double getPURCHASE() {
        return (PURCHASE);
    }

    public void setPURCHASE(double PURCHASE) {
        this.PURCHASE = PURCHASE;
    }
}
