 
"use client";

import { useCallback } from "react";

interface CheckBoxProps {
    readonly selected: boolean;
    readonly onChange: () => void;
    readonly className?: string;
}

export default function CheckBox({ className, selected, onChange }: CheckBoxProps) {

    const handleChange = useCallback(() => {
        onChange();
    }, [onChange, selected]);

    return (
        <input
            className={`w-7 h-7 ml-2
                appearance-none border-2 border-gray-400 rounded-sm 
                checked:bg-black
                checked:before:content-['✔'] 
                checked:before:text-white 
                checked:before:flex
                checked:before:items-center
                checked:before:justify-center
                ${className}`}
            checked={selected}
            type="checkbox"
            onChange={handleChange}
        />
    );
}