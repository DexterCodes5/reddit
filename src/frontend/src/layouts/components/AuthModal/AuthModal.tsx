import "./AuthModal.css"
import { ReactComponent as CloseIcon } from "../../../icons/close.svg"
import { useAuth } from "../../../context/AuthContext"
import { SignInRequest } from "../../../model/SignInRequest"
import { useEffect, useState } from "react"
import { SignUpModal } from "./components/SignUpModal/SignUpModal"
import { ForgotPassword } from "./components/ForgotPassword/ForgotPassword"

export const AuthModal = () => {
    const auth = useAuth()

    const [signInModal, setSignInModal] = useState(true)
    const [signInRequest, setSignInRequest] = useState<SignInRequest>(new SignInRequest("", ""))
    const [error, setError] = useState(false)
    const [isLoading, setIsLoading] = useState(false)

    const [forgotPassword, setForgotPassword] = useState(false)

    const [modalInfo, setModalInfo] = useState("")

    useEffect(() => {
        if (modalInfo) {
            setTimeout(() => setModalInfo(""), 5000)
        }
    }, [modalInfo])

    const changeSignInRequest = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setSignInRequest({ ...signInRequest, [name]: value })
    }

    const signIn = async (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault()
        setIsLoading(true)
        try {
            await auth.signIn(signInRequest)
            auth.setIsOpen(false)
            window.location.reload()
        } catch (err) {
            setError(true)
        } finally {
            setIsLoading(false)
        }
    }

    const render = () => {
        if (forgotPassword) {
            return <ForgotPassword setForgotPassword={setForgotPassword} setModalInfo={setModalInfo} />
        }
        else if (signInModal) {
            return (
                <div className="modal">
                    <div className="modal-padding">
                        <div className="modal-content">
                            {error && <div className="smwr">Incorrect username or password</div>}
                            <CloseIcon className="modal-close" onClick={() => auth.setIsOpen(false)} />
                            <h1>Sign in</h1>
                            <form>
                                <div className="auth-input-row">
                                    <p>Username</p>
                                    <input type="text" className={`input`} name="username" onChange={changeSignInRequest} />
                                </div>
                                <div className="auth-input-row">
                                    <p>Password</p>
                                    <input type="password" className={`input`} name="password"
                                        onChange={changeSignInRequest} />
                                    <p className="forgot-password" onClick={() => setForgotPassword(true)}>Forgot password?</p>
                                </div>
                                <button className={`auth-btn black-btn-long${isLoading ? " black-btn-disabled" : ""}`} onClick={signIn}>Sign in</button>
                            </form>
                        </div>
                    </div>
                    <div className="sign-up-link-container">
                        <p className="sign-up-link" onClick={() => setSignInModal(false)}>Don't have an account? Sign up</p>
                    </div>
                </div>
            )
        } else {
            return <SignUpModal setSignInModal={setSignInModal} />
        }
    }

    return (
        <div className="modal-background">
            {render()}
            <div className={`modal-info${!modalInfo ? " modal-info-hidden" : ""}`}>{modalInfo}</div>
        </div>
    )
}