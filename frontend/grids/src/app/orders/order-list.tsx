"use client";

import { useEffect, useState } from "react";
import { CANCEL, CANCEL_ORDER_ERROR_ALERT, EMPTY_ORDER_HISTORY, NOTHING_SELECTED_ALERT, SELECT_ALL, WON } from "../../constant";
import { OrderHistoryResponseDto, SelectableOrder } from "../../type/order";
import CheckBox from "../components/checkbox";
import { mapToSelectableOrders } from "../../util/rest.util";

export default function OrderList () {

    // TODO: fetch data - get order history API
    const originOrders: OrderHistoryResponseDto[] = [
        {
            orderId: 1,
            orderDate: "2025.09.23",
            orderStatus: "",
            totalPrice: 15000,
            shippingDetails: {
                email: "test@aa.com",
                address: "address",
                postCode: "01234",
            },
            orderItems: [{
                    itemId: 1,
                    orderItemId: 1,
                    itemName: "name1",
                    quantity: 2,
                    price: 5000,
                },
                {
                    itemId: 2,
                    orderItemId: 2,
                    itemName: "name2",
                    quantity: 2,
                    price: 3000,
                },
            ],
        },
        {
            orderId: 2,
            orderDate: "2025.09.21",
            orderStatus: "",
            totalPrice: 13000,
            shippingDetails: {
                email: "test@aa.com",
                address: "address",
                postCode: "01234",
            },
            orderItems: [{
                    itemId: 1,
                    orderItemId: 3,
                    itemName: "name1",
                    quantity: 2,
                    price: 5000,
                },
                {
                    itemId: 4,
                    orderItemId: 4,
                    itemName: "name3",
                    quantity: 1,
                    price: 8000,
                },
            ]
        }
    ];

    const [ orders, setOrders ] = useState<SelectableOrder[]>([]);

    useEffect(() => {
        // TODO: fetch data - get order history API
        setOrders(mapToSelectableOrders(originOrders));
    }, []);

    const onOrderChange = (orderId: number) => {
        setOrders((prev) =>
            prev.map((order) =>
            order.orderId === orderId
                ? {
                    ...order,
                    selected: !order.selected,
                    orderItems: order.orderItems.map((item) => ({
                    ...item,
                    selected: !order.selected,
                    })),
                }
                : order
            )
        );
    };

    const onOrderItemChange = (orderId: number, orderItemId: number) => {
        setOrders((prev) =>
            prev.map((order) =>
            order.orderId === orderId
                ? {
                    ...order,
                    orderItems: order.orderItems.map((item) =>
                    item.orderItemId === orderItemId
                        ? { ...item, selected: !item.selected }
                        : item
                    ),
                    selected: order.orderItems.every((item) =>
                    item.orderItemId === orderItemId ? !item.selected : item.selected
                    ),
                }
                : order
            )
        );
    };

    const getSelectedItemIds = () => {
        return orders.flatMap((order) =>
            order.orderItems
            .filter((item) => item.selected)
            .map((item) => item.orderItemId)
        );
    };

    const handleCancelOrders = async () => {
        const selectedIds = getSelectedItemIds();
        
        if (selectedIds.length === 0) {
            alert(NOTHING_SELECTED_ALERT);
            return;
        }
        
        try {
            // TODO: fetch API - cancel order API
            console.log(selectedIds);
            
        
            setOrders((prev) =>
                prev.map((order) => ({
                    ...order,
                    selected: false,
                    orderItems: order.orderItems
                        .filter((item) => !selectedIds.includes(item.orderItemId))
                        .map((item) => ({
                        ...item,
                        selected: false,
                    })),
                }))
            );
        } catch (err) {
            console.error(err);
            alert(CANCEL_ORDER_ERROR_ALERT);
        }
    };

    return (
        <ul className="m-4">
            {
                orders.length === 0 || orders.every(order => order.orderItems.length === 0) ? (
                    <p className="text-center text-gray-500 font-bold text-2xl">{EMPTY_ORDER_HISTORY}</p>
                ) : (
                    orders.map((order, index) => (
                        order.orderItems.length > 0 && (
                            <div key={`order-${order.orderId}`} className="mt-6 mb-10">
                                <span className="font-extrabold text-xl">
                                    {order.orderDate}
                                </span>
                                <div className="flex justify-between items-center mb-4 mt-2">
                                    <div className="flex items-center">
                                        <span className="font-bold text-lg">{SELECT_ALL}</span>
                                        <CheckBox className={"ml-4"} selected={order.selected} onChange={() => onOrderChange(order.orderId)}/>
                                    </div>
                                    <button className="bg-black rounded-sm text-white ml-auto pr-4 pl-4 pt-2 pb-2 mr-1" onClick={handleCancelOrders}>{CANCEL}</button>
                                </div>
                                {
                                    order.orderItems.map((item) => (
                                        <li key={`item-${item.orderItemId}`} className="grid grid-cols-2 grid-flow-col grid-rows-1 gap-4 border-1 border-gray-300 rounded-md m-1 p-2 items-center">
                                            <div className="row-span-1 flex flex-row">
                                                <CheckBox selected={item.selected} onChange={() => onOrderItemChange(order.orderId, item.orderItemId)}/>
                                                <span className="ml-4">{item.itemName}</span>
                                            </div>
                                            <span className="row-span-1 text-gray-400">
                                                {`${item.price}${WON}`}
                                            </span>
                                        </li>
                                    ))
                                }
                                {index < orders.length - 1 && (<hr className="border-t border-gray-400 my-8" />)}
                            </div>
                        )
                    ))
                )
            }
        </ul>
    )
}