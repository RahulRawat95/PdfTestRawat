package com.example.test.pdf.pdftest;

/**
 * Created by Admin on 2/7/2018.
 */
public class BalanceSheet {
    private long grpid;
    private double amt;
    private String grptype;
    private String grpname;

    public BalanceSheet() {
    }

    public BalanceSheet(long grpid, double amt, String grptype, String grpname) {
        this.grpid = grpid;
        this.amt = amt;
        this.grptype = grptype;
        this.grpname = grpname;
    }

    public long getGrpid() {
        return grpid;
    }

    public void setGrpid(long grpid) {
        this.grpid = grpid;
    }

    public double getAmt() {
        return (amt);
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public String getGrptype() {
        return grptype;
    }

    public void setGrptype(String grptype) {
        this.grptype = grptype;
    }

    public String getGrpname() {
        return grpname;
    }

    public void setGrpname(String grpname) {
        this.grpname = grpname;
    }
}
