package me.chyc.crawler.baixing;

/**
 * Created by yicun.chen on 10/20/14.
 */
public class HireInfo {
    private String href;
    private String company;
    private String phone;

    public HireInfo(String href, String company){
        this.href = href;
        this.company = company;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String toPhoneCompany(){
        return this.phone + "\t" + this.company;
    }
}
