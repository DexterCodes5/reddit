export class AnswerRatingModel {
    id?: number
    upvote: boolean
    downvote: boolean
    userId: number
    answerId: number

    constructor(upvote: boolean, downvote: boolean, userId: number, 
                answerId: number) {
        this.upvote = upvote
        this.downvote = downvote
        this.userId = userId
        this.answerId = answerId
    }
}