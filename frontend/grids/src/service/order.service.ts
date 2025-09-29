import { fetchApi } from "./client";
import { CreateOrderRequest, CreateOrderResponse, OrderHistoryResponseDto } from "../type/order";

const PATH = '/orders';

class OrderService {
    async getOrders(email: string): Promise<OrderHistoryResponseDto[]> {
        const searchParams = new URLSearchParams();

        searchParams.append("email", email);

        const query = searchParams.toString();

        const response = await fetchApi(`${PATH}?${query}`);
        
        return response as OrderHistoryResponseDto[];
    }

    async createOrder(request: CreateOrderRequest): Promise<CreateOrderResponse> {
        const { email, userAddress, userZipCode, orderItems } = request;

        const response = await fetchApi(`${PATH}`, {
            method: "POST",
            body: JSON.stringify({
                email,
                userAddress,
                userZipCode,
                orderItems,
            })
        });

        return response as CreateOrderResponse;
    }
}

export const orderService = new OrderService();