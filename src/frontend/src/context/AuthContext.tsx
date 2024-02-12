import { ReactNode, createContext, useContext, useState } from "react";
import { UserModel } from "../model/UserModel";
import axios from "axios";
import { AuthModal } from "../layouts/components/AuthModal/AuthModal";
import { SignInRequest } from "../model/SignInRequest";
import { useLocalStorage } from "../hooks/useLocalStorage";
import { SignUpRequest } from "../model/SignUpRequest";
import { isTokenExpired } from "../util/JwtUtil";


type User = {
    id: number
    username: string
    email: string
    img: string
}

type AuthContext = {
    setIsOpen: React.Dispatch<React.SetStateAction<boolean>>
    signIn: (signInRequest: SignInRequest) => void
    signUp: (signUpRequest: SignUpRequest) => void
    signOut: () => void
    isAuthenticated: () => boolean
    getAccessToken: () => Promise<string | null>
    getUser: () => User | null
    setUser: React.Dispatch<React.SetStateAction<UserModel | null>>
}

type AuthProviderProps = {
    children: ReactNode
}

const AuthContext = createContext({} as AuthContext)

export const useAuth = () => {
    return useContext(AuthContext)
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
    const [isOpen, setIsOpen] = useState(false)
    const [user, setUser] = useLocalStorage<UserModel | null>("user", null)
    console.log(user)
    const signIn = async (signInRequest: SignInRequest) => {
        const res = await axios.post(`${process.env.REACT_APP_API}/api/v1/auth/sign-in`, signInRequest)
        setUser(res.data)
    }
    
    const signUp = async (signUpRequest: SignUpRequest) => {
        await axios.post(`${process.env.REACT_APP_API}/api/v1/auth/sign-up`, signUpRequest)
    }

    const signOut = async () => {
        const token = await getAccessToken()
        await axios.post(`${process.env.REACT_APP_API}/api/v1/auth/sign-out`, null, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        setUser(null)
    }
    
    const refreshToken = async () => {
        if (!user) {
            return
        }
        const res = await axios.post(`${process.env.REACT_APP_API}/api/v1/auth/refresh-token`, null, {
            headers: {
                Authorization: `Bearer ${user.refreshToken}` 
            }
        })
        setUser(res.data)
        return res.data.accessToken
    }
    
    const getAccessToken = async () => {
        if (!user) {
            return null
        }
        let token = user.accessToken
        if (isTokenExpired(token)) {
            try {
                token = await refreshToken()
            } catch (err) {
                console.error("Refresh Token failed")
                setUser(null)
            }
        }
        return token
    }

    const isAuthenticated = () => {
        return user !== null
    }

    const getUser = () => {
        if (!user) {
            return null
        }
        return {
            id: user.id,
            username: user.username,
            email: user.email,
            img: user.img
        }
    }

    return (
        <AuthContext.Provider
            value={{
                setIsOpen,
                signIn,
                signUp,
                signOut,
                isAuthenticated,
                getAccessToken,
                getUser,
                setUser
            }}
        >
            {children}
            {isOpen && <AuthModal />}
        </AuthContext.Provider>
    )
}