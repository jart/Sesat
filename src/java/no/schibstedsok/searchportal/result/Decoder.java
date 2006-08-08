package no.schibstedsok.searchportal.result;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: SSTHKJER
 * Date: 13.jan.2006
 * Time: 11:32:54
 * To change this template use File | Settings | File Templates.
 *
 * @todo make this a generic helper class for functions needed in templates
 *
 */


public class Decoder {

    public Decoder() {}

    public String yip_decoder(String s) {
        try {
            s = java.net.URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return s;
    }

    //function for infotext, must decode before stripping string
    public String yip_decoder(String s, int length) {
        try {
            s = java.net.URLDecoder.decode(s, "UTF-8");
            if (s.length() < length) {
                length = s.length();
                s = s.substring(0, length);
            } else {
                /* Make sure we are not cutting the string in the middle of a HTML tag. */
                if (s.indexOf("<",length) > s.indexOf(">", length)){
                    length = s.indexOf(">", length) + 1;
                    s = s.substring(0, length);
                } else {
	                s = s.substring(0, length) + "..";
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return s;
    }

    public String ypurl(String s) {

        if (!s.startsWith("http://")) {
            s = "http://" + s;
        }
        return s;
    }

    public boolean yip_checknames(String s1, String s2) {

        if (s1.equalsIgnoreCase(s2)) {
            return true;
        } else
	    return false;
    }

    public boolean checkInfoTextLength(String s, int i) {
        try {
            s = java.net.URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        int length = s.length();
        if (length > i)
            return true;

        return false;
    }

    //to be inserted in link to olympic tv-program
    public String dayOfMonth() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return Integer.toString(day);
    }
}