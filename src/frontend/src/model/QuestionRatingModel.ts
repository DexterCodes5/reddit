export class QuestionRatingModel {
    id?: number
    upvote: boolean
    downvote: boolean
    userId: number
    questionId: number

    constructor(upvote: boolean, downvote: boolean, userId: number, questionId: number) {
        this.upvote = upvote
        this.downvote = downvote
        this.userId = userId
        this.questionId = questionId
    }
}