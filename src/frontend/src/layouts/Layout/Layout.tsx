import { Outlet, useNavigate, useSearchParams } from "react-router-dom"
import { Header } from "../components/Header/Header"
import { Footer } from "../components/Footer/Footer"
import "./Layout.css"
import { useEffect, useState } from "react"

export const Layout = () => {
    const navigate = useNavigate()

    const [searchParams, setSearchParams] = useSearchParams()
    
    const [isAccountVerified, setIsAccountVerified] = useState(searchParams.get("verified") !== null)

    useEffect(() => {
        if (isAccountVerified) {
            setTimeout(() => {
                setIsAccountVerified(false)
                navigate("/")
            }, 5000)
        }
    }, [])

    return (
        <div className="layout">
            <div className={`account-verified${isAccountVerified ? " account-verified-anim" : " account-verified-hidden"}`}>Account verified</div>
            <Header />
            <div className="layout-mid">
                <Outlet />
            </div>
            <Footer />
        </div>
    )
}