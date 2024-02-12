import { Link } from "react-router-dom"
import { QuestionModel } from "../../../../model/QuestionModel"
import "./Question.css"
import { QuestionAnswerHeader } from "../../../components/QuestionAnswerHeader/QuestionAnswerHeader"
import { ReactComponent as ArrowUp} from "../../../../icons/arrow-up.svg"
import axios from "axios"
import { QuestionRatingModel } from "../../../../model/QuestionRatingModel"
import { useAuth } from "../../../../context/AuthContext"
import { useEffect, useState } from "react"

export const Question: React.FC<{ 
    question: QuestionModel, setGetQuestions: React.Dispatch<React.SetStateAction<boolean>>
}> = (props) => {
    const auth = useAuth()

    const [userRating, setUserRating] = useState(() => {
        if (auth.isAuthenticated()) {
            return new QuestionRatingModel(false, false, auth.getUser()!.id, props.question.id!)
        }
        return new QuestionRatingModel(false, false, 0, props.question.id!)
    })
    const [getUserRating, setGetUserRating] = useState(false)

    useEffect(() => {
        if (!auth.isAuthenticated()) {
            return
        }
        
        const getRating = async () => {
            try {
                const accessToken = await auth.getAccessToken()
                const res = await axios.get(`${process.env.REACT_APP_API}/api/v1/question-ratings/${props.question.id}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                })
                if (res.data) {
                    setUserRating(res.data)
                }
            } catch (err) {
                console.error("Get Rating failed")
            }
        }

        getRating()
    }, [getUserRating])
    
    const upvote = async (e: React.MouseEvent<HTMLDivElement>) => {
        e.preventDefault()
        if (!auth.isAuthenticated()) {
            auth.setIsOpen(true)
            return
        }
        userRating.upvote = !userRating.upvote
        userRating.downvote = false
        await rate(userRating)
    }

    const downvote = async (e: React.MouseEvent<HTMLDivElement>) => {
        e.preventDefault()
        if (!auth.isAuthenticated()) {
            auth.setIsOpen(true)
            return
        }
        userRating.upvote = false
        userRating.downvote = !userRating.downvote
        await rate(userRating)
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
            props.setGetQuestions(prevVal => !prevVal)
            setGetUserRating(prevVal => !prevVal)
        } catch (err) {
            console.error("Rate failed")
        }
    }

    return (
        <Link to={`/${props.question.id}`}>
            <div className="question">
                <div className="question-top">
                    <QuestionAnswerHeader data={{ img: props.question.user.img, username: props.question.user.username, 
                        postTimestamp: props.question.postTimestamp }} />
                    <div className="question-tc">
                        <h3 className="question-title">{props.question.title}</h3>
                        <p className="question-content">{props.question.content}</p>
                    </div>
                </div>
                <div className="question-footer">
                    <div className={`${userRating.upvote ? "question-footer-btn-active" : "question-footer-btn"}`} onClick={upvote}>
                        <ArrowUp className="question-arrow" style={{ color: "white", fill: "white" }} />
                        <p className="question-rating">{props.question.rating}</p>
                    </div>
                    <div className={`${userRating.downvote ? "question-footer-btn-active" : "question-footer-btn"}`} onClick={downvote}>
                        <ArrowUp className="question-arrow question-arrow-down" />
                    </div>
                    <div className="question-footer-btn">Answers</div>
                </div>
            </div>
        </Link>
    )
}