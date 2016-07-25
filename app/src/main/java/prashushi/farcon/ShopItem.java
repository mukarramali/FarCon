package prashushi.farcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Dell User on 7/22/2016.
 */
public class ShopItem {
    String item_id, item_name, item_cost, item_thumbnail, percent_off, min_qty, item_pkg_qty, offer;
    int flag_offer = 0;
    int buycount = 0;
    String hindi_name;
    JSONArray item_package;

    ShopItem(JSONObject item) {
        item_name = capitalize(item.optString("item_name")) + "";
        hindi_name = capitalize(item.optString("hindi_name")) + "";
        item_thumbnail = item.optString("image") + "";
        item_package = item.optJSONArray("package");
        JSONObject first = getPackageAt(0, item_package);
        item_id = first.optInt("id") + "";
        item_cost = first.optString("item_cost") + "";
        item_pkg_qty = removeZero(first.optString("package_qty")) + "";
        percent_off = first.optString("percent_off", "0") + "";
        if (percent_off.compareTo("null") == 0)
            percent_off = "0";
        min_qty = first.optString("item_min_qty", "0") + "";
        if (min_qty.compareTo("null") == 0)
            min_qty = "0";
        //flag: 0 nothing, 1:offer, 2:new price
        if ((min_qty.compareTo("0") == 0 || min_qty.compareTo("0") == 1) && Double.valueOf(percent_off) > 0.0) {
            flag_offer = 2;
            offer = String.format("%.0f", (1 - Double.valueOf(percent_off) / 100) * Double.valueOf(item_cost)) + "";

        } else if (Double.valueOf(percent_off) > 0.0) {
            flag_offer = 1;
//            item_offerTv.setText("Buy " + items.get(i).min_qty() + "Kg, Get " + items.get(i).percent_off() + "% Off");
            offer = "Buy " + min_qty + " packs, Get " + percent_off + "% Off";
        }
        //problem here, only offers taking from first element

    }

    private String removeZero(String package_qty) {
        double d = Double.valueOf(package_qty);
        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(d) + "";
    }

    private String capitalize(String name) {
        String st = name;
        if (name.length() == 0)
            return name;
        if (name.charAt(0) > 'Z') {
            st = (char) (name.charAt(0) - ('a' - 'A')) + name.substring(1);
        }
        return st;
    }

    private JSONObject getPackageAt(int i, JSONArray jsonArray) {
        JSONObject obj = new JSONObject();
        try {
            obj = jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    void id(String id) {
        item_id = id;
    }

    String id() {
        return item_id;
    }

    void name(String name) {
        item_name = name;
    }

    String name() {
        return item_name;
    }

    void hindi_name(String hindi_name) {
        hindi_name = hindi_name;
    }

    String hindi_name() {
        return hindi_name;
    }

    void pkg_qty(String pkg_qty) {
        item_pkg_qty = pkg_qty;
    }

    String pkg_qty() {
        return item_pkg_qty;
    }

    void thumbnail(String thumbnail) {
        item_thumbnail = thumbnail;
    }

    String thumbnail() {
        return item_thumbnail;
    }


    void percent_off(String percent_off) {
        this.percent_off = percent_off;
    }

    String percent_off() {
        if (percent_off.compareTo("null") == 0 || percent_off.length() == 0)
            return "0";
        return percent_off;
    }

    void min_qty(String min_qty) {
        this.min_qty = min_qty;
    }

    String min_qty() {
        if (min_qty.compareTo("null") == 0 || min_qty.length() == 0)
            return "0";
        return min_qty;
    }

    void offer(String min_qty) {
        this.min_qty = min_qty;
    }

    String offer() {
        if ((min_qty.compareTo("0") == 0 || min_qty.compareTo("0") == 1) && Double.valueOf(percent_off) > 0.0) {
            flag_offer = 2;
            offer = String.format("%.0f", (1 - Double.valueOf(percent_off) / 100) * Double.valueOf(item_cost)) + "";

        } else if (Double.valueOf(percent_off) > 0.0) {
            flag_offer = 1;
//            item_offerTv.setText("Buy " + items.get(i).min_qty() + "Kg, Get " + items.get(i).percent_off() + "% Off");
            offer = "Buy " + min_qty + " packs, Get " + percent_off + "% Off";
        }
        return offer;
    }


    void cost(String cost) {
        item_cost = cost;
    }

    String cost() {
        return item_cost;
    }

    //String new_cost(){
    //  if(flag_offer)
    //    return item_cost;
    //}

    void flag_offer(int flag) {
        flag_offer = flag;
    }

    int flag_offer() {
        return flag_offer;
    }

    void buycount(int buycount) {
        this.buycount = buycount;
    }

    int buycount() {
        return buycount;
    }

    JSONArray item_package() {
        return item_package;
    }

    void item_package(JSONArray item_package) {
        this.item_package = item_package;
    }
}
