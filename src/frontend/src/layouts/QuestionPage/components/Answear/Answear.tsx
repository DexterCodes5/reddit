import { AnswerModel } from "../../../../model/AnswerModel";
import { QuestionAnswerHeader } from "../../../components/QuestionAnswerHeader/QuestionAnswerHeader";
import "./Answer.css"
import { ReactComponent as ArrowUp } from "../../../../icons/arrow-up.svg"
import { useAuth } from "../../../../context/AuthContext";
import { useEffect, useState } from "react";
import { AnswerRatingModel } from "../../../../model/AnswerRatingModel";
import axios from "axios";

export const Answear: React.FC<{ 
    answer: AnswerModel, 
    setGetAnswers: React.Dispatch<React.SetStateAction<boolean>> 
}> = (props) => {
    const auth = useAuth()

    const [userAnswerRating, setUserAnswerRating] = useState<AnswerRatingModel | undefined>(() => {
        if (auth.isAuthenticated()) {
            return new AnswerRatingModel(false, false, auth.getUser()!.id, props.answer.id)
        }
        return undefined
    })
    
    useEffect(() => {
        const getUserAnswerRating = async () => {
            if (!auth.isAuthenticated()) {
                return
            }

            try {
                const accessToken = await auth.getAccessToken()
                const res = await axios.get(`${process.env.REACT_APP_API}/api/v1/answer-ratings/${props.answer.id}`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                })
                if (res.data) {
                    setUserAnswerRating(res.data)
                }
            } catch(err) {
                console.error("getUserAnswerRating failed")
            }
        }

        getUserAnswerRating()
    }, [])

    const upvote = async () => {
        if (!auth.isAuthenticated()) {
            auth.setIsOpen(true)
            return
        }
        
        userAnswerRating!.upvote = !userAnswerRating!.upvote
        userAnswerRating!.downvote = false
        await rate(userAnswerRating!)
    }

    const downvote = async () => {
        if (!auth.isAuthenticated()) {
            auth.setIsOpen(true)
            return
        }
        userAnswerRating!.upvote = false
        userAnswerRating!.downvote = !userAnswerRating!.downvote
        await rate(userAnswerRating!)
    }

    const rate = async (answerRating: AnswerRatingModel) => {
        try {
            const accessToken = await auth.getAccessToken()
            await axios.post(`${process.env.REACT_APP_API}/api/v1/answer-ratings`, answerRating,
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            })
            props.setGetAnswers(prevVal => !prevVal)
        } catch (err) {
            console.error("Rate failed")
        }
    }
    
    return (
        <div className="answer">
            <QuestionAnswerHeader data={{ img: props.answer.user.img, username: props.answer.user.username,
                postTimestamp: props.answer.postTimestamp }} />
            <p className="answer-content">{props.answer.content}</p>
            <div className="answer-footer">
                <div className={`${userAnswerRating?.upvote ? "answer-footer-btn-active" : "answer-footer-btn"}`}
                onClick={upvote}>
                    <ArrowUp className="question-arrow" />
                    <p>{props.answer.rating}</p>
                </div>
                <div className={`${userAnswerRating?.downvote ? "answer-footer-btn-active" : "answer-footer-btn"}`}
                onClick={downvote}>
                    <ArrowUp className="question-arrow question-arrow-down" />
                </div>
            </div>
        </div>
    )
}