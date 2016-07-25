package prashushi.farcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dell User on 7/22/2016.
 */
public class ComboItem {
    String combo_id, combo_name, combo_cost, combo_thumbnail, percent_off, min_qty, combo_content;
    int buycount = 0;

    ComboItem(JSONObject combo) {
        combo_id = combo.optInt("combo_id") + "";
        combo_name = capitalize(combo.optString("name")) + "";
        combo_cost = combo.optString("cost") + "";
        combo_thumbnail = combo.optString("thumbnail") + "";
        percent_off = combo.optInt("percent_off") + "";
        min_qty = combo.optInt("combo_min_qty") + "";
        JSONArray array = combo.optJSONArray("items");
        combo_content = fetchString(array);

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

    private String fetchString(JSONArray array) {
        StringBuilder output = new StringBuilder();
        try {

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("item_name");
                String qty = obj.getString("item_qty");
                output.append(capitalize(name) + " " + qty + "Kg\n");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output.toString();
    }


    void id(String id) {
        combo_id = id;
    }

    String id() {
        return combo_id;
    }

    void name(String name) {
        combo_name = name;
    }

    String name() {
        return combo_name;
    }

    void thumbnail(String thumbnail) {
        combo_thumbnail = thumbnail;
    }

    String thumbnail() {
        return combo_thumbnail;
    }

    void percent_off(String percent_off) {
        this.percent_off = percent_off;
    }

    String percent_off() {
        return percent_off;
    }

    void min_qty(String min_qty) {
        this.min_qty = min_qty;
    }

    String min_qty() {
        return min_qty;
    }

    void cost(String cost) {
        combo_cost = cost;
    }

    String cost() {
        return combo_cost;
    }

    void buycount(int buycount) {
        this.buycount = buycount;
    }

    int buycount() {
        return buycount;
    }

    void content(JSONArray array) {
        combo_content = fetchString(array);
    }

    String content() {
        return combo_content;
    }
}
