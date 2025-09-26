"use client";

import { useState } from "react";
import { ITEM_LIST_TITLE, SUMMARY_TITLE } from "../../../constant";
import ItemList from "./item-list";
import Summary from "./summary";
import { SummaryItem } from "../../../type/item";

export default function ItemCart() {

    const [summaryItems, setSummaryItems] = useState<SummaryItem[]>([]);

    return (
        <div className="flex flex-row rounded-2xl shadow-xl/20 w-full h-full">
            <div className="bg-white rounded-l-2xl basis-3/5 pt-4">
                <span className="text-lg font-extrabold p-4">
                    {ITEM_LIST_TITLE}
                </span>
                <ItemList setSummaryItems={setSummaryItems}/>
            </div>
            <div className="rounded-l-2xl basis-2/5 pt-4">
                <span className="text-lg font-extrabold p-4">
                    {SUMMARY_TITLE}
                </span>
                <hr className="border-t border-gray-500 my-4 ml-4 mr-4" />
                <Summary summaryItems={summaryItems} setSummaryItems={setSummaryItems}/>
            </div>
        </div>
    );
}