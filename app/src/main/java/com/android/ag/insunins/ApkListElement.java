package com.android.ag.insunins;

/**
 * Created by User on 16.03.2016.
 */
public class ApkListElement {

    String apk_name = null;
    boolean selected = false;

    public ApkListElement(String code, boolean selected) {
        super();
        this.apk_name = code;
        this.selected = selected;
    }

    public String getApk_name() {
        return apk_name;
    }
    public void setApk_name(String code) {
        this.apk_name = code;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
