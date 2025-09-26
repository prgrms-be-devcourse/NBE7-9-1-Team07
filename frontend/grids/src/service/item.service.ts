import { fetchApi } from "./client";
import { ItemDto } from "../type/item";

const PATH = 'v1/items';

class ItemService {
    async getItems(): Promise<ItemDto[]> {
        const response = await fetchApi(`${PATH}`);
        
        return response.data;
    }
}

export const itemService = new ItemService();