import { UserResponse } from "./UserResponse"

export class QuestionModel {
    id?: number
    title: string
    content: string
    username: string
    postTimestamp: string
    rating: number
    user: UserResponse

    constructor(title: string, content: string, username: string, id: number, postTimestamp: string, rating: number, user: UserResponse) {
        this.id = id
        this.title = title
        this.content = content
        this.username = username
        this.postTimestamp = postTimestamp
        this.rating = rating
        this.user = user
    }
}