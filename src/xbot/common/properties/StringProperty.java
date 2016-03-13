/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xbot.common.properties;

import xbot.common.properties.Property.PropertyPersistenceType;

/**
 * A type of Property that manages a String value.
 * @author Sterling
 */
public class StringProperty extends Property {
    private String defaultValue;
    
    public StringProperty(String name, String defaultValue, XPropertyManager manager) {
        super(name, manager);
        this.defaultValue = defaultValue;
        load();
    }
    
    public StringProperty(String name, String defaultValue, PropertyPersistenceType persistenceType, XPropertyManager manager) {
        super(name, manager, persistenceType);
        this.defaultValue = defaultValue;
        loadIfPersistent();
    }    
    
    private void loadIfPersistent() {
        if (persistenceType == PropertyPersistenceType.Persistent) {
            load();
        }
        else {
            set(defaultValue);
        }
    }
    
    public String get() {
        return randomAccessStore.getString(key);
    }
    
    public void set(String value) {
        randomAccessStore.setString(key, value);
    }
    
    /**
     * We only save the property if it's from a persistent type
     */
    public void save() {
        if(persistenceType == PropertyPersistenceType.Persistent) {
            permanentStore.setString(key, randomAccessStore.getString(key));
        }
    }

    public void load() {
        String value = permanentStore.getString(key);
        if(value != null) {
            set(value);
        } else {
            set(defaultValue);
        }
    }
}
