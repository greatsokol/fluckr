
package com.greatsokol.fluckr.models.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("secret")
    @Expose
    private String secret;
    @SerializedName("server")
    @Expose
    private String server;
    @SerializedName("farm")
    @Expose
    private Integer farm;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ispublic")
    @Expose
    private Integer ispublic;
    @SerializedName("isfriend")
    @Expose
    private Integer isfriend;
    @SerializedName("isfamily")
    @Expose
    private Integer isfamily;
    @SerializedName("description")
    @Expose
    private Description description;
    @SerializedName("url_t")
    @Expose
    private String urlT;
    @SerializedName("height_t")
    @Expose
    private Integer heightT;
    @SerializedName("width_t")
    @Expose
    private Integer widthT;
    @SerializedName("url_m")
    @Expose
    private String urlM;
    @SerializedName("height_m")
    @Expose
    private String heightM;
    @SerializedName("width_m")
    @Expose
    private String widthM;
    @SerializedName("url_n")
    @Expose
    private String urlN;
    @SerializedName("height_n")
    @Expose
    private String heightN;
    @SerializedName("width_n")
    @Expose
    private String widthN;
    @SerializedName("url_k")
    @Expose
    private String urlK;
    @SerializedName("url_b")
    @Expose
    private String urlB;
    @SerializedName("height_k")
    @Expose
    private String heightK;
    @SerializedName("width_k")
    @Expose
    private String widthK;
    @SerializedName("url_h")
    @Expose
    private String urlH;
    @SerializedName("height_h")
    @Expose
    private String heightH;
    @SerializedName("width_h")
    @Expose
    private String widthH;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getFarm() {
        return farm;
    }

    public void setFarm(Integer farm) {
        this.farm = farm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIspublic() {
        return ispublic;
    }

    public void setIspublic(Integer ispublic) {
        this.ispublic = ispublic;
    }

    public Integer getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(Integer isfriend) {
        this.isfriend = isfriend;
    }

    public Integer getIsfamily() {
        return isfamily;
    }

    public void setIsfamily(Integer isfamily) {
        this.isfamily = isfamily;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getUrlT() {
        return urlT;
    }

    public void setUrlT(String urlT) {
        this.urlT = urlT;
    }

    public Integer getHeightT() {
        return heightT;
    }

    public void setHeightT(Integer heightT) {
        this.heightT = heightT;
    }

    public Integer getWidthT() {
        return widthT;
    }

    public void setWidthT(Integer widthT) {
        this.widthT = widthT;
    }

    public String getUrlM() {
        return urlM;
    }

    public void setUrlM(String urlM) {
        this.urlM = urlM;
    }

    public String getHeightM() {
        return heightM;
    }

    public void setHeightM(String heightM) {
        this.heightM = heightM;
    }

    public String getWidthM() {
        return widthM;
    }

    public void setWidthM(String widthM) {
        this.widthM = widthM;
    }

    public String getUrlN() {
        return urlN;
    }

    public void setUrlN(String urlN) {
        this.urlN = urlN;
    }

    public String getHeightN() {
        return heightN;
    }

    public void setHeightN(String heightN) {
        this.heightN = heightN;
    }

    public String getWidthN() {
        return widthN;
    }

    public void setWidthN(String widthN) {
        this.widthN = widthN;
    }

    public String getUrlK() {
        return urlK;
    }

    public void setUrlK(String urlK) {
        this.urlK = urlK;
    }

    public String getHeightK() {
        return heightK;
    }

    public void setHeightK(String heightK) {
        this.heightK = heightK;
    }

    public String getWidthK() {
        return widthK;
    }

    public void setWidthK(String widthK) {
        this.widthK = widthK;
    }

    public String getUrlH() {
        return urlH;
    }
    public String getUrlB() {
        return urlB;
    }

    public void setUrlH(String urlH) {
        this.urlH = urlH;
    }

    public String getHeightH() {
        return heightH;
    }

    public void setHeightH(String heightH) {
        this.heightH = heightH;
    }

    public String getWidthH() {
        return widthH;
    }

    public void setWidthH(String widthH) {
        this.widthH = widthH;
    }

    public String getThumbnailUrl(){
        String url_n = getUrlN();
        String url_m = getUrlM();
        String url_t = getUrlT();
        if(url_n!=null)if(!url_n.equals(""))return url_n;
        if(url_m!=null)if(!url_m.equals(""))return url_m;
        if(url_t!=null)if(!url_t.equals(""))return url_t;
        return null;
    }

    public String getFullsizeUrl(){
        String url_k = getUrlK();
        String url_h = getUrlH();
        String url_b = getUrlB();
        if(url_k!=null)if(!url_k.equals(""))return url_k;
        if(url_h!=null)if(!url_h.equals(""))return url_h;
        if(url_b!=null)if(!url_b.equals(""))return url_b;
        return null;
    }
}
