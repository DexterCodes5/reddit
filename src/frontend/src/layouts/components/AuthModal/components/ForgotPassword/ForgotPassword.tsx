import { useState } from "react"
import { useAuth } from "../../../../../context/AuthContext"
import { ReactComponent as CloseIcon } from "../../../../../icons/close.svg"
import "./ForgotPassword.css"
import axios from "axios"

export const ForgotPassword: React.FC<{
    setForgotPassword: React.Dispatch<React.SetStateAction<boolean>>,
    setModalInfo: React.Dispatch<React.SetStateAction<string>>
}> = (props) => {
    const auth = useAuth()

    const [email, setEmail] = useState("")
    const [isEmailEmpty, setIsEmailEmpty] = useState(false)

    const [isLoading, setIsLoading] = useState(false)

    const sendVerificationEmail = async () => {
        if (email.length === 0) {
            setIsEmailEmpty(true)
            return
        }
        else {
            setIsEmailEmpty(false)
        }

        setIsLoading(true)
        try {
            await axios.post(`${process.env.REACT_APP_API}/api/v1/auth/forgot-password`, {email})
            props.setForgotPassword(false)
            props.setModalInfo("Password recovery email send.")
        } catch (err) {
            console.error("Send Verification Email failed")
            setIsLoading(false)
        }
    }

    return (
        <div className="modal">
            <div className="modal-padding">
                <img src={require("../../../../../icons/arrow-left.png")} alt="back" className="forgot-password-back"
                    onClick={() => props.setForgotPassword(false)} />
                <CloseIcon className="modal-close" onClick={() => auth.setIsOpen(false)} />
                <h1>Forgot password</h1>
                <div className="auth-input-row">
                    <p>Email</p>
                    <input type="text" className={`input${isEmailEmpty ? " input-error" : ""}`} name="email" onChange={e => setEmail(e.target.value)} />
                    {isEmailEmpty && <p className="input-info">* Enter email</p>}
                </div>
                <button className={`auth-btn black-btn-long${isLoading ? " black-btn-disabled" : ""}`} onClick={sendVerificationEmail}>
                    Password recovery
                </button>
            </div>
        </div>
    )
}