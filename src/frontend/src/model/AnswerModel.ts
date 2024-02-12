import { UserResponse } from "./UserResponse"

export class AnswerModel {
    id: number
    content: string
    postTimestamp: string
    user: UserResponse
    rating: number

    constructor(id: number, content: string, postTimestamp: string, 
                user: UserResponse, rating: number) {
        this.id = id
        this.content = content
        this.postTimestamp = postTimestamp
        this.user = user
        this.rating = rating
    }
}