"use client"

import { useEffect, useState } from "react";
import { ItemDto, SummaryItem } from "../../../type/item";
import { ADD_ITEM, WON } from "../../../constant";
import { itemService } from "../../../service";

interface ItemListProps {
    readonly setSummaryItems: React.Dispatch<React.SetStateAction<SummaryItem[]>>;
}

export default function ItemList({ setSummaryItems }: ItemListProps) {
    const [ items, setItems ] = useState<ItemDto[]>([]);

    useEffect(() => {
        const fetchItems = async () => {
            const data = await itemService.getItems();
            setItems(data);
        }

        fetchItems();
    }, []);

    const handleAddItemToCart = (item: ItemDto) => {
        setSummaryItems((prev) => {
            const exists = prev.find((it) => it.id === item.id);

            if (exists) {
                return prev.map((it) =>
                    it.id === item.id ? { ...it, quantity: it.quantity + 1 } : it
                );
            }
            
            return [...prev, { ...item, quantity: 1 }];
        });
    }

    return (
        <>
            <ul className="m-4">
                {items.map((item) => (
                    <li key={item.id} className="grid grid-flow-col grid-rows-1 gap-4 border-1 border-gray-300 m-1 p-2 items-center">
                        <div className="row-span-1">
                            <img src={item.image} width="40" height="40"/>
                        </div>
                        <div className="row-span-1 flex flex-col justify-center">
                            <span className="text-gray-400">{item.category}</span>
                            <span>{item.name}</span>
                        </div>
                        <span className="row-span-1">
                            {`${item.price}${WON}`}
                        </span>
                        <div className="row-span-1" onClick={() => { handleAddItemToCart(item) }}>
                            <button className="border-2 rounded-md p-2">
                                {ADD_ITEM}
                            </button>
                        </div>
                    </li>
                ))}
            </ul>
        </>
    );
}