import { useEffect, useState } from "react"
import { useParams } from "react-router-dom"
import axios from "axios"
import "./QuestionPage.css"
import { Answear } from "./components/Answear/Answear"
import { QuestionAnswerHeader } from "../components/QuestionAnswerHeader/QuestionAnswerHeader"
import { WriteAnswerModal } from "./components/WriteAnswerModal/WriteAnswerModal"
import { useAuth } from "../../context/AuthContext"
import { QuestionModel } from "../../model/QuestionModel"
import { AnswerModel } from "../../model/AnswerModel"
import { ErrorPage } from "../ErrorPage/ErrorPage"
import { ReactComponent as ArrowUp } from "../../icons/arrow-up.svg"
import { QuestionRatingModel } from "../../model/QuestionRatingModel"

export const QuestionPage = () => {
    const { questionId } = useParams()
    const auth = useAuth()

    const [error, setError] = useState(false)

    const [question, setQuestion] = useState<QuestionModel>()
    const [getQuestion, setGetQuestion] = useState(false)

    const [answers, setAnswers] = useState<AnswerModel[]>()
    const [getAnswers, setGetAnswers] = useState(false)

    const [showAnswerModal, setShowAnswerModal] = useState(false)

    const [userRating, setUserRating] = useState(() => {
        if (auth.isAuthenticated()) {
            return new QuestionRatingModel(false, false, auth.getUser()!.id, parseInt(questionId!))
        }
        return null
    })
    const [getUserRating, setGetUserRating] = useState(false)

    useEffect(() => {
        const getQuestion = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_API}/api/v1/questions/${questionId}`)
                setQuestion(res.data)
            } catch (err) {
                console.error("Get Question failed")
                setError(true)
            }
        }

        getQuestion()
    }, [getQuestion])

    useEffect(() => {
        const getAnswers = async () => {
            try {
                const res = await axios.get(`${process.env.REACT_APP_API}/api/v1/answers/${questionId}`)
                setAnswers(res.data)
            } catch (err) {
                console.error("Get Answers failed")
            }
        }

        getAnswers()
    }, [getAnswers])

    useEffect(() => {
        if (!auth.isAuthenticated()) {
            return
        }

        const getUserRating = async () => {
            try {
                const accessToken = await auth.getAccessToken()
                const res = await axios.get(`${process.env.REACT_APP_API}/api/v1/question-ratings/${questionId}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                })
                setUserRating(res.data)
            } catch (err) {
                console.error("Get User Rating failed")
            }
        }

        getUserRating()
    }, [getUserRating])
    
    const upvote = async (e: React.MouseEvent<HTMLDivElement>) => {
        if (!auth.isAuthenticated()) {
            auth.setIsOpen(true)
            return
        }
        userRating!.upvote = !userRating!.upvote
        userRating!.downvote = false
        await rate(userRating!)
    }

    const downvote = async (e: React.MouseEvent<HTMLDivElement>) => {
        if (!auth.isAuthenticated()) {
            auth.setIsOpen(true)
            return
        }
        userRating!.upvote = false
        userRating!.downvote = !userRating!.downvote
        await rate(userRating!)
    }

    const rate = async (questionRating: QuestionRatingModel) => {
        try {
            const accessToken = await auth.getAccessToken()
            await axios.post(`${process.env.REACT_APP_API}/api/v1/question-ratings`, questionRating,
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            })
            setGetQuestion(prevVal => !prevVal)
            setGetUserRating(prevVal => !prevVal)
        } catch (err) {
            console.error("Rate failed")
        }
    }

    if (error) {
        return (
            <ErrorPage />
        )
    }

    return (
        <div className="container">
            <div className="question-page-question">
                <QuestionAnswerHeader data={{
                    img: question?.user.img, username: question?.user.username!,
                    postTimestamp: question?.postTimestamp!
                }} />
                <div className="question-page-tc">
                    <h3 className="question-title">{question?.title}</h3>
                    <p className="question-content">{question?.content}</p>
                </div>
                <div className="question-page-question-footer">
                    <div className={userRating?.upvote ? "question-page-question-footer-btn-active" : "question-page-question-footer-btn"}
                            onClick={upvote}>
                        <ArrowUp className="question-arrow" />
                        <p>{question?.rating}</p>
                    </div>
                    <div className={userRating?.downvote ? "question-page-question-footer-btn-active" : "question-page-question-footer-btn"}
                            onClick={downvote}>
                        <ArrowUp className="question-arrow question-arrow-down" />
                    </div>
                </div>
            </div>
            {auth.isAuthenticated() &&
                <div className="question-page-answer-btn-container">
                    <button className="black-btn answer-btn" onClick={() => setShowAnswerModal(true)}>Answer</button>
                </div>
            }
            <div className="question-page-container">
                {answers?.map(a => <Answear answer={a} setGetAnswers={setGetAnswers} key={a.id} />)}
            </div>
            {showAnswerModal && <WriteAnswerModal setShowAnswerModal={setShowAnswerModal} setGetAnswers={setGetAnswers} />}
        </div>
    )
}