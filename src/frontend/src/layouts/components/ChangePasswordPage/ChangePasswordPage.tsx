import { useState } from "react"
import "./ChangePasswordPage.css"
import { PASSWORD_REGEX } from "../../../util/RegexConstants"
import axios from "axios"
import { useNavigate, useSearchParams } from "react-router-dom"

type ChangePasswordType = {
    forgotPasswordCode: string | null,
    newPassword: string,
    confirmNewPassword: string
}

export const ChangePasswordPage = () => {
    const navigate = useNavigate()

    const [searchParams, setSearchParams] = useSearchParams()

    const [changePassword, setChangePassword] = useState<ChangePasswordType>({ forgotPasswordCode: searchParams.get("code"), newPassword: "", confirmNewPassword: ""})

    const [newPasswordError, setNewPasswordError] = useState("")
    const [confirmNewPasswordError, setConfirmNewPasswordError] = useState("")

    const changeChangePassword = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setChangePassword({...changePassword, [name]: value})
    }
    
    const callChangePassword = async () => {
        if (!PASSWORD_REGEX.test(changePassword.newPassword)) {
            setNewPasswordError("* Password should be at least 5 characters long and should contain a digit.")
            return
        } else {
            setNewPasswordError("")
        } 
        
        if (changePassword.newPassword !== changePassword.confirmNewPassword) {
            setConfirmNewPasswordError("* Passwords don't match.")
            return 
        } else {
            setConfirmNewPasswordError("")
        }

        try {
            await axios.patch(`${process.env.REACT_APP_API}/api/v1/auth/change-password`, { 
                forgotPasswordCode: changePassword.forgotPasswordCode,
                newPassword: changePassword.newPassword
            })
            navigate("/")
        } catch (err) {
            console.error("Call Change Password failed")
        }
    }

    return (
        <div className="container change-password-container1">
            <div className="change-password-container2">
                <h1 className="change-password-title">Change Password</h1>
                <p className="cp-label">New Password</p>
                <input type="password" className={`input${newPasswordError ? " input-error" : ""}`} name="newPassword" onChange={changeChangePassword} />
                {newPasswordError && <p className="input-info">{newPasswordError}</p>}
                <p className="cp-label">Confirm New Password</p>
                <input type="password" className={`input${confirmNewPasswordError ? " input-error" : ""}`} name="confirmNewPassword" onChange={changeChangePassword} />
                {confirmNewPasswordError && <p className="input-info">{confirmNewPasswordError}</p>}
                <button className="black-btn-long chpp-btn" onClick={callChangePassword}>Change password</button>
            </div>
        </div>
    )
}