package com.grids.domain.item.service;

import com.grids.domain.item.default_Item_List.DefaultItemList;
import com.grids.domain.item.entity.Item;
import com.grids.domain.item.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ItemService {

    DefaultItemList defaultItemList = new DefaultItemList();
    List<Item> defaultItems = defaultItemList.makeItemList();

    private final ItemRepository itemRepository;

    //기본 상품 4가지 등록
    @PostConstruct// 이 어노테이션이 붙은 메서드는 빈 생성 후 자동 실행
    public void registerDefaultItems() {
        itemRepository.saveAll(defaultItems);
    }

}
