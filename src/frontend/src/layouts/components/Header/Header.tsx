import { Link } from "react-router-dom"
import { useAuth } from "../../../context/AuthContext"
import "./Header.css"
import { ReactComponent as UserIcon } from "../../../icons/user.svg"

export const Header = () => {
    const auth = useAuth()

    const signOut = async () => {
        try {
            await auth.signOut()
            window.location.reload()
        } catch (err) {
            console.error("Sign out failed")
        }
    }

    return (
        <header className="header">
            <div className="container">
                <div className="header-content">
                    <Link to="/">
                        <div className="logo">Reddit</div>
                    </Link>
                    <div className="header-user">
                        <UserIcon className="user-icon" />
                        <div className="header-user-dropdown">
                            <div className="header-user-dropdown1">
                                <div className="header-user-triangle"></div>
                                <div className="header-user-dropdown-content">
                                    {auth.isAuthenticated() ?
                                        <>
                                            <Link to={`/user/questions/${auth.getUser()?.username}`} className="header-user-dropdown-option">Account</Link>
                                            <div className="header-user-dropdown-btn" onClick={signOut}>Sign out</div>
                                        </>
                                        :
                                        <>
                                            <p className="header-user-dropdown-text">You are not in your account</p>
                                            <div className="header-user-dropdown-btn" onClick={() => auth.setIsOpen(true)}>Sign in</div>
                                        </>
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </header>
    )
}