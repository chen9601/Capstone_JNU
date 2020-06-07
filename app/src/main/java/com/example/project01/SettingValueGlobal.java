package com.example.project01;

public class SettingValueGlobal {
    private int setValue=0;
    public int getData()
    {
        return setValue;
    }
    public void setData(int data)
    {
        this.setValue = data;
    }
    private static SettingValueGlobal instance = null;

    public static synchronized SettingValueGlobal getInstance(){
        if(null == instance){
            instance = new SettingValueGlobal();
        }
        return instance;
    }

}
