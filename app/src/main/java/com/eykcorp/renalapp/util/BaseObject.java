package com.eykcorp.renalapp.util;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Created by galo.penaherrera on 15/12/2017.
 */

public class BaseObject {

    public void cargarEntidadconJson(JSONObject obj){
        if(obj==null){return;}
        Iterator<String> keys;
        keys=obj.keys();
        while(keys.hasNext()){
            String k=keys.next().toString();
            Field campo;
            try {
                campo = getClass().getDeclaredField(k);
                campo.setAccessible(true);
                campo.set(this, obj.opt(k));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

}
