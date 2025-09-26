import { createContext, useContext, useState } from "react";

export interface UserContextType {
    email: string;
    setEmail: (email: string) => void;
}

export const UserContext = createContext<UserContextType>({
    email: "",
    setEmail: () => {},
});

export const UserProvider = ({ children }: { children: React.ReactNode }) => {
    const [email, setEmail] = useState("");
    return <UserContext.Provider value={{ email, setEmail }}>{children}</UserContext.Provider>
}

export const useEmail = () => {
    const context = useContext(UserContext);
    if (!context) throw new Error("useEmail must be used within EmailProvider");
    return context;
}