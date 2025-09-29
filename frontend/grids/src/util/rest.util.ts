import { OrderHistoryResponseDto, SelectableOrder } from "../type/order";

export const mapToSelectableOrders = (orders: OrderHistoryResponseDto[]): SelectableOrder[] => {
    return orders.map((order) => ({
      orderId: order.orderId,
      orderDate: order.orderName, // ✅ orderName → orderDate
      orderStatus: order.status,  // ✅ status → orderStatus
      totalPrice: order.totalPrice,
      shippingDetails: order.shippingDetails,
      selected: false,
      orderItems: order.orderItems.map((item) => ({
        itemId: item.itemId,
        orderItemId: item.orderItemId,
        itemName: item.orderItemName,      // ✅ orderItemName → itemName
        quantity: item.orderQuantity,      // ✅ orderQuantity → quantity
        price: item.orderPrice,            // ✅ orderPrice → price
        selected: false,
      })),
    }));
  };