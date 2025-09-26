import { fetchApi } from "./client";
import { CreateOrderRequest, CreateOrderResponse, OrderHistoryResponseDto } from "../type/order";

const PATH = 'v1/orders';

class OrderService {
    async getOrders(email: string): Promise<OrderHistoryResponseDto[]> {
        const searchParams = new URLSearchParams();

        searchParams.append("email", email);

        const query = searchParams.toString();

        const response = await fetchApi(`${PATH}${query}`);
        
        return response.data;
    }

    async createOrder(request: CreateOrderRequest): Promise<CreateOrderResponse> {
        const { email, items } = request;

        const response = await fetchApi(`${PATH}`, {
            method: "POST",
            body: JSON.stringify({
                email,
                items,
            })
        });

        return response.data;
    }
}

export const orderService = new OrderService();