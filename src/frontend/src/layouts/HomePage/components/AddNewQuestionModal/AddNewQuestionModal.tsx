import "./AddNewQuestionModal.css"
import { ReactComponent as CloseIcon } from "../../../../icons/close.svg"
import { useState } from "react"
import { useAuth } from "../../../../context/AuthContext"
import axios from "axios"

type QuestionRequest = {
    title: string
    content: string
    userId: number
}

export const AddNewQuestionModal: React.FC<{
    setShowAddNewQuestionModal: React.Dispatch<React.SetStateAction<boolean>>,
    setGetQuestions: React.Dispatch<React.SetStateAction<boolean>>
}> = (props) => {
    const auth = useAuth()

    const [question, setQuestion] = useState<QuestionRequest>({ title: "", content: "", userId: auth.getUser()!.id })

    const [titleError, setTitleError] = useState(false)
    const [contentError, setContentError] = useState(false)

    const [isLoading, setIsLoading] = useState(false)

    const changeQuestion = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target
        setQuestion({ ...question, [name]: value })
    }

    const postQuestion = async (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault()
        
        let failed = false
        if (question.title.length < 10) {
            setTitleError(true)
            failed = true
        } else {
            setTitleError(false)
        }
        if (question.content.length < 20) {
            setContentError(true)
            failed = true
        }
        else {
            setContentError(false)
        }
        if (failed) {
            return
        }

        try {
            setIsLoading(true)
            const token = await auth.getAccessToken()
            await axios.post(`${process.env.REACT_APP_API}/api/v1/questions`, question, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            props.setShowAddNewQuestionModal(false)
            props.setGetQuestions(prevVal => !prevVal)
        } catch (err) {
            console.error("Add new question failed")
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="modal-background">
            <div className="modal">
                <div className="modal-padding">
                    <CloseIcon className="modal-close" onClick={() => props.setShowAddNewQuestionModal(false)} />
                    <h1>Add new question</h1>
                    <form>
                        <div className="anqm-input-container">
                            <p>Title</p>
                            <input type="text" className={`input${titleError ? " input-error" : ""}`} name="title" onChange={changeQuestion} />
                            {titleError && <p className="input-info">* Title should be at least 10 characters.</p>}
                        </div>
                        <div className="anqm-input-container">
                            <p>Content</p>
                            <textarea className={`textarea${contentError ? " input-error" : ""}`} rows={4} name="content"
                                onChange={changeQuestion} />
                            {contentError && <p className="input-info">* Content should be at least 20 characters.</p>}
                        </div>
                        <button className="black-btn-long add-new-q-btn" onClick={postQuestion}>Add new question</button>
                    </form>
                </div>
            </div>
        </div>
    )
}