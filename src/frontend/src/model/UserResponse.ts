export class UserResponse {
    id: number
    username: string
    email: string
    img?: string
    
    constructor(id: number, username: string, email: string, img: string) {
        this.id = id
        this.username = username
        this.email = email
        this.img = img
    }
}