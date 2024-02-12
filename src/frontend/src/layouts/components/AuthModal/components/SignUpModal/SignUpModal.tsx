import { useState } from "react"
import { useAuth } from "../../../../../context/AuthContext"
import { ReactComponent as CloseIcon } from "../../../../../icons/close.svg"
import { SignUpRequest } from "../../../../../model/SignUpRequest"
import { SuccessfulSignUp } from "./SuccessfulSignUp/SuccessfulSignUp"
import { EMAIL_REGEX, PASSWORD_REGEX, USERNAME_REGEX } from "../../../../../util/RegexConstants"

type SignUpType = {
    username: string
    password: string
    confirmPassword: string
    email: string
}

export const SignUpModal: React.FC<{ setSignInModal: React.Dispatch<React.SetStateAction<boolean>> }> = (props) => {
    const auth = useAuth()

    const [signUpData, setSignUpData] = useState<SignUpType>({ username: "", password: "", confirmPassword: "", email: "" })
    const [error, setError] = useState("")

    const [usernameError, setUsernameError] = useState(false)
    const [passwordError, setPasswordError] = useState(false)
    const [confirmPasswordError, setConfirmPasswordError] = useState(false)
    const [emailError, setEmailError] = useState(false)

    const [isLoading, setIsLoading] = useState(false)

    const [isSuccessfulSignUp, setIsSuccessfulSignUp] = useState(false)

    const changeSignUpRequest = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setSignUpData({ ...signUpData, [name]: value })
    }

    const inputBlur = (e: React.FocusEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        if (name === "username") {
            if (!USERNAME_REGEX.test(value)) {
                setUsernameError(true)
            } else {
                setUsernameError(false)
            }
        } else if (name === "password") {
            if (!PASSWORD_REGEX.test(value)) {
                setPasswordError(true)
            } else {
                setPasswordError(false)
            }
        } else if (name === "confirmPassword") {
            if (value !== signUpData.password) {
                setConfirmPasswordError(true)
            } else {
                setConfirmPasswordError(false)
            }
        } else if (name === "email") {
            if (!EMAIL_REGEX.test(value)) {
                setEmailError(true)
            } else {
                setEmailError(false)
            }
        }
    }

    const signUp = async (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault()

        if (usernameError || passwordError || confirmPasswordError || emailError) {
            return
        }

        try {
            setIsLoading(true)
            await auth.signUp(new SignUpRequest(signUpData.username, signUpData.password, signUpData.email))
            setIsSuccessfulSignUp(true)
        } catch (err: any) {
            setError(err.response.data.message)
            setIsLoading(false)
        }
    }

    if (!isSuccessfulSignUp) {
        return (
            <div className="modal">
                <div className="modal-padding">
                    {error && <div className="smwr">{error}</div>}
                    <CloseIcon className="modal-close" onClick={() => auth.setIsOpen(false)} />
                    <h1>Sign up</h1>
                    <form>
                        <div className="auth-input-row">
                            <p>Username</p>
                            <input type="text" className={`input${usernameError ? " input-error" : ""}`} name="username"
                                onChange={changeSignUpRequest} onBlur={inputBlur} />
                            {usernameError && <p className="input-info">* Username should be at least 5 characters long</p>}
                        </div>
                        <div className="auth-input-row">
                            <p>Password</p>
                            <input type="password" className={`input${passwordError ? " input-error" : ""}`} name="password"
                                onChange={changeSignUpRequest} onBlur={inputBlur} />
                            {passwordError &&
                                <p className="input-info">
                                    * Password should be at least 5 characters long and should contain a digit
                                </p>
                            }
                        </div>
                        <div className="auth-input-row">
                            <p>Confirm Password</p>
                            <input type="password" className={`input${confirmPasswordError ? " input-error" : ""}`} name="confirmPassword"
                                onChange={changeSignUpRequest} onBlur={inputBlur} />
                        </div>
                        <div className="auth-input-row">
                            <p>Email</p>
                            <input type="text" className={`input${emailError ? " input-error" : ""}`} name="email"
                                onChange={changeSignUpRequest} onBlur={inputBlur} />
                        </div>
                        <div>
                            <button className={`auth-btn black-btn-long${isLoading ? " black-btn-disabled" : ""}`} onClick={signUp}>
                                Sign up
                            </button>
                        </div>
                    </form>
                </div>
                <div className="sign-up-link-container">
                    <p className="sign-up-link" onClick={() => props.setSignInModal(true)}>Already have an account? Sign in</p>
                </div>
            </div>
        )
    } else {
        return <SuccessfulSignUp />
    }
}