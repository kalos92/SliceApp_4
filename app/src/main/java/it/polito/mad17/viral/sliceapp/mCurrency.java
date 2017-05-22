package it.polito.mad17.viral.sliceapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static io.fabric.sdk.android.services.concurrency.AsyncTask.init;

/**
 * Created by Kalos on 18/05/2017.
 */

public class mCurrency implements Serializable{

    private static final Map<String,String> currsym = new HashMap<>();
    private static final Map<String,Integer> special = new HashMap();

    public String getChoosencurr() {
        return choosencurr;
    }

    public void setChoosencurr(String choosencurr) {
        this.choosencurr = choosencurr;
    }

    private String choosencurr;
    private int digits;





    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    private String symbol;

    public mCurrency(){}
    private void Init(){
        currsym.put("ALL","Lek");
        currsym.put("AFN","؋");
        currsym.put("ARS","$");
        currsym.put("AWG", "ƒ");
        currsym.put("AUD", "$");
        currsym.put("AZN", "ман");
        currsym.put("BSD", "$");
        currsym.put("BBD", "$");
        currsym.put("BYN", "Br");
        currsym.put("BZD", "Bz$");
        currsym.put("BMD", "$");
        currsym.put("BOB","$b");
        currsym.put("BAM", "KM");
        currsym.put("BWP", "P");
        currsym.put("BGN", "лв");
        currsym.put("BRL", "R$");
        currsym.put("BND", "$");
        currsym.put("KHR", "៛");
        currsym.put("CAD", "$");
        currsym.put("KYD", "$");
        currsym.put("CLP", "$");
        currsym.put("CNY", "¥");
        currsym.put("COP", "$");
        currsym.put("CRC", "₡");
        currsym.put("HRK", "kn");
        currsym.put("CUP", "₱");
        currsym.put("CZK", "Kč");
        currsym.put("DKK", "kr");
        currsym.put("DOP", "RD$");
        currsym.put("XCD", "$");
        currsym.put("EGP", "£");
        currsym.put("SVC", "$");
        currsym.put("EUR", "€");
        currsym.put("FKP", "£");
        currsym.put("FJD", "$");
        currsym.put("GHS", "¢");
        currsym.put("GIP", "£");
        currsym.put("GTQ", "Q");
        currsym.put("GGP", "£");
        currsym.put("GYD", "$");
        currsym.put("HNL", "L");
        currsym.put("HKD", "$");
        currsym.put("HUF", "Ft");
        currsym.put("ISK", "kr");
        currsym.put("IDR", "Rp");
        currsym.put("IRR", "﷼");
        currsym.put("IMP", "£");
        currsym.put("ILS", "₪");
        currsym.put("JMD", "J$");
        currsym.put("JPY", "¥");
        currsym.put("JEP", "£");
        currsym.put("KZT", "лв");
        currsym.put("KPW", "₩");
        currsym.put("KRW", "₩");
        currsym.put("KGS", "лв");
        currsym.put("LAK", "₭");
        currsym.put("LBP", "£");
        currsym.put("LRD", "$");
        currsym.put("MKD", "ден");
        currsym.put("MYR", "RM");
        currsym.put("MUR", "₨");
        currsym.put("MXN", "$");
        currsym.put("MNT", "₮");
        currsym.put("MZN", "MT");
        currsym.put("NAD", "$");
        currsym.put("NPR", "₨");
        currsym.put("ANG", "ƒ");
        currsym.put("NZD", "$");
        currsym.put("NIO", "C$");
        currsym.put("NGN", "₦");
        currsym.put("NOK", "kr");
        currsym.put("OMR", "﷼");
        currsym.put("PKR", "₨");
        currsym.put("PAB", "B/.");
        currsym.put("PYG", "Gs");
        currsym.put("PEN", "S/.");
        currsym.put("PHP", "₱");
        currsym.put("PLN", "zł");
        currsym.put("QAR", "﷼");
        currsym.put("RON", "lei");
        currsym.put("RUB", "rub");
        currsym.put("SHP", "£");
        currsym.put("SAR", "﷼");
        currsym.put("RSD", "Дин.");
        currsym.put("SCR", "₨");
        currsym.put("SGD", "$");
        currsym.put("SBD", "$");
        currsym.put("SOS", "S");
        currsym.put("ZAR", "R");
        currsym.put("LKR", "₨");
        currsym.put("SEK", "kr");
        currsym.put("CHF", "CHF");
        currsym.put("SRD", "$");
        currsym.put("SYP", "£");
        currsym.put("TWD", "NT$");
        currsym.put("THB", "฿");
        currsym.put("TTD", "TT$");
        currsym.put("TVD", "$");
        currsym.put("UAH", "₴");
        currsym.put("GBP", "£");
        currsym.put("USD", "$");
        currsym.put("UYU", "$U");
        currsym.put("UZS", "лв");
        currsym.put("VEF", "Bs");
        currsym.put("VND", "₫");
        currsym.put("YER", "﷼");
        currsym.put("ZWD", "Z$");
        special.put("BHD", 3);
        special.put("BIF", 0);
        special.put("CLF", 4);
        special.put("CLP", 0);
        special.put("CVE", 0);
        special.put("DJF", 0);
        special.put("GNF", 0);
        special.put("IQD", 3);
        special.put("ISK", 0);
        special.put("JMD", 2);
        special.put("JPY", 0);
        special.put("KMF", 0);
        special.put("KRW", 0);
        special.put("KWD", 3);
        special.put("LYD", 3);
        special.put("MGA", 1);
        special.put("MRO", 1);
        special.put("OMR", 3);
        special.put("PYG", 0);
        special.put("RWF", 0);
        special.put("UGX", 0);
        special.put("UTI", 0);
        special.put("VND", 0);
        special.put("VUV", 0);
        special.put("XAF", 0);
        special.put("XOF", 0);
        special.put("XPF", 0);

    }

    public mCurrency(String curr){
        Init();
        StringTokenizer st = new StringTokenizer(curr,"-");
            choosencurr=st.nextToken();

        if(currsym.containsKey(choosencurr))
            symbol=currsym.get(choosencurr);
        else
            symbol="$";

        if(special.containsKey(choosencurr))
            digits=special.get(choosencurr);
        else
            digits=2;




    }



}
