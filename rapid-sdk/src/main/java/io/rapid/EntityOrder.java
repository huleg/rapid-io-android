package io.rapid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Leos on 22.03.2017.
 */

class EntityOrder
{
	private List<EntityOrderDetail> mOrderList = new ArrayList<>();


	public void putOrder(String property, Sorting sorting)
	{
		mOrderList.add(new EntityOrderDetail(property, sorting));
	}


	public List<EntityOrderDetail> getOrderList()
	{
		return mOrderList;
	}


	public JSONArray toJson() throws JSONException
	{
		JSONArray array = new JSONArray();
		for(EntityOrderDetail order : mOrderList)
		{
			JSONObject object = new JSONObject();
			object.put(order.getProperty(), order.getSorting().getKey());
			array.put(object);
		}

		return array;
	}


	public static EntityOrder fromJson(JSONArray array)
	{
		if(array == null) return null;

		EntityOrder order = new EntityOrder();
		for(int i = 0; i < array.length(); i++)
		{
			JSONObject item = array.optJSONObject(i);
			String property = item.keys().hasNext() ? item.keys().next() : null;
			Sorting sorting = Sorting.fromKey(item.optString(property));
			order.putOrder(property, sorting);
		}
		return order;
	}
}