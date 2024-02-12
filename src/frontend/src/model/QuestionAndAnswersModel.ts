import { AnswerModel } from "./AnswerModel"
import { UserResponse } from "./UserResponse"

export class QuestionAndAnswersModel {
    id?: number
    title: string
    content: string
    username: string
    postTimestamp: string
    user: UserResponse
    answers: AnswerModel[]

    constructor(title: string, content: string, username: string, id: number, postTimestamp: string, user: UserResponse, 
                answers: AnswerModel[]) {
        this.id = id
        this.title = title
        this.content = content
        this.username = username
        this.postTimestamp = postTimestamp
        this.user = user
        this.answers = answers
    }
}