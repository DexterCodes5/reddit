import { useAuth } from "../../../../../../context/AuthContext"
import {ReactComponent as CloseIcon} from "../../../../../../icons/close.svg"

export const SuccessfulSignUp = () => {
    const auth = useAuth()

    return (
        <div className="modal">
            <div className="modal-padding">
                <CloseIcon className="modal-close" onClick={() => auth.setIsOpen(false)} />
                <h1>Successful sign up</h1>
                <p className="check-you-email-text">Check your email to verify your account.</p>
                <button className="auth-btn black-btn-long" onClick={() => auth.setIsOpen(false)}>OK</button>
            </div>
        </div>
    )
}