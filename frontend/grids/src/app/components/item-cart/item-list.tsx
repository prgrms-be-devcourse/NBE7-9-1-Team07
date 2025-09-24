"use client"

import { useEffect, useState } from "react";
import { ItemDto, SummaryItem } from "../../../type/item";
import { ADD_ITEM, WON } from "../../../constant";

interface ItemListProps {
    setSummaryItems: React.Dispatch<React.SetStateAction<SummaryItem[]>>
}

export default function ItemList({ setSummaryItems }: ItemListProps) {
    const [ items, setItems ] = useState<ItemDto[]>([]);

    //TODO: fetch data
    useEffect(() => {
        setItems([{
            id: 1,
            name: "커피",
            category: "음료",
            price: 1000,
            image: "https://i.postimg.cc/05L7tfJr/Kakao-Talk-Photo-2025-09-10-16-13-04.png",
        },
        {
            id: 2,
            name: "top커피",
            category: "음료",
            price: 2000,
            image: "https://i.postimg.cc/05L7tfJr/Kakao-Talk-Photo-2025-09-10-16-13-04.png",
        }
    ]);
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