package com.example.harish.b2bapplication.model;

/**
 * Created by harish on 15/3/16.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductJSONParser {


    public List<HashMap<String,String>> parse(JSONArray jsonArray){

        JSONArray jProduct = null;

            /** Retrieves all the elements in the 'product' array */
            jProduct = jsonArray;


        return getProducts(jProduct);
    }

    private List<HashMap<String, String>> getProducts(JSONArray jProducts){
        int productCount = jProducts.length();
        List<HashMap<String, String>> productList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> product = null;

        /** Taking each country, parses and adds to list object */
        for(int i=0; i<productCount;i++){
            try {
                /** Call getProduct with id JSON object to parse the product */
                product = getProduct((JSONObject) jProducts.get(i));
                productList.add(product);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return productList;
    }

    /** Parsing the Product JSON object */
    private HashMap<String, String> getProduct(JSONObject jProduct){

        HashMap<String, String> product = new HashMap<String, String>();
       // String flag="";
         String productDescription = "";
         String productName = "";

        try {
            productDescription = jProduct.getString("description");
            productName = jProduct.getString("name");

            product.put("description",productDescription);
            product.put("name", productName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return product;
    }
}