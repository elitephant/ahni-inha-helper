package kr.ac.inha.itlab.daegikim.models;

/**
 * Created with IntelliJ IDEA.
 * User: daegikim
 * Date: 9/28/13
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Major {
    private String major;       //전공명

    public Major(String major) {
        this.major = major;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
