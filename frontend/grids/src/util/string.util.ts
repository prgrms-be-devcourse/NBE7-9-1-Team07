export const checkEmailValidation = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    if (!emailRegex.test(email)) { // 입력값이 있고 정규식에 맞지 않을 때
        return false;;
    }

    return true;
}

export const checkZipCodeValidation = (zipCode: string): boolean => {
    const emailRegex = /^[0-9]+$/;
    
    if (!emailRegex.test(zipCode)) { // 입력값이 있고 정규식에 맞지 않을 때
        return false;;
    }

    return true;
}