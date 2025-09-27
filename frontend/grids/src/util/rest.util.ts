import { OrderHistoryResponseDto, SelectableOrder } from "../type/order";

export const mapToSelectableOrders = (orders: OrderHistoryResponseDto[]): SelectableOrder[] => {
    return orders.map((order) => ({
        ...order,
        selected: false,
        orderItems: order.orderItems.map((item) => ({
        ...item,
        selected: false,
        })),
    }));
}