import axios from "axios"
import { useEffect, useState } from "react"
import { Link, useActionData, useParams } from "react-router-dom"
import { useAuth } from "../../context/AuthContext"
import { UserResponse } from "../../model/UserResponse"
import { ErrorPage } from "../ErrorPage/ErrorPage"

type MyQuestionType = {
    id: number,
    title: string,
    rating: number,
    answers: number
}

export const UserQuestionsPage = () => {
    const auth = useAuth()
    const { username } = useParams()

    const [user, setUser] = useState<UserResponse>()

    const [myQuestions, setMyQuestions] = useState<MyQuestionType[]>()

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

    useEffect(() => {
        const getMyQuestions = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_API}/api/v1/questions/get-questions-by-user/${username}`)
                setMyQuestions(res.data)
            } catch (err) {
                console.error("Get My Questions failed")
            }
        }

        getMyQuestions()
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
            <div className="user-page-mid">
                <ul className="user-page-mid-ul">
                    <Link to={`/user/questions/${username}`}>
                        <li className="user-page-option user-page-option-active">Questions</li>
                    </Link>
                    {auth.getUser()?.username == username &&
                        <Link to={`/user/settings/${username}`}>
                            <li className="user-page-option">Settings</li>
                        </Link>
                    }
                </ul>
                <div className="user-questions">
                    <h1>Questions</h1>
                    <div className="my-questions">
                        {myQuestions?.map(mq => (
                            <Link to={`/${mq.id}`} className="my-question" key={mq.id}>
                                <div className="my-question-top">
                                    <p>{mq.rating} votes</p>
                                    <p>{mq.answers} answers</p>
                                </div>
                                <p className="my-question-title">{mq.title}</p>
                            </Link>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}