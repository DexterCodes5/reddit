import { useEffect, useState } from "react"
import axios from "axios"
import { Link, Outlet, useParams } from "react-router-dom"
import "./UserPage.css"
import { UserResponse } from "../../model/UserResponse"
import { ErrorPage } from "../ErrorPage/ErrorPage"

export const UserPage = () => {
    const { username } = useParams()
    const [user, setUser] = useState<UserResponse>()
    
    useEffect(() => {
        const getUser = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_API}/api/v1/users/${username}`)
                setUser(res.data)
            } catch (err) {
                console.error("Get User failed")
            }
        }

        getUser()
    }, [])

    if (!user) {
        return <ErrorPage />
    }

    return (
        <div className="container">
            <div className="user-page-top">
                <div className="user-page-top-left">
                    <img src={user?.img ? user.img : require("../../images/user.jpg")} alt="user" className="user-page-img" />
                    <h1 className="user-page-username">{user?.username}</h1>
                </div>
                <div className="user-page-top-right">
                    <button className="black-btn">Edit profile</button>
                </div>
            </div>
            <Outlet />
        </div>
    )
}