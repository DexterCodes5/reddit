import { Link, useParams } from "react-router-dom"
import "./UserSettings.css"
import { useAuth } from "../../context/AuthContext"
import { useState } from "react"
import axios from "axios"
import { ErrorPage } from "../ErrorPage/ErrorPage"

type EditProfileType = {
    img: File | null,
    username: string | undefined
}

export const UserSettingsPage = () => {
    const { username } = useParams()
    const auth = useAuth()

    const [editProfileData, setEditProfileData] = useState<EditProfileType>({ img: null, username: auth.getUser()?.username })

    const changeImage = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEditProfileData({ ...editProfileData, img: e.target.files![0] })
    }

    const saveProfile = async () => {
        if (!editProfileData.img) {
            return
        }
        const formData = new FormData()
        formData.append("image", editProfileData.img, editProfileData.img.name)
        formData.append("username", editProfileData.username!)
        try {
            const accessToken = await auth.getAccessToken()
            const res = await axios.patch(`${process.env.REACT_APP_API}/api/v1/users`, formData, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            })
            
            auth.setUser(res.data)
            window.location.href = `${process.env.REACT_APP_URL}/user/settings/${res.data.username}`
        } catch (err) {
            console.error("Save Profile failed")
        }
    }

    if (!auth.isAuthenticated() || auth.getUser()!.username !== username) {
        return <ErrorPage />
    }

    return (
        <div className="container">
            <div className="user-page-top">
                <div className="user-page-top-left">
                    <img src={auth.getUser()?.img} alt="user" className="user-page-img" />
                    <h1 className="user-page-username">{auth.getUser()?.username}</h1>
                </div>
                <div className="user-page-top-right">
                    <button className="black-btn">Edit profile</button>
                </div>
            </div>
            <div className="user-page-mid">
                <ul className="user-page-mid-ul">
                    <Link to={`/user/questions/${username}`}>
                        <li className="user-page-option">Questions</li>
                    </Link>
                    <Link to={`/user/settings/${username}`}>
                        <li className="user-page-option user-page-option-active">Settings</li>
                    </Link>
                </ul>
                <div className="user-page-settings">
                    <div>
                        <p>Personal information</p>
                        <ul className="user-page-settings-options">
                            <li className="user-page-settings-option user-page-settings-option-active">Edit profile</li>
                        </ul>
                    </div>
                    <div className="user-page-settings-mid">
                        <p className="settings-label">Profile image</p>
                        <div className="settings-img-container">
                            <img src={editProfileData.img ? URL.createObjectURL(editProfileData.img) : auth.getUser()!.img} alt="user"
                                className="settings-img" />
                            <div className="settings-change-picture">Change picture</div>
                            <input type="file" accept="image/*" className="settings-img-input" onChange={changeImage} />
                        </div>
                        <p className="settings-label">Username</p>
                        <input type="text" className="settings-input" value={editProfileData.username}
                            onChange={(e) => setEditProfileData({ ...editProfileData, username: e.target.value })} />
                        <button className="black-btn settings-save-btn" onClick={saveProfile}>Save profile</button>
                    </div>
                </div>
            </div>
        </div>
    )
}