export type OrderHistoryResponseDto = {
    orderId: number;
    orderDate: string;
    orderStatus: string;
    totalPrice: number;
    shippingDetails: ShippingDetail;
    orderItems: OrderItem[];
}

export type ShippingDetail = {
    email: string;
    address: string;
    postCode: string;
}

export type OrderItem = {
    itemId: number;
    orderItemId: number;
    itemName: string;
    quantity: number;
    price: number;
}

export type SelectableOrderItem = {
    itemId: number;
    orderItemId: number;
    itemName: string;
    quantity: number;
    price: number;
    selected: boolean;
};
  
export type SelectableOrder = {
    orderId: number;
    orderDate: string;
    orderStatus: string;
    totalPrice: number;
    shippingDetails: ShippingDetail;
    orderItems: SelectableOrderItem[];
    selected: boolean;
};