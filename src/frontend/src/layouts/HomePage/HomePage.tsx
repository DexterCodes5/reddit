import { useEffect, useState } from "react"
import { QuestionModel } from "../../model/QuestionModel"
import { Question } from "./components/Question/Question"
import "./HomePage.css"
import axios from "axios"
import { AddNewQuestionModal } from "./components/AddNewQuestionModal/AddNewQuestionModal"
import { useAuth } from "../../context/AuthContext"
import { Pagination } from "../components/Pagination/Pagination"
import { useNavigate, useSearchParams } from "react-router-dom"
import { SpinnerLoading } from "../components/SpinnerLoading/SpinnerLoading"

export const HomePage = () => {
    const auth = useAuth()
    const navigate = useNavigate()

    const [searchParams, setSearchParams] = useSearchParams()
    const [currentPage, setCurrentPage] = useState(() => {
        return searchParams.get("page") ? parseInt(searchParams.get("page")!) : 1
    })
    const [questions, setQuestions] = useState<QuestionModel[]>([])
    const [showAddNewQuestionModal, setShowAddNewQuestionModal] = useState(false)
    const [getQuestions, setGetQuestions] = useState(false)

    const questionsPerPage = 10
    const [totalPages, setTotalPages] = useState(0)

    const [isLoading, setIsLoading] = useState(false)

    useEffect(() => {
        const getQuestions = async () => {
            const res = await axios.get(`${process.env.REACT_APP_API}/api/v1/questions/get-questions?page=${currentPage-1}&size=${questionsPerPage}`)
            setQuestions(res.data.content)
            setTotalPages(res.data.totalPages)
        }
        setIsLoading(true)
        getQuestions()
        setIsLoading(false)
    }, [getQuestions, currentPage])

    const paginate = (pageNum: number) => {
        navigate(`?page=${pageNum}`)
        setCurrentPage(pageNum)
        window.scrollTo(0, 0)
    }
    
    if (isLoading) {
        return (
            <SpinnerLoading />
        )
    }

    return (
        <div className="container">
            <h1 className="home-page-title">Questions</h1>
            {auth.isAuthenticated() &&
                <button className="black-btn ask-a-q-btn" onClick={() => setShowAddNewQuestionModal(true)}>Ask a question</button>
            }
            {questions.map(question => <Question question={question} setGetQuestions={setGetQuestions} key={question.id} />)}
            {totalPages > 1 && <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate} />}
            {showAddNewQuestionModal && <AddNewQuestionModal setShowAddNewQuestionModal={setShowAddNewQuestionModal}
                setGetQuestions={setGetQuestions} />}
        </div>
    );
}