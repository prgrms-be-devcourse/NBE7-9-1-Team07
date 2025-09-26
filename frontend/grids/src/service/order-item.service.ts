import { fetchApi } from "./client";
import { CancelOrderItemRequest, CancelOrderItemResponse } from "../type/order";

const PATH = 'v1/orderItems';

class OrderItemService {
    async cancelOrderItem(request: CancelOrderItemRequest): Promise<CancelOrderItemResponse> {
        const { orderItemIds } = request;

        const response = await fetchApi(`${PATH}`, {
            method: "DELETE",
            body: JSON.stringify({
                orderItemIds,
            })
        });

        return response.data;
    }
}

export const orderItemService = new OrderItemService();