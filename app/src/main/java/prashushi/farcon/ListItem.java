package prashushi.farcon;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dell User on 6/21/2016.
 */public class ListItem {
    String item_id, item_name, item_cost, item_qty="0";

    public ListItem(String item, String name, String cost, String quantity) {
        item_id = item;
        item_name = name;
        item_cost = cost;
        item_qty = quantity;

    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("item_id", item_id);
            obj.put("item_name", item_name);
            obj.put("item_cost", item_cost);
            obj.put("item_qty", item_qty);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}