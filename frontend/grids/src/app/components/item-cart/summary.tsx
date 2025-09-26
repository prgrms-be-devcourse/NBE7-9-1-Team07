"use client";

import { useMemo, useState } from "react";
import { ADDRESS, ADDRESS_TAG, EMAIL, EMAIL_TAG, INVALID_EMAIL, INVALID_ZIP_CODE, NOTE_MESSAGE, PAYMENT, QUANTITY_MINUS, QUANTITY_PLUS, TOTAL_PRICE, UNIT, WON, ZIP_CODE, ZIP_CODE_TAG } from "../../../constant";
import { checkEmailValidation, checkZipCodeValidation } from "../../../util/string.util";
import { SummaryItem } from "../../../type/item";
import { useRouter } from "next/navigation";
import { orderService } from "../../../service";
import { useEmail } from "../../../context";

interface SummaryProps {
    readonly summaryItems: SummaryItem[];
    readonly setSummaryItems: React.Dispatch<React.SetStateAction<SummaryItem[]>>;
}

export default function Summary({ summaryItems, setSummaryItems }: SummaryProps) {
    const router = useRouter();

    const { email, setEmail } = useEmail();
    const [ emailError, setEmailError ] = useState<string>("");
    const [ address, setAddress ] = useState<string>("");
    const [ zipCode, setZipCode ] = useState<string>("");
    const [ zipCodeError, setZipCodeError ] = useState<string>("");

    const totalPrice = useMemo(() => {
        return summaryItems.reduce((acc, item) => acc + item.price * item.quantity, 0);
      }, [summaryItems]);

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault();
        const { name, value } = event.target;
        
        switch (name) {
            case EMAIL_TAG:
                setEmail(value);
                setEmailError(checkEmailValidation(value) ? "" : INVALID_EMAIL);
                break;
        
            case ADDRESS_TAG:
                setAddress(value);
                break;
        
            case ZIP_CODE_TAG:
                setZipCode(value);
                setZipCodeError(checkZipCodeValidation(value) ? "" : INVALID_ZIP_CODE);
                break;
        
            default:
                break;
        }
    };

    const handleIncreaseQuantity = (item: SummaryItem) => {
        setSummaryItems((items) =>
            items.map((it) =>
                it.id === item.id ? { ...it, quantity: it.quantity + 1 } : it
            )
        );
    };
      
    const handleDecreaseQuantity = (item: SummaryItem) => {
        setSummaryItems((items) =>
            items.map((it) =>
                it.id === item.id
                    ? { ...it, quantity: Math.max(it.quantity - 1, 1) }
                    : it
            )
        );
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        try {
            await orderService.createOrder({
                email,
                items: summaryItems.map((item) => ({
                    itemId: item.id,
                    quantity: item.quantity,
                })),
            });
        
            router.push(`/orders`);
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <>
            <ul className="m-4">
                {summaryItems.map((item) => (
                    <li key={item.id} className="flex flex-row items-center">
                        <div className="">
                            <span>{item.name}</span>
                        </div>
                        <div className="text-xs bg-black rounded-sm text-white pl-2 pr-2 ml-2">
                            <button className="pr-2" onClick={() => { handleDecreaseQuantity(item) }}>{QUANTITY_MINUS}</button>
                            <span>{`${item.quantity}${UNIT}`}</span>
                            <button className="pl-2" onClick={() => { handleIncreaseQuantity(item) }}>{QUANTITY_PLUS}</button>
                        </div>
                    </li>
                ))}
            </ul>
            <form className="flex flex-col h-full m-4" onSubmit={handleSubmit}>
                <span className="text-sm">{EMAIL}</span>
                <input className="bg-white border-white" type="text" name={EMAIL_TAG} value={email} onChange={handleInputChange}/>
                {emailError && <span className="text-red-500 text-xs">{emailError}</span>}
                <span className="mt-2 text-sm">{ADDRESS}</span>
                <input className="bg-white border-white" type="text" name={ADDRESS_TAG} value={address} onChange={handleInputChange}/>
                <span className="mt-2 text-sm">{ZIP_CODE}</span>
                <input className="bg-white border-white" type="text" name={ZIP_CODE_TAG} value={zipCode} onChange={handleInputChange}/>
                {zipCodeError && <span className="text-red-500 text-xs">{zipCodeError}</span>}
                <span className="mt-4">{NOTE_MESSAGE}</span>
                <div className="flex flex-row items-center justify-between mt-4 mb-4 pl-6 pr-6 font-extrabold">
                    <span>{TOTAL_PRICE}</span>
                    <span>{`${totalPrice}${WON}`}</span>
                </div>
                <button className="bg-black rounded-sm text-white p-3"  type="submit">{PAYMENT}</button>
            </form>
        </>
    );
}