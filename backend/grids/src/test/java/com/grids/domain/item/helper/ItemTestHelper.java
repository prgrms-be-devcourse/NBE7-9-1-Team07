package com.grids.domain.item.helper;

import com.grids.domain.item.dto.ItemInfoUpdateRequestDto;
import com.grids.domain.item.entity.Item;

import java.lang.reflect.Field;

public final class ItemTestHelper {

    private ItemTestHelper() {
    }

    public static Item createItem(String name, Long price, String category, String image) {
        return Item.builder()
                .name(name)
                .price(price)
                .category(category)
                .image(image)
                .build();
    }

    /**
     * 테스트용 ItemInfoUpdateRequestDto 객체를 생성하는 메서드
     * 모든 필드 값을 파라미터로 받아 동적으로 DTO를 생성
     */
    public static ItemInfoUpdateRequestDto createItemInfoUpdateRequestDto(String name, Long price, String category, String image) {
        ItemInfoUpdateRequestDto dto = new ItemInfoUpdateRequestDto();
        try {
            setField(dto, "name", name);
            setField(dto, "price", price);
            setField(dto, "category", category);
            setField(dto, "image", image);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return dto;
    }

    private static void setField(Object targetObject, String fieldName, Object value) throws ReflectiveOperationException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, value);
    }

    public static void setEntityField(Object entity, String fieldName, Object value) throws ReflectiveOperationException {
        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(entity, value);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("'" + fieldName + "' 필드를 클래스 계층 구조에서 찾을 수 없습니다.");
    }
}