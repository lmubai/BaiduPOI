package model;

/**
 * Created by liyonglin on 2017/10/25.
 */
public class StoreModel {
    /**
     * 简化地址，新疆维吾尔自治区乌鲁木齐市天山区赛马场路
     */
    public String formatted_address = "";

    public String storeName = "";

    public String longitude = "";
    public String latitude = "";


    public String locationDetailURL;

    public Area areaInfo = null;
    public String tag = "";


    public StoreModel() {

    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StoreModel{");
        sb.append("formatted_address='").append(formatted_address).append('\'');
        sb.append(", storeName='").append(storeName).append('\'');
        sb.append(", longitude='").append(longitude).append('\'');
        sb.append(", latitude='").append(latitude).append('\'');
        sb.append(", locationDetailURL='").append(locationDetailURL).append('\'');
        sb.append(", areaInfo=").append(areaInfo);
        sb.append(", tag='").append(tag).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
