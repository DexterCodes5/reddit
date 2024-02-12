import { useState } from "react"
import { ReactComponent as CloseIcon } from "../../../../icons/close.svg"
import { useAuth } from "../../../../context/AuthContext"
import axios from "axios"
import { useParams } from "react-router-dom"

export const WriteAnswerModal: React.FC<{
    setShowAnswerModal: React.Dispatch<React.SetStateAction<boolean>>,
    setGetAnswers: React.Dispatch<React.SetStateAction<boolean>>
}> = (props) => {
    const auth = useAuth()
    const { questionId } = useParams()

    const [answer, setAnswer] = useState("")
    const [answerError, setAnswerError] = useState(false)

    const [isLoading, setIsLoading] = useState(false)

    const postAnswer = async (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault()

        if (answer.length < 20) {
            setAnswerError(true)
            return
        }

        setIsLoading(true)
        try {
            const token = await auth.getAccessToken()
            await axios.post(`${process.env.REACT_APP_API}/api/v1/answers`,
                {
                    userId: auth.getUser()!.id,
                    questionId: parseInt(questionId!),
                    answer
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                })
            props.setShowAnswerModal(false)
            props.setGetAnswers(prevVal => !prevVal)
        } catch (err) {
            console.error("Post answer failed")
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="modal-background">
            <div className="modal">
                <div className="modal-padding">
                    <CloseIcon className="modal-close" onClick={() => props.setShowAnswerModal(false)} />
                    <h1>Write an answer</h1>
                    <form>
                        <div className="anqm-input-container">
                            <p>Answer</p>
                            <textarea className={`textarea${answerError ? " input-error" : ""}`} rows={4} name="content"
                                onChange={(e) => setAnswer(e.target.value)} />
                            {answerError && <p className="input-info">* Answer should be at least 20 characters.</p>}
                        </div>
                        <button className={`add-new-q-btn black-btn-long${isLoading ? " black-btn-disabled" : ""}`} onClick={postAnswer}>Answer question</button>
                    </form>
                </div>
            </div>
        </div>
    )
}