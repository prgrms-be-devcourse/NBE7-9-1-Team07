import { fetchApi } from "./client";
import { ItemDto } from "../type/item";

const PATH = '/items';

class ItemService {
    async getItems(): Promise<ItemDto[]> {
        const response = await fetchApi(`${PATH}`);
        
        return response as ItemDto[];
    }
}

export const itemService = new ItemService();