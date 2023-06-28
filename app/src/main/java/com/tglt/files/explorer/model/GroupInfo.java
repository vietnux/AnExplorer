package com.tglt.files.explorer.model;

import java.util.List;

import com.tglt.files.explorer.fragment.RootsFragment.*;

/**
 * Created by HaKr on 07/08/16.
 */

public class GroupInfo {
    public String label;
    public List<Item> itemList;

    public GroupInfo(String text, List<Item> list){
        label = text;
        itemList = list;
    }
}
