package com.grids.domain.item.default_Item_List;

import com.grids.domain.item.entity.Item;

import java.util.ArrayList;
import java.util.List;

public class DefaultItemList {
    String [] names = {
            "columbia narino",
            "brazil serra do caparao",
            "columbia quindio(white wine extended fermentation)",
            "ethiopia sidamo"
    };

    Long [] prices = {
            3200L,
            4500L,
            1800L,
            3600L
    };

    String [] categories = {
            "columbia",
            "brazil",
            "columbia",
            "ethiopia",
    };

    String [] imageUrls = {
            "이미지 URL 예시"
    };

    public List<Item> makeItemList() {

        List<Item> items = new ArrayList<>();

        for(int i = 0 ; i < 4 ; i++){
            items.add(new Item(names[i], prices[i], categories[i], imageUrls[0] ));
        }

        return items;
    }

}
