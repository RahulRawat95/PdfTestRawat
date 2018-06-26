package com.example.test.pdf.pdftest;

/**
 * Created by Admin on 1/30/2018.
 */
public class ProfitAndLossDatabaseModel {
    private String particular;
    private double EXPENSE;
    private long SUBGROUP_ID_N;

    public ProfitAndLossDatabaseModel(String particular, double EXPENSE, long SUBGROUP_ID_N) {
        this.particular = particular;
        this.EXPENSE = EXPENSE;
        this.SUBGROUP_ID_N = SUBGROUP_ID_N;
    }

    public String getParticular() {
        return particular;
    }

    public void setParticular(String particular) {
        this.particular = particular;
    }

    public double getEXPENSE() {
        return (EXPENSE);
    }

    public void setEXPENSE(double EXPENSE) {
        this.EXPENSE = EXPENSE;
    }

    public long getSUBGROUP_ID_N() {
        return SUBGROUP_ID_N;
    }

    public void setSUBGROUP_ID_N(long SUBGROUP_ID_N) {
        this.SUBGROUP_ID_N = SUBGROUP_ID_N;
    }
}
