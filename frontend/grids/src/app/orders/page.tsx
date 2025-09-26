"use client"

import { ORDER_HISTORY_TITLE } from "../../constant";
import OrderList from "./order-list";

export default function OrderHistory() {
    return (
        <div className="justify">
            <header className="flex justify-center items-center font-extrabold text-3xl mt-4">
                {ORDER_HISTORY_TITLE}
            </header>
            <div className="bg-white p-4 m-6">
                <OrderList />
            </div>
        </div>
    );
}