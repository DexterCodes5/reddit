import { JwtPayload, jwtDecode } from "jwt-decode";

interface DecodedToken extends JwtPayload {
    exp: number
}

export const isTokenExpired = (token: string) => {
    if (!token) {
        return true
    }

    try {
        const decodedToken: DecodedToken = jwtDecode(token)
        return decodedToken.exp < Date.now() / 1000
    } catch (err) {
        return true
    }
}